
// $Id: IndividualDataVolumePanel.java,v 1.6 2003/12/21 15:29:43 crisco Exp $
/* 
  This file is part of JIV.  
  Copyright (C) 2000, 2001 Chris A. Cocosco (crisco@bic.mni.mcgill.ca)

  JIV is free software; you can redistribute it and/or modify it under
  the terms of the GNU General Public License as published by the Free
  Software Foundation; either version 2 of the License, or (at your
  option) any later version.

  JIV is distributed in the hope that it will be useful, but WITHOUT
  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
  License for more details.

  You should have received a copy of the GNU General Public License
  along with JIV; if not, write to the Free Software Foundation, Inc.,
  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA, 
  or see http://www.gnu.org/copyleft/gpl.html
*/


package jiv;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;

/**
 * Provides the volume panel functionality specific to panels
 * displaying a single image volume.
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: IndividualDataVolumePanel.java,v 1.6 2003/12/21 15:29:43 crisco Exp $ 
 */
public final class IndividualDataVolumePanel extends DataVolumePanel {

    /** used for debugging the problem with non-working popup menu cmds */
    /*protected*/ static final boolean		DEBUG_POPUP_CMDS= false;

    /** initial "low" setting of the colormap controls slider */
    /*private*/ static final short		DEFAULT_CMAP_START= 0;
    /** initial "high" setting of the colormap controls slider */
    /*private*/ static final short		DEFAULT_CMAP_END= 255;
    /*private*/ static final int		DEFAULT_COLOR_CODING= ColorCoding.GRAY;
    /*private*/ static final boolean		INITIAL_CMAP_TIED_MODE= false;

    /*private*/ Data3DVolume 		data_volume; 
    /*private*/ final ColorCoding	color_coder= new ColorCoding();
    /*private*/ ColormapControl		cmap_control;
    /*private*/ ColormapDisplay		cmap_display;

    public IndividualDataVolumePanel( Data3DVolume data_volume, 
				      Container parent_container,
				      int grid_column,
				      Main applet_root
				      ) {
	this( data_volume, parent_container, grid_column, 
	      new Point3Dfloat(), true, false,
	      DEFAULT_CMAP_START, DEFAULT_CMAP_END, DEFAULT_COLOR_CODING, 
	      applet_root);
    }
    
    public IndividualDataVolumePanel( Data3DVolume data_volume, 
				      Container parent_container,
				      int grid_column,
				      Point3Dfloat initial_world_cursor,
				      boolean enable_world_coords,
				      boolean byte_voxel_values,
				      short initial_cmap_start,
				      short initial_cmap_end,
				      int color_coding_type,
				      Main applet_root
				      ) {
	// initialization done in the superclass (part 1/2)
	super( parent_container, grid_column, initial_world_cursor,
	       enable_world_coords, byte_voxel_values, 
	       applet_root);

	this.data_volume= data_volume;

	/* any specific (non-shared) popup menu commands can be added here:
	 */
	// Note: the ColormapControl constructor adds to popup_menu 
	cmap_control= new ColormapControl( initial_cmap_start, initial_cmap_end,
					   color_coding_type, INITIAL_CMAP_TIED_MODE,
					   popup_menu 
					   );
	Point3Dint initial_slices= CoordConv.world2voxel( initial_world_cursor);

	slice_producers= new SliceImageProducer[] {
		new TransverseSliceImageProducer( data_volume, initial_slices.z, 
						  cmap_control.getColormap()),
		new SagittalSliceImageProducer( data_volume, initial_slices.x, 
						cmap_control.getColormap()),
		new CoronalSliceImageProducer( data_volume, initial_slices.y, 
					       cmap_control.getColormap())
	};
	GridBagConstraints gbc= new GridBagConstraints();
	gbc.fill= GridBagConstraints.HORIZONTAL;
	gbc.weightx= 1.0;
	gbc.gridx= 0;
	gbc.gridy= 1;
	gbc.insets.top= 5;
	controls_panel.add( cmap_control, gbc);
	gbc.gridy= 2;
	gbc.insets.top= 4; gbc.insets.bottom= 2;
	gbc.insets.left= gbc.insets.right= 3;
	controls_panel.add( cmap_display= 
			    new ColormapDisplay( cmap_control.getColormap()), 
			    gbc);

	// hookup communication lines for colormap events:
	for( int i= 0; i < 3; ++i)
	    cmap_control.addColormapListener( (ColormapListener)(slice_producers[ i]));
	cmap_control.addColormapListener( cmap_display);

	popup_menu.addSeparator();
	// initialization done in the superclass (part 2/2)
	super._finish_initialization();
    }

    public SliceImageProducer getATransverseSliceImageProducer( int initial_slice) {
	
	SliceImageProducer ret= 
	    new TransverseSliceImageProducer( data_volume, 
					      initial_slice, 
					      cmap_control.getColormap());
	cmap_control.addColormapListener( ret);
	return ret;
    }

    public SliceImageProducer getASagittalSliceImageProducer( int initial_slice) {
	
	SliceImageProducer ret= 
	    new SagittalSliceImageProducer( data_volume, 
					    initial_slice, 
					    cmap_control.getColormap());
	cmap_control.addColormapListener( ret);
	return ret;
    }

    public SliceImageProducer getACoronalSliceImageProducer( int initial_slice) {
	
	SliceImageProducer ret= 
	    new CoronalSliceImageProducer( data_volume, 
					   initial_slice, 
					   cmap_control.getColormap());
	cmap_control.addColormapListener( ret);
	return ret;
    }

    final public int getXSize() { return data_volume.getXSize(); }
    final public int getYSize() { return data_volume.getYSize(); }
    final public int getZSize() { return data_volume.getZSize(); }

    final public String getTitle() { return data_volume.getNickName(); }

    /** aid to DataVolumePanel.CoordinateFields */
    protected final int _getVoxelValue( Point3Dint voxel_pos) {
	return data_volume.getVoxelAsInt( voxel_pos);
    }

    protected final float _image_byte2real( short voxel_value) {
	return data_volume.voxel2image( voxel_value);
    }

    protected final short _image_real2byte( float image_value) {
	return data_volume.image2voxel( image_value);
    }


    /** 
     * Support interface for the inner class
     * <code>IndividualDataVolumePanel.ColormapControl</code>. 
     *
     * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
     * @version $Id: IndividualDataVolumePanel.java,v 1.6 2003/12/21 15:29:43 crisco Exp $ 
     *
     * @see IndividualDataVolumePanel.ColormapControl 
     */
    interface ColormapControlMenus {

	/** label-value pairs to be used in the "color coding" menu */
	Object[][]	color_codings= {
	    { "grey", new Integer( ColorCoding.GREY) },
	    { "hotmetal", new Integer( ColorCoding.HOTMETAL) },
	    { "spectral", new Integer( ColorCoding.SPECTRAL) },
	    { "red", new Integer( ColorCoding.RED) },
	    { "green", new Integer( ColorCoding.GREEN) },
	    { "blue", new Integer( ColorCoding.BLUE) },
	    { "MNI labels", new Integer( ColorCoding.MNI_LABELS) }
	};
	/** label-value pairs to be used in the "under/over color" menues */
	Object[][]	basic_colors= {
	    { "(default)", null },
	    { "black", Color.black },
	    { "blue", Color.blue },
	    { "cyan", Color.cyan },
	    { "dark gray", Color.darkGray },
	    { "gray", Color.gray },
	    { "green", Color.green },
	    { "light gray", Color.lightGray },
	    { "magenta", Color.magenta },
	    { "orange", Color.orange },
	    { "pink", Color.pink },
	    { "red", Color.red },
	    { "yellow", Color.yellow },
	    { "white", Color.white }
	};
    } 
    /** 
     * Member (inner) class : the user interface input
     * controls for adjusting the colormap.
     *
     * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
     * @version $Id: IndividualDataVolumePanel.java,v 1.6 2003/12/21 15:29:43 crisco Exp $ 
     *
     * @see IndividualDataVolumePanel.ColormapControlMenus 
     */
    /*private*/ final class ColormapControl extends LightweightPanel
		    implements ColormapControlMenus, ActionListener, AdjustmentListener {

	/*private*/ IndexColorModel crt_colormap;

	/*private*/ int		lower_value;
	/*private*/ TextField 	lower_value_tf;
	/*private*/ Scrollbar 	lower_value_sb;
	/*private*/ int		upper_value;
	/*private*/ TextField 	upper_value_tf;
	/*private*/ Scrollbar 	upper_value_sb;

	/*private*/ boolean	tied_mode; 

	/*private*/ ChoiceMenu	color_coding_menu;
	/*private*/ ChoiceMenu	under_color_menu;
	/*private*/ ChoiceMenu	over_color_menu;

	/*private*/ Vector 		event_listeners;
	/*private*/ GridBagConstraints 	gbc= new GridBagConstraints();

	/* these are exclusively for internal use: */
	/*private*/ int 	_old_lower_value;   
	/*private*/ int 	_old_upper_value;
	/*private*/ int 	_old_coding_type;
	/*private*/ Color 	_old_under_color;
	/*private*/ Color 	_old_over_color;

	protected ColormapControl( int initial_lower_value, 
				   int initial_upper_value,
				   int color_coding_type,
				   boolean initial_tied_mode,
				   PopupMenu popup_menu       ) {
	    
	    tied_mode= initial_tied_mode;

	    ActionListener al= new ActionListener() {
		public void actionPerformed( ActionEvent ae) {
		    ColormapControl.this.changeColormap();
		}
	    };
	    /* determine the string label of the menu entry
	       corresponding to 'color_coding_type' */
	    int i;
	    for( i= 0; i < color_codings.length; ++i) 
		if( color_coding_type == ( (Integer)color_codings[ i][ 1] ).intValue() )
		    break;
	    // if we didn't find it, then i == color_codings.length and Java
	    // will throw an ArrayIndexOutOfBoundsException on the following line:
	    String default_color_coding= (String)color_codings[ i][ 0];
	    color_coding_menu= 
		new ChoiceMenu( "Color coding",
				color_codings, default_color_coding, al); 
	    popup_menu.add( color_coding_menu);
	    under_color_menu= 
		new ChoiceMenu( "\"Under\" color", 
				basic_colors, (String)basic_colors[0][0], al);
	    popup_menu.add( under_color_menu);
	    over_color_menu= 
		new ChoiceMenu( "\"Over\" color",
				basic_colors, (String)basic_colors[0][0], al);
	    popup_menu.add( over_color_menu);
	    CheckboxMenuItem tied_cmi= new CheckboxMenuItem( "Tie colormap sliders", 
							     INITIAL_CMAP_TIED_MODE);
	    tied_cmi.addItemListener( new ItemListener() {
		public synchronized void itemStateChanged( ItemEvent e) {
		    if( DEBUG_POPUP_CMDS) 
			System.out.println( "Tie colormap sliders: itemStateChanged(): " + e);
		    switch( e.getStateChange()) {
		    case ItemEvent.SELECTED:
			ColormapControl.this.setTiedMode( true); 
			if( DEBUG_POPUP_CMDS) 
			    System.out.println( "setting tied mode to true...");
			break;
		    case ItemEvent.DESELECTED:
			ColormapControl.this.setTiedMode( false); 
			if( DEBUG_POPUP_CMDS) 
			    System.out.println( "setting tied mode to false...");
			break;
		    }
		}
	    });
	    popup_menu.add( tied_cmi);

	    if( initial_lower_value > initial_upper_value)
		throw new 
		    IllegalArgumentException( "initial_cmap_start > initial_cmap_end");
	    crt_colormap= color_coder.get8bitColormap( color_coding_type, 
						       initial_lower_value, 
						       initial_upper_value );
	    _old_lower_value= lower_value= initial_lower_value;
	    _old_upper_value= upper_value= initial_upper_value;
	    _old_coding_type= ( (Integer)color_coding_menu.getSelection() ).intValue();
	    _old_under_color= (Color)under_color_menu.getSelection();
	    _old_over_color= (Color)over_color_menu.getSelection();

	    lower_value_tf= new TextField( _voxel2string( lower_value), 
					   byte_voxel_values ? 
					   3 : 
					   IMAGE_VALUE_TEXTFIELD_WIDTH);
	    upper_value_tf= new TextField( _voxel2string( upper_value), 
					   byte_voxel_values ? 
					   3 : 
					   IMAGE_VALUE_TEXTFIELD_WIDTH);
	    lower_value_sb= _new_scrollbar( lower_value);
	    upper_value_sb= _new_scrollbar( upper_value);

	    setLayout( new GridBagLayout());
	    _add_comp( lower_value_tf, 0, 0, 2);
	    _add_comp( upper_value_sb, 1, 0, 1);
	    _add_comp( lower_value_sb, 1, 1, 1);
	    _add_comp( upper_value_tf, 2, 0, 2);

	    lower_value_tf.addActionListener( this);
	    upper_value_tf.addActionListener( this);
	    lower_value_sb.addAdjustmentListener( this);
	    upper_value_sb.addAdjustmentListener( this);
	}

	/** convenience (helper) method */
	/*private*/ Scrollbar _new_scrollbar( int initial_value) {
	    Scrollbar sb= new Scrollbar( Scrollbar.HORIZONTAL);
	    int visible= sb.getVisibleAmount();
	    sb.setMinimum( 0);
	    /* the value is read on the left side of the slider, hence
	     we need to correct the Max if we want the scrollbar to
	     return values up to 255 inclusive... */
	    sb.setMaximum( 255+visible);
	    sb.setValue( initial_value);
	    return sb;
	}

	/** convenience (helper) method */
	/*private*/ void _add_comp( Component what, int gx, int gy, int row_span) {
	    gbc.gridx= gx; 
	    gbc.gridy= gy; 
	    gbc.gridheight= row_span;
	    gbc.weightx= ( what instanceof Scrollbar) ? 1f : 0f;
	    gbc.fill= GridBagConstraints.HORIZONTAL;
	    add( what, gbc);
	}

	public final IndexColorModel getColormap() { return crt_colormap; }

	synchronized public final void actionPerformed( ActionEvent ae) {

	    if( DEBUG) System.out.println( ae);
	    if( ae.getID() != ActionEvent.ACTION_PERFORMED)
		return;
	    TextField source= (TextField)ae.getSource();
	    if( source != lower_value_tf && source != upper_value_tf) {
		// neither of the 2 Scrollbar-s is the source of this event...
		throw new IllegalArgumentException( "unexpected source:" + ae);
	    }
	    int new_value;
	    if( !tied_mode) {
		if( source == lower_value_tf) {
		    try { 
			new_value= _string2voxel( ae.getActionCommand());
			if( new_value < 0)
			    throw new NumberFormatException( "out of range...");
			if( new_value > upper_value)
			    new_value= upper_value;
		    }
		    catch( NumberFormatException exception) { 
			new_value= lower_value; // revert to previous (valid) value
		    }
		    lower_value_sb.setValue( lower_value= new_value);
		}
		else { // we know that (source == upper_value_tf)
		    try { 
			new_value= _string2voxel( ae.getActionCommand());
			if( new_value > 255)
			    throw new NumberFormatException( "out of range...");
			if( new_value < lower_value)
			    new_value= lower_value;
		    }
		    catch( NumberFormatException exception) { 
			new_value= upper_value; // revert to previous (valid) value
		    }
		    upper_value_sb.setValue( upper_value= new_value);
		}
		source.setText( _voxel2string( new_value));
		changeColormap();
		return;
	    }
	    // tied_mode is ON
	    final int diff= upper_value - lower_value;
	    try { 
		new_value= _string2voxel( ae.getActionCommand());
		if( new_value < 0 || new_value > 255)
		    throw new NumberFormatException( "out of range...");

		if( source == lower_value_tf) 
		    if( new_value + diff > 255) {
			lower_value= 255 - diff;
			upper_value= 255;
		    }
		    else {
			lower_value= new_value;
			upper_value= new_value + diff;
		    }
		else  // we know that (source == upper_value_tf)
		    if( new_value - diff < 0) {
			lower_value= 0;
			upper_value= diff;
		    }
		    else {
			lower_value= new_value - diff;
			upper_value= new_value;
		    }
	    }
	    catch( NumberFormatException exception) { 
		// keep the previous (valid) values
	    }
	    lower_value_tf.setText( _voxel2string( lower_value));
	    upper_value_tf.setText( _voxel2string( upper_value));
	    lower_value_sb.setValue( lower_value);
	    upper_value_sb.setValue( upper_value);
	    changeColormap();
	}

	synchronized public final void adjustmentValueChanged( AdjustmentEvent ae) {

	    if( DEBUG) System.out.println( ae);
	    final Scrollbar source= (Scrollbar)ae.getSource();
	    if( source != lower_value_sb && source != upper_value_sb) {
		// neither of the 2 Scrollbar-s is the source of this event...
		throw new IllegalArgumentException( "unexpected source:" + ae);
	    }
	    int new_value= ae.getValue();
	    if( new_value < 0 )
		source.setValue( new_value= 0);
	    else if( new_value > 255)
		source.setValue( new_value= 255);

	    if( !tied_mode) {
		if( source == lower_value_sb) {
		    if( new_value > upper_value)
			lower_value_sb.setValue( new_value= upper_value);
		    lower_value_tf.setText( _voxel2string( lower_value= new_value));
		}
		else {	// we know that (source == upper_value_sb)
		    if( new_value < lower_value)
			upper_value_sb.setValue( new_value= lower_value);
		    upper_value_tf.setText( _voxel2string( upper_value= new_value));
		}
		changeColormap();
		return;
	    }	
	    // tied_mode is ON
	    final int diff= upper_value - lower_value;
	    if( source == lower_value_sb) {
		if( new_value + diff > 255 ) {
		    lower_value_sb.setValue( lower_value= 255 - diff);
		    upper_value_sb.setValue( upper_value= 255);
		}
		else {
		    lower_value= new_value;
		    upper_value_sb.setValue( upper_value= new_value + diff);
		}
	    }
	    else {	// we know that (source == upper_value_sb)
		if( new_value - diff < 0 ) {
		    upper_value_sb.setValue( upper_value= diff);
		    lower_value_sb.setValue( lower_value= 0);
		}
		else {
		    upper_value= new_value;
		    lower_value_sb.setValue( lower_value= new_value - diff);
		}
	    }
	    lower_value_tf.setText( _voxel2string( lower_value));
	    upper_value_tf.setText( _voxel2string( upper_value));
	    changeColormap();
	}

	synchronized /*private*/ final void setTiedMode( final boolean new_setting) {

	    if( new_setting == tied_mode)
		return;	// nothing to do...
	    tied_mode= new_setting;
	}

	synchronized /*private*/ final void changeColormap() {
	    
	    final int coding_type= 
		( (Integer)color_coding_menu.getSelection() ).intValue();
	    final Color under_color= (Color)under_color_menu.getSelection();
	    final Color over_color= (Color)over_color_menu.getSelection();
	    // optimization:
	    if( lower_value == _old_lower_value &&
		upper_value == _old_upper_value &&
		coding_type == _old_coding_type &&
		under_color == _old_under_color &&
		over_color == _old_over_color )
		// no need to change the colormap
		return;
	    _old_lower_value= lower_value;
	    _old_upper_value= upper_value;
	    _old_coding_type= coding_type;
	    _old_under_color= under_color;
	    _old_over_color= over_color;

	    /** Note: the ColormapEvent constructor and, currently, all 
		the listeners only store a reference to the IndexColorModel
		provided below, i.e. we do _not_ make a copy of it. Hence,
		it's expected that this IndexColorModel is not changed until
		another event is sent out!  
		(and the implementation below does exactly that, because
		get8bitColormap constructs a new IndexColorModel at each 
		call...)
	    */
	    crt_colormap= color_coder.get8bitColormap( coding_type, 
						       lower_value, upper_value,
						       under_color, over_color
						       );
	    _fireColormapEvent( new ColormapEvent( ColormapControl.this, crt_colormap));
	}

	final /*private*/ void _fireColormapEvent( final ColormapEvent e) {

	    if( DEBUG) System.out.println( e);

	    // deliver the event to each of the listeners
	    if( null == event_listeners) 
		// nobody listening...
		return;
	    for( int i= 0; i < event_listeners.size(); ++i)
		((ColormapListener)event_listeners.elementAt( i)).colormapChanged( e);
	}

	synchronized void addColormapListener( ColormapListener cl) {

	    if( null == event_listeners) 
		event_listeners= new Vector();
	    event_listeners.addElement( cl);
	}

	synchronized void removeColormapListener( ColormapListener cl) {
	    
	    if( null != event_listeners) 
		event_listeners.removeElement( cl);
	}
    } // end of class ColormapControl


    /** 
     * Support interface for the inner class
     * <code>IndividualDataVolumePanel.ColormapDisplay</code>.
     *
     * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
     * @version $Id: IndividualDataVolumePanel.java,v 1.6 2003/12/21 15:29:43 crisco Exp $ 
     *
     * @see IndividualDataVolumePanel.ColormapDisplay 
     */
    interface ColormapDisplayConstants {

	boolean 	DEBUG= false;

	/** for this particular component, the performance seems to be
            Ok even w/o double buffering, so no reason to use it (it
            wastes resources) */
	boolean		DOUBLE_BUFFER= false;
	int 		BAR_HEIGHT= 10;
	int 		TICKS_HEIGHT= 8;
	int 		HALF_TICKS_HEIGHT= TICKS_HEIGHT >> 1;

	/** used by ColormapDisplay::getMinimumSize() */
	Dimension 	MINIMUM_SIZE= new Dimension( 15, BAR_HEIGHT+TICKS_HEIGHT+2);
    }
    /** 
     * Member (inner) class: produces a visual representation of
     * the current colormap.
     *
     * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
     * @version $Id: IndividualDataVolumePanel.java,v 1.6 2003/12/21 15:29:43 crisco Exp $ 
     *
     * @see IndividualDataVolumePanel.ColormapDisplayConstants 
     */
    /*private*/ final class ColormapDisplay 
	extends Panel implements ColormapListener, ColormapDisplayConstants {

	/*private*/ IndexColorModel 	colormap;
	/*private*/ int			width; /** == 'component width - 2' */
	/*private*/ int[]		ramp;

	/* These var-s are used for double-buffering the screen updates.
	   Initializing them is a bit tricky because
	   Component::createImage() will return null if neither
	   Component's peer, neither its Container's peer, does exist;
	   peers may not exist before the frames, etc are actually pop'ed
	   up on the display...)
	   doLayout() updates them when vport_dims is changed.  
	*/
	/*private*/ Image 	offscreen_buffer; /** by default initialized to null */
	/*private*/ Graphics 	offscreen_gc;
    
	protected ColormapDisplay( IndexColorModel initial_colormap) {
	    colormap= initial_colormap;
	}

	final public void colormapChanged( ColormapEvent e ) {
	    
	    colormap= e.getColormap();
	    // optimization: specify a clip window (no need to repaint the ticks)
	    repaint( 1, TICKS_HEIGHT+1, width, BAR_HEIGHT);
	}

	final public void paint( Graphics g) {
	    
	    if( ColormapDisplayConstants.DEBUG) {
		System.out.println( this + " *** paint" +
				    " getClipBounds:" + g.getClipBounds());
	    }
	    if( DOUBLE_BUFFER && null == offscreen_buffer) {
		offscreen_buffer= createImage( width, BAR_HEIGHT);
		offscreen_gc= offscreen_buffer.getGraphics();
		offscreen_gc.setClip( 0, 0, width, BAR_HEIGHT);
	    }
	    _draw_colormap( g);
	    _draw_ticks( g);	

	    // super.paint( g);	// not needed in this case...
	}

	final public void update( Graphics g) { 

	    if( ColormapDisplayConstants.DEBUG) {
		System.out.println( this + " *** update:" +
				    " getClipBounds:" + g.getClipBounds());
	    }
	    _draw_colormap( g);
	}

	final public void doLayout() {

	    int new_width= getSize().width - 2;
	    if( ColormapDisplayConstants.DEBUG) {
		System.out.println( this + " *** doLayout: new_width= " + new_width);
	    }
	    if( width == new_width || // everything still valid, nothing to do.
		new_width <= 0 ) // window too narrow, not much we can do...
		return;
	    width= new_width;
	    if( DOUBLE_BUFFER && offscreen_buffer != null) {
		// need to resize our offscreen buffer (used for double-buffering)
		offscreen_gc.dispose();	// good practice...
		offscreen_gc= null;
		offscreen_buffer= null;
		// a new buffer will be allocated in update() ...
	    }
	    if( null == ramp || ramp.length < width) 
		ramp= new int[ width];
	    for( int i= 0; i <= width-1; ++i) 
		ramp[ i]= 255 * i / (width-1);
	}

	/** override this in order to prevent being "squished" ... */
	final public Dimension getMinimumSize() { return MINIMUM_SIZE; }

	/** override this in order to prevent being "squished" ... */
	final public Dimension getPreferredSize() { return getMinimumSize(); }

	final /*private*/ void _draw_ticks( Graphics g) {
	    
	    int x, height;
	    g.setColor( getForeground());
	    for( int tick= 0; tick <= 10; ++tick) {
		x= 1 + tick * (width-1) / 10;
		height= ((tick/5)*5 == tick) ? TICKS_HEIGHT : HALF_TICKS_HEIGHT;
		g.drawLine( x, TICKS_HEIGHT-height, x, TICKS_HEIGHT-1);
	    }
	    // drawRect corners are at (x,y) and (x+width,y+height), inclusive
	    g.drawRect( 0, TICKS_HEIGHT, width+1, BAR_HEIGHT+1);
	}

	final /*private*/ void _draw_colormap( Graphics g) {

	    // for speed, use stack var-s instead of the instance fields:
	    final IndexColorModel colormap= this.colormap;
	    final int width= this.width;
	    final int[] ramp= this.ramp;

	    for( int x= 1; x <= width; ++x) {
		g.setColor( new Color( colormap.getRGB( ramp[ x-1])));
		g.drawLine( x, TICKS_HEIGHT+1, x, TICKS_HEIGHT+BAR_HEIGHT);
	    }
	}
    } // end of class ColormapDisplay

} // end of class IndividualDataVolumePanel


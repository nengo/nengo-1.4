
// $Id: DataVolumePanel.java,v 1.7 2002/04/24 14:31:56 cc Exp $
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
 * Provides the functionality common for both volume panel types
 * (individual and combined).  <br><i>Note:</i> despite its name,
 * this is not a subclass of <code>awt.Panel</code>, and not even a
 * <code>Container</code>; its <code>Component</code>-s are managed by
 * an outside <code>Container</code>, which is supplied as an argument
 * to the constructor.
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: DataVolumePanel.java,v 1.7 2002/04/24 14:31:56 cc Exp $ 
 */
abstract public class DataVolumePanel 
    extends PositionListenerAdapter implements PositionGenerator {

    protected static final boolean	DEBUG= false;

    /** as per the spec of Double.toString() (in jdk1.1), this should
    be set to 11 in order to accomodate all posible outputs... but
    this would waste a lot of space in the gui... */
    protected static final int 		IMAGE_VALUE_TEXTFIELD_WIDTH= 8;

    /** 
     * Support interface for the inner class
     * <code>DataVolumePanel.CoordinateFields</code>.
     *
     * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
     * @version $Id: DataVolumePanel.java,v 1.7 2002/04/24 14:31:56 cc Exp $ 
     *
     * @see DataVolumePanel.CoordinateFields */
    interface CoordinateTypes {

	int	VOXEL_COORDINATES= 0;
	int	WORLD_COORDINATES= 1;

	/* Note: on some platforms, '-' & '.' use up almost the same
           space as an '8' -- e.g. a world coords textfield width of 5
           is only _guaranteed_ to display 3 significant digits (for
           typical brain MNI-Talairach space world coordinates) ... */
	int[]	TEXTFIELD_WIDTH= { 
	    3, // voxel coords
	    5  // world coords
	};
    }

    /*private*/ Container 		parent_container;
    /*private*/ Frame			parent_frame;
    /** our column in parent_container's grid */
    /*private*/ int 			grid_column;
    protected boolean 			enable_world_coords;
    protected boolean 			byte_voxel_values;
    /*private*/ Main			applet_root;
    /** initialized by the subclasses; 
	NB: they are expected to implement PositionListener too! */
    protected ImageProducer[]		slice_producers;
    /*private*/ Slice2DViewport[] 	slice_vports;
    /*private*/ Component		title;
    protected Container			controls_panel; 
    /*private*/ CoordinateFields	coord_fields;
    protected PopupMenu			popup_menu;
    /*private*/ PositionGateway		pos_event_gateway; 
    /*private*/ CheckboxMenuItem	sync_menu_item;
    /*private*/ Point3Dfloat		initial_world_cursor;

    public DataVolumePanel( /** it's expected to have a GridBagLayout-manager! */
			    Container parent_container,
			    /** to be used for GridBagConstraints.gridx */
			    int grid_column,
			    Point3Dfloat initial_world_cursor,
			    boolean enable_world_coords,
			    boolean byte_voxel_values,
			    Main applet_root
			    ) {

	if( !( parent_container.getLayout() instanceof GridBagLayout)) {
	    final String error_msg= 
		"supplied Container doesn't have a GridBagLayout layout manager";
	    throw new IllegalArgumentException( error_msg);
	}
	this.parent_container= parent_container;
	parent_frame= Util.getParentFrame( parent_container);
	if( null == parent_frame)
	    System.err.println( this + ": cannot determine my parent frame!");

	this.grid_column= grid_column;
	this.initial_world_cursor= initial_world_cursor;
	this.enable_world_coords= enable_world_coords;
	this.byte_voxel_values= byte_voxel_values; 
	this.applet_root= applet_root;
	controls_panel= new LightweightPanel( new GridBagLayout());
	popup_menu= new PopupMenu();
	pos_event_gateway= new PositionGateway();
    }

    /** completes the initalization process started by constructor(s) */
    protected final void _finish_initialization() {

	MouseAdapter popup_adapter= new MouseAdapter() {
	    // NB: need to override all 3 methods because on different 
	    // platforms the PopupTrigger could be delivered on different
	    // event types (e.g. mousePressed on Unix, mouseReleased on Win32)
	    public final void mousePressed( MouseEvent e) {
		if( e.isPopupTrigger()) _process_mouse_event( e);
	    }
	    public final void mouseReleased( MouseEvent e) {
		if( e.isPopupTrigger()) _process_mouse_event( e);
	    }
	    public final void mouseClicked( MouseEvent e) {
		if( e.isPopupTrigger()) _process_mouse_event( e);
	    }
	    /*private*/ final void _process_mouse_event( MouseEvent e) {
		if( DEBUG) {
		    System.out.println( e);
		    System.out.println( e.getSource() + " " + e.getComponent());
		}
		// in this context, 
		// 'e.getComponent()' is same as '(Component)e.getSource()'
		popup_menu.show( e.getComponent(), e.getX(), e.getY());
		e.consume();
	    }
	};
	/* NOTE: something poorly documented: a popup menu can only be
	   owned by one component at a time ! */
	parent_container.add( popup_menu);

	GridBagConstraints gbc= new GridBagConstraints();
	gbc.fill= GridBagConstraints.HORIZONTAL;
	gbc.weightx= 1.0;
	gbc.gridx= 0; 
	gbc.gridy= 0;
	// Note: the CoordinateFields constructor adds to popup_menu 
	controls_panel.add( coord_fields= 
			    new CoordinateFields( initial_world_cursor, popup_menu,
						  enable_world_coords),
			    gbc);
	controls_panel.addMouseListener( popup_adapter);

	slice_vports= new Slice2DViewport[] { 
	    new TransverseSlice2DViewport( slice_producers[ 0], 
					   (PositionListener)slice_producers[ 0],
					   initial_world_cursor),
	    new SagittalSlice2DViewport( slice_producers[ 1], 
					 (PositionListener)slice_producers[ 1],
					 initial_world_cursor),
	    new CoronalSlice2DViewport( slice_producers[ 2], 
					(PositionListener)slice_producers[ 2],
					initial_world_cursor)
	};
	for( int i= 0; i < 3; ++i) 
	    slice_vports[ i].addMouseListener( popup_adapter);

	gbc.gridx= grid_column;
	gbc.gridy= GridBagConstraints.RELATIVE;
	gbc.weightx= 1.0;
	gbc.insets.bottom= gbc.insets.top= 0;
	gbc.insets.left= gbc.insets.right= 5;
	gbc.fill= GridBagConstraints.HORIZONTAL;
	gbc.weighty= 0;
	parent_container.add( title= new Label( getTitle(), Label.CENTER), gbc);
	gbc.insets.bottom= gbc.insets.top= 3;
	gbc.fill= GridBagConstraints.BOTH;
	for( int i= 0; i < 3; ++i) {
	    // transverse, sagittal, coronal
	    gbc.weighty= slice_vports[ i].getOriginalImageHeight();
	    parent_container.add( slice_vports[ i], gbc); 
	}
	gbc.fill= GridBagConstraints.HORIZONTAL;
	gbc.weighty= 0;
	gbc.anchor= GridBagConstraints.NORTH;
	parent_container.add( controls_panel, gbc);

	// hookup the position communication lines
	Vector listeners= new Vector();
	Vector generators= new Vector();
	for( int i= 0; i < 3; ++i) {
	    listeners.addElement( slice_producers[ i]);
	    listeners.addElement( slice_vports[ i]);
	    generators.addElement( slice_vports[ i]);
	}
	listeners.addElement( coord_fields);
	listeners.addElement( pos_event_gateway);
	generators.addElement( coord_fields);
	generators.addElement( pos_event_gateway);

	for( int g= 0; g < generators.size(); ++g) 
	    for( int l= 0; l < listeners.size(); ++l) {
		Object peg= generators.elementAt( g);
		Object pl= listeners.elementAt( l);
		if( peg != pl) {	// don't want to listen to my own babble
		    if( DEBUG) System.out.println( peg + "  " + pl);
		    // this method is smart enough not to add duplicates
		    ((PositionGenerator)peg).addPositionListener( (PositionListener)pl);
		}
	    }

	// any general (shared) popup menu commands should be added here:
	sync_menu_item= new PositionSyncMenuItem( applet_root);
	popup_menu.add( sync_menu_item);
	popup_menu.addSeparator();
	Menu hm= new Menu( "Help", true /* a "tear-off" menu */ );
	{
	    MenuItem help= new MenuItem( "Help");
	    help.addActionListener( new ActionListener() {
		    public void actionPerformed( ActionEvent ev) {
			Help.showHelp( applet_root);
		    }
		});
	    hm.add( help); 
	    MenuItem about= new MenuItem( "About JIV");
	    about.addActionListener( new ActionListener() {
		    public void actionPerformed( ActionEvent e) {
			if( parent_frame != null)
			    About.popFullVersion( parent_frame);
			else
			    applet_root.progressMessage( About.getShortVersion());
		    }
		});
	    hm.add( about);
	}
	popup_menu.add( hm);
	popup_menu.add( new QuitMenuItem( applet_root));
	// TODO(maybe): add a kbd shortcut as well?

    } // end of _finish_initialization()

    public final void positionChanged( PositionEvent e) {
	pos_event_gateway.positionChanged_External( e);
    }
	
    public void addPositionListener( PositionListener pl) {
	addPositionListener( pl, false);
    }

    public void addPositionListener( PositionListener pl, boolean send_event) {
	pos_event_gateway.addPositionListener_External( pl);
	if( send_event) {
	    PositionEvent event= 
		/* Note: the PositionEvent constructor makes a _copy_ of
		   its last argument (the cursor) */
		new PositionEvent( this,
				   PositionEvent.X|PositionEvent.Y|PositionEvent.Z,
				   coord_fields.getCursorPosition() );
	    pl.positionChanged( event);
	}
    }

    public void removePositionListener( PositionListener pl) {
	pos_event_gateway.removePositionListener_External( pl);
    }

    /** it should be 'synchronized', but not necessary because Main is the 
	only one calling it...
    */
    public void setPositionSync( boolean new_setting) {
	sync_menu_item.setState( new_setting);
    }

    /** returns a short instead of the more logical byte merely for convenience
	(bytes are signed, so additional code is needed to interpret bytes as
	unsigned, i.e. 0...255) 
    */
    protected final short _string2voxel( String string) 
	throws NumberFormatException {

	if( byte_voxel_values)
	    return Short.parseShort( string);

	return _image_real2byte( Float.valueOf( string).floatValue());
    }

    protected final String _voxel2string( int voxel_value) {

	if( byte_voxel_values)
	    return String.valueOf( voxel_value);

	float real_value= _image_byte2real( (short)voxel_value);
	return String.valueOf( Util.chopToNSignificantDigits( real_value, 3));
    }

    abstract public int getXSize();
    abstract public int getYSize();
    abstract public int getZSize();

    abstract public String getTitle();

    /** This is an aid to CoordinateFields:
	if the actual implementation returns (always) a negative value, then
	the "value field" won't be displayed
    */
    abstract protected int _getVoxelValue( Point3Dint voxel_pos);

    abstract protected float _image_byte2real( short voxel_value);

    abstract protected short _image_real2byte( float image_value);


    /** 
     * Member (inner) class: the textual coordinate display/input
     * boxes ("fields").
     *
     * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
     * @version $Id: DataVolumePanel.java,v 1.7 2002/04/24 14:31:56 cc Exp $ 
     *
     * @see DataVolumePanel.CoordinateTypes 
     */
    /*private*/ final class CoordinateFields extends LightweightPanel 
	implements CoordinateTypes, ActionListener, PositionListener, PositionGenerator {

	// FIXME: it doesn't handle well an allocated width which is 
	// too small to fit everything (horizontally) -> left/right
	// ends are not visible/accesible...

	/* Note: these two should be kept in sync (we use a separare
           int field simply for execution speed (its value is tested
           inside changePosition(), which is typically called very
           often... */
	/*private*/ int			coordinates_type= WORLD_COORDINATES;
	/*private*/ ChoiceMenu		coords_type_menu;

	/*private*/ TextField 		x_field;
	/*private*/ TextField 		y_field;
	/*private*/ TextField 		z_field;
	/*private*/ TextField 		value_field;
	/*private*/ Vector 		event_listeners;
	/* Beware! these two should always be in sync! */
	/*private*/ Point3Dfloat 	world_cursor;	/** world coordinates! */
	/*private*/ Point3Dint 		voxel_cursor;	/** voxel coordinates */

	protected CoordinateFields( Point3Dfloat initial_world_cursor,
				    PopupMenu popup_menu,
				    boolean enable_world_coords
				    ) {

	    this.world_cursor= initial_world_cursor;
	    // initialization for voxel_cursor
	    voxel_cursor= CoordConv.world2voxel( this.world_cursor);

	    /** label-value(-flag) tuples to be used in the "coords type" menu */
	    final Object[][] menu_definition= {
		{ "world", new Integer( WORLD_COORDINATES), 
		  new Boolean( !enable_world_coords) 
		},
		{ "voxel", new Integer( VOXEL_COORDINATES) 
		}
	    };
	    coordinates_type= 
		enable_world_coords ? WORLD_COORDINATES : VOXEL_COORDINATES;
	    /* determine the string label of the menu entry
               corresponding to 'coordinates_type' */
	    int i;
	    for( i= 0; i < menu_definition.length; ++i) 
		if( coordinates_type == ((Integer)menu_definition[ i][ 1]).intValue() )
		    break;
	    // if we didn't find it, then i == menu_definition.length and the JVM
	    // will throw an ArrayIndexOutOfBoundsException on the following line:
	    String default_menu_selection= (String)menu_definition[ i][ 0];
	    ActionListener al= new ActionListener() {
		public void actionPerformed( ActionEvent ae) {
		    CoordinateFields.this.changeCoordinatesType();
		}
	    };
	    coords_type_menu= new ChoiceMenu( "Coordinates type",
					      menu_definition, default_menu_selection, 
					      al); 
	    popup_menu.add( coords_type_menu);

	    setLayout( new GridBagLayout());
	    GridBagConstraints gbc= new GridBagConstraints();
	    gbc.gridx= GridBagConstraints.RELATIVE;
	    gbc.gridy= 0;
	    gbc.fill= GridBagConstraints.NONE;
	    final int tf_width= TEXTFIELD_WIDTH[ coordinates_type];
	    x_field= _add_field_with_label( "X", "", tf_width, gbc);
	    y_field= _add_field_with_label( "Y", "", tf_width, gbc);
	    z_field= _add_field_with_label( "Z", "", tf_width, gbc);
	    /* send myself an event, such that positionChanged() will
               fill-in the correct type of coordinates... */
	    positionChanged( new PositionEvent( CoordinateFields.this, 
						PositionEvent.X |
						PositionEvent.Y |
						PositionEvent.Z,
						world_cursor)
			     );
	    int voxel_value= _getVoxelValue( voxel_cursor);
	    if( voxel_value >= 0) {
		value_field= _add_field_with_label( "", _voxel2string( voxel_value), 
						    byte_voxel_values ? 
						    3 : 
						    IMAGE_VALUE_TEXTFIELD_WIDTH,
						    gbc);
		value_field.setEditable( false);
	    }
	    else 
		value_field= null;	// probably unnecessary...

	    this.addPositionListener( this); 
	    x_field.addActionListener( this);
	    y_field.addActionListener( this);
	    z_field.addActionListener( this);
	}
	
	/** convenience method */
	/*private*/ TextField _add_field_with_label( String text_label,
						     String initial_content,
						     int width,
						     GridBagConstraints gbc) {

	    gbc.weightx= 1.0;
	    gbc.anchor= GridBagConstraints.EAST;
	    add( new Label( text_label + ":"), gbc);

	    gbc.weightx= 0;
	    gbc.anchor= GridBagConstraints.WEST;
	    gbc.insets.left= 0;
	    TextField text_field= new TextField( initial_content, width);
	    add( text_field, gbc);

	    gbc.insets.left= 0;

	    return text_field;
	}

	final /*private*/ void _firePositionEvent( final PositionEvent e) {

	    if( DEBUG) System.out.println( e);

	    // deliver the event to each of the listeners
	    if( null == event_listeners) 
		// nobody listening...
		return;
	    for( int i= 0; i < event_listeners.size(); ++i)
		((PositionListener)event_listeners.elementAt( i)).positionChanged( e);
	}

	synchronized /*private*/ final void changeCoordinatesType() { 

	    coordinates_type= ((Integer)coords_type_menu.getSelection()).intValue();

	    int new_width= TEXTFIELD_WIDTH[ coordinates_type];
	    x_field.setColumns( new_width);
	    y_field.setColumns( new_width);
	    z_field.setColumns( new_width);
	    // FIXME: these setColumns() currently don't have any effect... ??

	    /* send myself an event, such that positionChanged() will
	       update the coordinate fields with the new format... */
	    PositionEvent pe= 
		new PositionEvent( CoordinateFields.this, 
				   PositionEvent.X|PositionEvent.Y|PositionEvent.Z,
				   world_cursor);
	    positionChanged( pe);
	}

	/** for the exclusive private/internal use of actionPerformed */
	/*private*/ Point3Dint __actionPerformed_voxel= new Point3Dint();

	synchronized public final void actionPerformed( ActionEvent ae) {

	    if( DEBUG) System.out.println( ae);
	    if( ae.getID() != ActionEvent.ACTION_PERFORMED)
		return;

	    int pos_event_mask;
	    TextField source= (TextField)ae.getSource();
	    // alias them to local (stack) var-s for speed:
	    final Point3Dfloat 	world_cursor= this.world_cursor;
	    final Point3Dint 	voxel_cursor= this.voxel_cursor;

	    if( VOXEL_COORDINATES == coordinates_type) {
		/* == VOXEL COORDINATES == */
		try { 
		    int new_value= Short.parseShort( ae.getActionCommand()); 

		    int dimension_size;
		    if( source == x_field) dimension_size= getXSize();
		    else if( source == y_field) dimension_size= getYSize();
		    else if( source == z_field) dimension_size= getZSize();
		    else {
			throw new IllegalArgumentException( "unexpected source:" + ae);
		    }
		    // remember: the event producer should check the range!
		    if( new_value < 0 || new_value >= dimension_size) {
			throw new NumberFormatException( "out of range...");
		    }
		    // recall: voxel_cursor and world_cursor
		    // are always consistent with each other!
		    if( source == x_field) {
			voxel_cursor.x = new_value;
			pos_event_mask= PositionEvent.X;
		    }
		    else if( source == y_field) {
			voxel_cursor.y = new_value;
			pos_event_mask= PositionEvent.Y;
		    }
		    else {
			voxel_cursor.z = new_value;
			pos_event_mask= PositionEvent.Z;
		    }
		    CoordConv.voxel2world( world_cursor, voxel_cursor);
		    /* Note: the PositionEvent constructor makes a _copy_
		       of its last argument (the cursor) */
		    _firePositionEvent( new PositionEvent( CoordinateFields.this, 
							   pos_event_mask, 
							   world_cursor));
		}
		catch( NumberFormatException exception) { 
		    /* the code below is suboptimal (we could figure out
		       which one text field to update), but it's only run
		       in exceptional situations (and when speed is not an
		       issue because the user just typed something...) */
		    x_field.setText( String.valueOf( voxel_cursor.x));
		    y_field.setText( String.valueOf( voxel_cursor.y));
		    z_field.setText( String.valueOf( voxel_cursor.z));
		}
		return;
	    }
	    /* == WORLD COORDINATES == */
	    try {
		float new_value= 
		    Float.valueOf( ae.getActionCommand()).floatValue();

		if( source == x_field) {
		    CoordConv.world2voxel( __actionPerformed_voxel, 
					   new_value, world_cursor.y, world_cursor.z);
		    /* FIXME: the range test below _assumes_ that the
                       world coords axes are the same as the voxel
                       coords axes! */
		    if( __actionPerformed_voxel.x < 0 || 
			__actionPerformed_voxel.x >= getXSize() )
			throw new NumberFormatException( "out of range...");
		    world_cursor.x= new_value;
		    pos_event_mask= PositionEvent.X;
		}
		else if( source == y_field) { 
		    CoordConv.world2voxel( __actionPerformed_voxel, 
					   world_cursor.x, new_value, world_cursor.z);
		    if( __actionPerformed_voxel.y < 0 || 
			__actionPerformed_voxel.y >= getYSize() )
			throw new NumberFormatException( "out of range...");
		    world_cursor.y= new_value;
		    pos_event_mask= PositionEvent.Y;
		}
		else if( source == z_field) {
		    CoordConv.world2voxel( __actionPerformed_voxel, 
					   world_cursor.x, world_cursor.y, new_value);
		    if( __actionPerformed_voxel.z < 0 || 
			__actionPerformed_voxel.z >= getZSize() )
			throw new NumberFormatException( "out of range...");
		    world_cursor.z= new_value;
		    pos_event_mask= PositionEvent.Z;
		}
		else {
		    throw new IllegalArgumentException( "unexpected source:" + ae);
		}	
		__actionPerformed_voxel.copyInto( voxel_cursor);
		/* Note: the PositionEvent constructor makes a _copy_
		   of its last argument (the cursor) */
		_firePositionEvent( new PositionEvent( CoordinateFields.this, 
						       pos_event_mask, 
						       world_cursor));
	    }
	    catch( NumberFormatException exception) {
		/* the code below is suboptimal (we could figure out
		   which one text field to update), but it's only run
		   in exceptional situations (and when speed is not an
		   issue because the user just typed something...) */
		x_field.setText( String.valueOf( world_cursor.x));
		y_field.setText( String.valueOf( world_cursor.y));
		z_field.setText( String.valueOf( world_cursor.z));
	    }
	    /* NB: ActionEvents are not a subclass of InputEvent,
	       thus cannot be marked "consumed" ... 
	    */
	} // end of actionPerformed()

	synchronized final public void positionChanged( PositionEvent e) {

	    // alias them to local (stack) var-s for speed:
	    final Point3Dfloat 	world_cursor= this.world_cursor;
	    final Point3Dint 	voxel_cursor= this.voxel_cursor;

	    if( e.isXValid())
		world_cursor.x= e.getX();
	    if( e.isYValid())
		world_cursor.y= e.getY();
	    if( e.isZValid())
		world_cursor.z= e.getZ();
	    CoordConv.world2voxel( voxel_cursor, world_cursor);

	    if( value_field != null) {
		int new_voxel_value= _getVoxelValue( voxel_cursor);
		value_field.setText( _voxel2string( new_voxel_value));
	    }
	    if( VOXEL_COORDINATES == coordinates_type) {
		/* == VOXEL COORDINATES == */
		if( e.isXValid())
		    x_field.setText( String.valueOf( voxel_cursor.x));
		if( e.isYValid())
		    y_field.setText( String.valueOf( voxel_cursor.y));
		if( e.isZValid())
		    z_field.setText( String.valueOf( voxel_cursor.z));
		return;
	    }
	    /* == WORLD COORDINATES == */
	    if( e.isXValid())
		x_field.setText( String.valueOf( world_cursor.x));
	    if( e.isYValid())
		y_field.setText( String.valueOf( world_cursor.y));
	    if( e.isZValid())
		z_field.setText( String.valueOf( world_cursor.z));

	} // end of positionChanged()
	
	final public int getMaxSliceNumber() { return -1; }
	final public float getOrthoStep() { return Float.NaN; }

	synchronized public void addPositionListener( PositionListener pl) {
	
	    if( null == event_listeners) 
		event_listeners= new Vector();
	    if( null == pl || event_listeners.contains( pl))
		return;
	    event_listeners.addElement( pl);
	}

	synchronized public void removePositionListener( PositionListener pl) {
	    
	    if( null != event_listeners) 
		event_listeners.removeElement( pl);
	}

	/** returns the current cursor position (in world coordinates) */
	public Point3Dfloat getCursorPosition() { return world_cursor; }

    } // end of class CoordinateFields

} // end of class DataVolumePanel

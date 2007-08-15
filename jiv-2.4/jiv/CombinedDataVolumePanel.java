
// $Id: CombinedDataVolumePanel.java,v 1.4 2001/10/04 19:26:31 cc Exp $
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

/**
 * Implements the volume panel functionality specific to panels
 * displaying a combination of two image volumes.
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: CombinedDataVolumePanel.java,v 1.4 2001/10/04 19:26:31 cc Exp $ 
 */
public final class CombinedDataVolumePanel extends DataVolumePanel {

    /** initial setting of the blend controls slider */
    /*private*/ static final byte		DEFAULT_BLEND_SETTING= 50;

    /*private*/ IndividualDataVolumePanel[]	source_panels;
    /*private*/ BlendControl			blend_control;

    public CombinedDataVolumePanel( IndividualDataVolumePanel source_panel_1,
				    IndividualDataVolumePanel source_panel_2,
				    Container parent_container,
				    int grid_column,
				    Point3Dfloat initial_world_cursor,
				    boolean enable_world_coords,
				    Main applet_root
				    ) {
	// initialization done in the superclass (part 1/2)
	super( parent_container, grid_column, initial_world_cursor,
	       enable_world_coords, false, applet_root);

	source_panels= new IndividualDataVolumePanel[] {
	    source_panel_1,
	    source_panel_2
	};
	Point3Dint initial_slices= CoordConv.world2voxel( initial_world_cursor);
	SliceImageProducer[][] src_producers= new SliceImageProducer[ 2][];
	for( int i= 0; i < 2; ++i) {
	    src_producers[ i]= new SliceImageProducer[] {
		source_panels[ i].getATransverseSliceImageProducer( initial_slices.z),
		source_panels[ i].getASagittalSliceImageProducer( initial_slices.x),
		source_panels[ i].getACoronalSliceImageProducer( initial_slices.y)
	    };
	}
	slice_producers= new BlendedCombinedImageSource[] {
	    // transverse
	    new BlendedCombinedImageSource( src_producers[0][0], src_producers[0][0],
					    src_producers[1][0], src_producers[1][0]),
	    // sagittal
	    new BlendedCombinedImageSource( src_producers[0][1], src_producers[0][1],
					    src_producers[1][1], src_producers[1][1]),
	    // coronal
	    new BlendedCombinedImageSource( src_producers[0][2], src_producers[0][2],
					    src_producers[1][2], src_producers[1][2])
	};
	GridBagConstraints gbc= new GridBagConstraints();
	gbc.fill= GridBagConstraints.HORIZONTAL;
	gbc.weightx= 1.0;
	gbc.gridx= 0;
	gbc.gridy= 1;
	gbc.insets.top= 5;
	blend_control= new BlendControl( DEFAULT_BLEND_SETTING,
					 (BlendedCombinedImageSource[])slice_producers);
	controls_panel.add( blend_control, gbc);

	// initialization done in the superclass (part 2/2)
	super._finish_initialization();
    }

    /** NB: the current implementation _assumes_ that both source_panels 
	have the same sizes! 
    */
    final public int getXSize() { return source_panels[ 0].getXSize(); }
    final public int getYSize() { return source_panels[ 0].getYSize(); }
    final public int getZSize() { return source_panels[ 0].getZSize(); }

    final public String getTitle() { 

	final String title0= source_panels[ 0].getTitle();
	final String title1= source_panels[ 1].getTitle();
	final String separator= " <-> ";
	StringBuffer ret= 
	    new StringBuffer( title0.length() + title1.length() + separator.length());
	ret.append( title0);
	ret.append( separator);
	ret.append( title1);
	// this is efficient: it doesn't create a new String!
	return ret.toString();
    }

    /** aid to DataVolumePanel.CoordinateFields */
    protected final int _getVoxelValue( Point3Dint voxel_pos) {
	// a negative value means that the "value field" won't be displayed
	return -1;
    }

    protected final float _image_byte2real( short voxel_value) {
	throw new IllegalArgumentException( this + " CombinedDataVolumePanel#_image_byte2real not yet implemented...");
    }

    protected final short _image_real2byte( float image_value) {
	throw new IllegalArgumentException( this + " CombinedDataVolumePanel#_image_real2byte not yet implemented...");
    }


    /**
     * Member (inner) class: a <code>CombinedImageSource</code> that
     * uses an adjustable blending factor for combining two images
     * (in RGB color space).
     *
     * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
     * @version $Id: CombinedDataVolumePanel.java,v 1.4 2001/10/04 19:26:31 cc Exp $ 
     */
    /*private*/ final class BlendedCombinedImageSource extends CombinedImageSource {

	/*private*/ boolean			initialization_complete= false;

	// BEWARE: instance fields initializers are equivalent to an
	// "instance initializer block", so are run _after_ the superclass
	// constructor, and before our constructor!!!

	/** valid range: 0 ... 100 (inclusive) 
	    we use a byte instead of a float for efficiency reasons: the
	    colormap multiplications will be done in integer arithmetic
	    instead of the more time consuming floating point one...
	 */
	/*private*/ byte 			blend_setting= DEFAULT_BLEND_SETTING;
	/*private*/ IndexColorModel[] 		old_color_model= { null, null};
	/*private*/ byte		 	old_blend_setting= -1;
	/*private*/ ByteColormapEntries		cmap_read_buffer= 
	    new ByteColormapEntries();
	/*private*/ ShortColormapEntries[]	altered_cmap= { 
	    new ShortColormapEntries(), new ShortColormapEntries() 
	};

	protected BlendedCombinedImageSource( ImageProducer source_1, 
					      PositionListener listener_for_source_1, 
					      ImageProducer source_2,
					      PositionListener listener_for_source_2 
					      ) {
	    super( source_1, listener_for_source_1, source_2, listener_for_source_2);
	    initialization_complete= true;
	    // now that _everything_ is initialized (i.e. including our instance
	    // fields initialized by the "instance initializer" code above :),
	    // we should call super.newFrame() which in turn will, for the first
	    // time, call combineInputImages()!
	    newFrame( -1);
	}

	/** valid range for 'value' is 0.0 ... 1.0 (inclusive) */
	protected final void setBlend( byte value) {
	    this.blend_setting= value;
	}

	protected final void combineInputImages() {

	    /* This test is critical!  Otherwise this method may run before 
	       various instance fields of BlendedCombinedImageSource are 
	       properly initialized/allocated (e.g. called from within the
	       superclass constructor CombinedImageSource.<init>)!
	    */
	    if( !initialization_complete)
		return;

	    /* Note: The two long, time-consuming loops of this method
	       have been carefully hand-optimized for speed (yes, the
	       compiler output was checked :) 
	    */
	    // for speed, use stack variables instead of the instance fields:
	    ShortColormapEntries[]	altered_cmap= this.altered_cmap;
	    final IndexColorModel[]	color_model= this.color_model;
	    final byte[][]		src_data= this.src_data;
	    int[]			outgoing_buffer= this.outgoing_buffer;

	    // alter (i.e. scale-down) the colormaps 
	    int i;
	    for( i= 0; i < 2; ++i) {

		if( CombinedDataVolumePanel.DEBUG) {
		    System.out.println( this + " old: " + old_color_model[ i]);
		    System.out.println( this + " new: " + color_model[ i]);
		}
		// an easy optimization :
		if( blend_setting == old_blend_setting && 
		    color_model[ i] == old_color_model[ i] 
		    ) {
		    if( CombinedDataVolumePanel.DEBUG) 
			System.out.println( color_model[ i] + " didn't change");
		    continue;
		}
		old_color_model[ i]= color_model[ i];

		final ByteColormapEntries cmap_read_buffer= this.cmap_read_buffer;
		color_model[ i].getReds( cmap_read_buffer.reds);
		color_model[ i].getGreens( cmap_read_buffer.greens);
		color_model[ i].getBlues( cmap_read_buffer.blues);

		final byte mult= 
		    (byte)( (i == 0) ? (100 - blend_setting) : blend_setting );

		final ShortColormapEntries altered_cmap_i= altered_cmap[ i];
		/* decreasing index loops are rumored to run faster in
		   Java */
		for( int p= color_model[ i].getMapSize() - 1; p >= 0; --p) {
		    altered_cmap_i.reds[ p]= (short)
			(mult * (0xFF & cmap_read_buffer.reds[ p]) / 100);
		    altered_cmap_i.greens[ p]= (short)
			(mult * (0xFF & cmap_read_buffer.greens[ p]) / 100);
		    altered_cmap_i.blues[ p]= (short)
			(mult * (0xFF & cmap_read_buffer.blues[ p]) / 100);
		}
	    }
	    old_blend_setting= blend_setting;

	    /* mix (i.e. sum) the pixels in RGB space 
	     */
	    final ShortColormapEntries altered_cmap_0= altered_cmap[ 0];
	    final ShortColormapEntries altered_cmap_1= altered_cmap[ 1];
	    // this is a _long_ loop ...
	    for( i= img_width*img_height - 1; i >= 0; --i) {
		final int src_0= 0xFF & src_data[ 0][ i];
		final int src_1= 0xFF & src_data[ 1][ i];
		outgoing_buffer[ i]= 
		    ((altered_cmap_0.reds[ src_0]+altered_cmap_1.reds[ src_1]) << 16) 
		    | 
		    ((altered_cmap_0.greens[ src_0]+altered_cmap_1.greens[ src_1]) << 8)
		    | 
		    (altered_cmap_0.blues[ src_0]+altered_cmap_1.blues[ src_1]);
	    }
	}

	/** helper class (a member/inner class) */
	/*private*/ final class ByteColormapEntries {
	    
	    public byte[] reds= new byte[ 256];
	    public byte[] greens= new byte[ 256];
	    public byte[] blues= new byte[ 256];
	} 

	/** helper class (a member/inner class) */
	/*private*/ final class ShortColormapEntries {

	    public short[] reds= new short[ 256];
	    public short[] greens= new short[ 256];
	    public short[] blues= new short[ 256];
	} 

    } // end of class BlendedCombinedImageSource


    /**
     * Member (inner) class: the user interface input controls for
     * adjusting the blending factor.
     *
     * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
     * @version $Id: CombinedDataVolumePanel.java,v 1.4 2001/10/04 19:26:31 cc Exp $ 
     */
    /*private*/ final class BlendControl extends LightweightPanel {

	/** should always be in the 0 ... 100 range (inclusive) */
	/*private*/ byte 				blend_setting;
	/*private*/ BlendedCombinedImageSource[] 	slice_producers;
	/*private*/ TextField[] 			tf= { null, null};
	/*private*/ Scrollbar				sb;
	/*private*/ GridBagConstraints 			gbc= new GridBagConstraints();

	protected BlendControl( byte initial_value, 
				BlendedCombinedImageSource[] slice_producers) {

	    blend_setting= initial_value;
	    this.slice_producers= slice_producers;
	    tf[ 0]= new TextField( String.valueOf( (100 - blend_setting)/100f), 4);
	    tf[ 1]= new TextField( String.valueOf( blend_setting/100f), 4);
	    sb= new Scrollbar( Scrollbar.HORIZONTAL);
	    int visible= sb.getVisibleAmount();
	    sb.setMinimum( 0);
	    /* the value is read on the left side of the slider, hence
	       we need to correct the Max if we want the scrollbar to
	       return values up to 100 inclusive... */
	    sb.setMaximum( 100+visible);
	    sb.setValue( blend_setting);

	    setLayout( new GridBagLayout());
	    gbc.gridx= GridBagConstraints.RELATIVE; 
	    gbc.fill= GridBagConstraints.HORIZONTAL;
	    add( tf[ 0], gbc);
	    gbc.weightx= 1f;
	    add( sb, gbc);
	    gbc.weightx= 0f;
	    add( tf[ 1], gbc);

	    ActionListener action_listener= new ActionListener() {
		synchronized public final void actionPerformed( ActionEvent ae) {

		    if( DEBUG) System.out.println( ae);
		    if( ae.getID() != ActionEvent.ACTION_PERFORMED)
			return;
		    try { 
			final float new_value_as_float= 
			    Float.valueOf( ae.getActionCommand()).floatValue();
			if( new_value_as_float < 0f || new_value_as_float > 1f)
			    throw new NumberFormatException( "out of range...");
			blend_setting=
			    (byte)Math.round( 100f * new_value_as_float);
			if( ae.getSource() == tf[ 0]) 		
			    blend_setting= (byte)(100 - blend_setting);

			sb.setValue( blend_setting);
			_change_blend();
		    }
		    catch( NumberFormatException exception) { 
			// the previous (valid) value is automatically preserved...
		    }
		    finally {
			tf[ 0].setText( String.valueOf( (100 - blend_setting)/100f));
			tf[ 1].setText( String.valueOf( blend_setting/100f));
		    }
		}
	    };
	    tf[ 0].addActionListener( action_listener);
	    tf[ 1].addActionListener( action_listener);
	    sb.addAdjustmentListener( new AdjustmentListener() {
		public final void adjustmentValueChanged( AdjustmentEvent ae) {

		    if( DEBUG) System.out.println( ae);
		    int new_value= ae.getValue();
		    if( new_value < 0 )
			((Scrollbar)ae.getSource()).setValue( new_value= 0);
		    else if( new_value > 100)
			((Scrollbar)ae.getSource()).setValue( new_value= 100);

		    blend_setting= (byte)new_value;
		    tf[ 0].setText( String.valueOf( (100 - blend_setting)/100f));
		    tf[ 1].setText( String.valueOf( blend_setting/100f));
		    _change_blend();
		}
	    });
	}

	final /*private*/ void _change_blend() { 

	    for( int i= 0; i < slice_producers.length; ++i) {
		slice_producers[ i].setBlend( blend_setting);
		slice_producers[ i].newFrame( -1);
	    }
	}
    } // end of class BlendControl

} // end of class CombinedDataVolumePanel

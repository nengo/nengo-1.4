
// $Id: SliceImageProducer.java,v 1.4 2002/04/24 14:31:56 cc Exp $
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

/**
 * An <code>ImageProducer</code> that provides the
 * orientation-independent functionality of producing 2D slice image
 * data. The orientation-specific functionality is in its 3 direct 
 * subclasses: <code>TransverseSliceImageProducer</code>, 
 * <code>CoronalSliceImageProducer</code>, and 
 * <code>SagittalSliceImageProducer</code>. 
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: SliceImageProducer.java,v 1.4 2002/04/24 14:31:56 cc Exp $ 
 */
public abstract class SliceImageProducer extends MemoryImageSource 
    implements PositionListener, ColormapListener {

    protected static final boolean DEBUG= false;

    protected Data3DVolume data_volume;
    protected IndexColorModel colormap;
    protected int crt_slice;
    protected int slice_width;
    protected int slice_height;
    /** this array is allocated by whoever creates this object (and passes
	an allocated and initialized array to this class' constructor).
	For efficiency, the same array is then reused over and over again
	(since it's always the right size, guaranteed). */
    protected byte[] slice_data;

    protected SliceImageProducer( int default_slice, 
				  byte[] default_slice_data,
				  int slice_width,
				  int slice_height,
				  IndexColorModel default_colormap,
				  Data3DVolume data_volume 
				  ) {

	super( slice_width, slice_height, default_colormap, 
	       default_slice_data, 0, slice_width);
	// it's good to call this right after MemoryImageSource's constructor.
	setAnimated(true); 
	/* after browsing through the AWT public sources, it become obvious
	   that enabling full buff updates will speed thing up in 
	   MemoryImageSource, and possibly down the pipeline too (because
	   the hint TOPDOWNLEFTRIGHT|COMPLETESCANLINES will be sent to 
	   the consumers) ... */
	setFullBufferUpdates( true); 
	this.data_volume= data_volume;
	colormap= default_colormap;
	crt_slice= default_slice;
	this.slice_width= slice_width;
	this.slice_height= slice_height;
	slice_data= default_slice_data;
    }

    /** overrides MemoryImageSource (who's implementation does nothing) 
     */
    public final void requestTopDownLeftRightResend( ImageConsumer ic) {

	System.err.println( this + 
			    ": hmmm, requestTopDownLeftRightResend called by " + ic);
	// a call to newPixels() would be the obvious implementation, but
	// that's not quite right if we have more consumers!
	removeConsumer( ic);
	addConsumer( ic);
    }

    abstract public int getMaxSliceNumber();

    /** @return original (not common) step orthogonal to this slice,
        in world coords */
    abstract public float getOrthoStep();

    /** for the private use of positionChanged() in the 3 subclasses (using 
	the same Point3Dint object everytime is easier on the heap...) */
    protected Point3Dint new_voxel_pos= new Point3Dint(); 

    // required by the PositionListener interface
    abstract public void positionChanged( PositionEvent new_position);
    
    /** BEWARE: we only store the reference to the IndexColorModel returned
	by e.getColormap() , i.e. we do _not_ make a copy of it. Hence,
	it's expected that this IndexColorModel is not changed until we
	receive another ColormapEvent!
    */
    synchronized public void colormapChanged( ColormapEvent e ) {

	colormap= e.getColormap();
	// send another frame (i.e. update the image)
	newPixels( slice_data, colormap, 0, slice_width);
    }

    synchronized void sliceDataUpdated( int which_slice ) {
	
	if( DEBUG ) {
	    System.out.println( this + "#sliceDataUpdated: ");
	    System.out.println( "   which_slice= " + which_slice);
	    System.out.println( "   crt_slice= " + crt_slice);
	}
	// did we already moved to another slice? 
	if( which_slice == crt_slice ) 
	    // if not, update screen!
	    _getNewSliceData( false);
    }

    abstract /*private*/ void _getNewSliceData( boolean future_notification);

}

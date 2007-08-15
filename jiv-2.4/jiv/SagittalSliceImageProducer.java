
// $Id: SagittalSliceImageProducer.java,v 1.5 2002/04/24 14:31:56 cc Exp $
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

import java.awt.image.*;

/**
 * A <code>SliceImageProducer</code> customized for "sagittal"
 * (X=constant) 2D slices.
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: SagittalSliceImageProducer.java,v 1.5 2002/04/24 14:31:56 cc Exp $ 
 */
public final class SagittalSliceImageProducer extends SliceImageProducer {

    public SagittalSliceImageProducer( Data3DVolume data_volume, 
				       int default_slice, 
				       IndexColorModel default_colormap ) {

	super( default_slice, 
	       new byte[ data_volume.getYSize() * data_volume.getZSize()],
	       data_volume.getYSize(), 
	       data_volume.getZSize(),
	       default_colormap,
	       data_volume);
	_getNewSliceData( true);
    }

    // NB: any changes here should also be made 
    // in the other two subclasses of SliceImageProducer!

    public final int getMaxSliceNumber() { return data_volume.getXSize()-1; }

    public final float getOrthoStep() { return data_volume.getXStep(); }

    synchronized public final void positionChanged( PositionEvent new_position) {
	
	if( !new_position.isXValid()) 
	    return;
	    
	if( DEBUG) 
	    System.out.println( this + " new x: " + new_position.getX());

	CoordConv.world2voxel( new_voxel_pos, new_position.getX(), 0, 0);
	// don't update the image if we don't have to...
	if( crt_slice == new_voxel_pos.x) 
	    return;

	crt_slice= new_voxel_pos.x;
	_getNewSliceData( true);
    }

    /*private*/ final void _getNewSliceData( boolean future_notification) {

	// reuse the existing slice_data array!
	data_volume.getSagittalSlice( crt_slice, slice_data,
				      future_notification ? this : null );

	// Send another frame (i.e. update the image);
	// this version of MemoryImageSource::newPixels() will send the
	// data presently found in 'slice_data' (it stores a ref internally)
	newPixels();
    }

}


// $Id: SagittalSlice2DViewport.java,v 1.2 2001/10/04 19:26:31 cc Exp $
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
 * A <code>Slice2DViewport</code> customized for "sagittal"
 * (X=constant) slices.
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: SagittalSlice2DViewport.java,v 1.2 2001/10/04 19:26:31 cc Exp $ 
 */
public final class SagittalSlice2DViewport extends Slice2DViewport {

    // NB: any changes in this class should also be made
    // in the other two subclasses of Slice2DViewport !

    public SagittalSlice2DViewport( ImageProducer ip, 
				    PositionListener pos_listener_for_ip,
				    Point3Dfloat initial_world_cursor ) {
	super( ip, pos_listener_for_ip, 
	       new Point3Dfloat( initial_world_cursor.y, // vport horiz
				 initial_world_cursor.z, // vport vert
				 initial_world_cursor.x ) // ortho to vport
		   );
    }

    synchronized final public void positionChanged( PositionEvent e ) {

	if( e.isXValid())
	    // NB: this assumes that our ImageProducer also received this
	    // event, and will update the supplied image accordingly...
	    cursor.z= e.getX();

	if( e.isYValid() || e.isZValid())
	    _newCursor( e.isYValid() ? e.getY() : cursor.x, 
			e.isZValid() ? e.getZ() : cursor.y, 
			false);
    }

    final protected void _firePositionEvent( final int changed_coords_mask) {
	
	int translated_mask= 0;
	if( (changed_coords_mask & PositionEvent.X) != 0) 
	    translated_mask |= PositionEvent.Y;
	if( (changed_coords_mask & PositionEvent.Y) != 0) 
	   translated_mask |= PositionEvent.Z;
	if( (changed_coords_mask & PositionEvent.Z) != 0) 
	   translated_mask |= PositionEvent.X;

	/* Note: the PositionEvent constructor makes a _copy_
	   of its last 3 arguments (the cursor) */
	__aid_to_firePositionEvent( new PositionEvent( this, 
						       translated_mask, 
						       cursor.z,
						       cursor.x,
						       cursor.y));
    }

    final protected void _voxel2world( Point3Dfloat world, 
				       float vx, float vy, float vz) {
	CoordConv.voxel2world( world, vz, vx, vy);
	final float real_x= world.x;
	final float real_y= world.y;
	final float real_z= world.z;
	world.x= real_y; world.y= real_z; world.z= real_x;
    }

    final protected void _world2voxel( Point3Dint voxel,
				       float wx, float wy, float wz) {
	CoordConv.world2voxel( voxel, wz, wx, wy);
	final int real_x= voxel.x;
	final int real_y= voxel.y;
	final int real_z= voxel.z;
	voxel.x= real_y; voxel.y= real_z; voxel.z= real_x;
    }
}

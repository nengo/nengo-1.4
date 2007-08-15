
// $Id: CoordConv.java,v 1.3 2001/10/02 01:27:09 cc Exp $
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

/**
 * Functions for converting 3D positions between the "voxel" (array
 * index) and "world" coordinate systems.  All methods (functions) are
 * currently <code>static</code> because, for now, the coordinate
 * transformation is the same for all image volumes, and it doesn't
 * change after the initialization.
 *
 * Note: currently, this class is simply a dumb wrapper around the
 * methods from VolumeHeader -- it is only kept for convenience.
 *
 * @see VolumeHeader
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: CoordConv.java,v 1.3 2001/10/02 01:27:09 cc Exp $ 
 */
public final class CoordConv {

    static /*private*/ VolumeHeader sampling= new VolumeHeader();


    /** Changes the global (common) voxel-to-world transformation. The
        default is the one corresponding to the default
        VolumeHeader(void) constructor.  */
    static final public void set( VolumeHeader new_sampling ) {

	CoordConv.sampling= new VolumeHeader( new_sampling);
    }


    /* ** world2voxel ** 
     */

    static final public Point3Dint world2voxel( float x, float y, float z) {
	return sampling.world2voxel( x, y, z);
    }

    static final public void world2voxel( Point3Dint voxel, 
					  float wx, float wy, float wz) {
	sampling.world2voxel( voxel, wx, wy, wz);
    }

    static final public Point3Dint world2voxel( Point3Dfloat world) {
	return sampling.world2voxel( world);
    }

    static final public void world2voxel( Point3Dint voxel, 
					  Point3Dfloat world) {
	sampling.world2voxel( voxel, world);
    }

    /* ** voxel2world ** 
     */

    static final public Point3Dfloat voxel2world( int x, int y, int z) {
	return sampling.voxel2world( x, y, z);
    }

    static final public void voxel2world( Point3Dfloat world, 
					  int vx, int vy, int vz) {
	sampling.voxel2world( world, vx, vy, vz);
    }

    static final public Point3Dfloat voxel2world( float x, float y, float z) {
	return sampling.voxel2world( x, y, z);
    }

    static final public void voxel2world( Point3Dfloat world, 
					  float vx, float vy, float vz) {
	sampling.voxel2world( world, vx, vy, vz);
    }

    static final public Point3Dfloat voxel2world( Point3Dint voxel) {
	return sampling.voxel2world( voxel);
    }

    static final public void voxel2world( Point3Dfloat world,
					  Point3Dint voxel) {
	sampling.voxel2world( world, voxel);
    }

}


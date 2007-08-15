
// $Id: Point3Dfloat.java,v 1.1 2001/04/08 00:04:28 cc Exp $
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
 * A tuple of (x,y,z) floating point coordinates.
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: Point3Dfloat.java,v 1.1 2001/04/08 00:04:28 cc Exp $
 */
public final class Point3Dfloat {

    public float x;
    public float y;
    public float z;

    public Point3Dfloat() {}

    public Point3Dfloat( float x, float y, float z) {
	
	this.x= x;
	this.y= y;
	this.z= z;
    }

    public Point3Dfloat( Point3Dfloat src) {

	this( src.x, src.y, src.z);
    }

    public final void copyInto( Point3Dfloat dest) {
	dest.x= x;
	dest.y= y;
	dest.z= z;
    }

    public String toString() {

	return "(" + x + "," + y + "," + z + ")";
    }
}

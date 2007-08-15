
// $Id: PositionEvent.java,v 1.1 2001/04/08 00:04:28 cc Exp $
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

import java.util.*;

/**
 * An event type used for communicating position (cursor) changes, as
 * "world" coordinates. <br> <i>Beware:</i> for efficiency reasons,
 * the receivers (listeners) of <code>PositionEvents</code> are not
 * required to check the range of the new position received.  Thus, it
 * is sender's responsibility not to send positions outside the valid
 * range!
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: PositionEvent.java,v 1.1 2001/04/08 00:04:28 cc Exp $ 
 *
 * @see PositionListener
 */
public final class PositionEvent extends EventObject {

    /*private*/ float position_x;
    /*private*/ float position_y;
    /*private*/ float position_z;
    /*private*/ int valid_mask;

    /** bit masks for the 'changed_fields_mask' argument (which should be
	the logical '|' of all the relevant bit masks */
    public static final int X= 0x01;
    public static final int Y= 0x02;
    public static final int Z= 0x04;

    public PositionEvent( Object source, int valid_fields_mask, 
			  Point3Dfloat position) {

	// simply invoke the other constructor...
	this( source, valid_fields_mask, position.x, position.y, position.z);
    }

    public PositionEvent( Object source, int valid_fields_mask, 
			  float new_x, float new_y, float new_z ) {

	super( source);
	this.valid_mask= valid_fields_mask;
	this.position_x= new_x;
	this.position_y= new_y;
	this.position_z= new_z;
    }

    final public int getFieldsMask() { return valid_mask;}
    final public boolean isXValid() { return 0 != ( valid_mask & X);}
    final public boolean isYValid() { return 0 != ( valid_mask & Y);}
    final public boolean isZValid() { return 0 != ( valid_mask & Z);}
    final public float getX() { return position_x;}
    final public float getY() { return position_y;}
    final public float getZ() { return position_z;}

    /** if possible, 
	this version should be avoided (use the other getXYZ()) since
	it's hard on the memory manager: it creates a new Point3Dfloat
	object at each invocation... */
    final public Point3Dfloat getXYZ() { 
	return new Point3Dfloat( position_x, position_y, position_z);
    }

    final public void getXYZ( Point3Dfloat result) { 
	result.x= position_x;
	result.y= position_y;
	result.z= position_z;
    }

    final public String toString() {
	return "PositionEvent [mask=" + valid_mask + 
	    ",pos=" + getXYZ() + 
	    "] source=" + source;
    }
}


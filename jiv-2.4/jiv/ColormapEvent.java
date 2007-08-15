
// $Id: ColormapEvent.java,v 1.1 2001/04/08 00:04:27 cc Exp $
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
import java.awt.image.*;

/** 
 * An event type used for communicating colormap changes.
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: ColormapEvent.java,v 1.1 2001/04/08 00:04:27 cc Exp $
 */
public final class ColormapEvent extends EventObject {

    /*private*/ IndexColorModel colormap;

    public ColormapEvent( Object source, IndexColorModel colormap) {

	super( source);
	this.colormap= colormap;
    }

    final public IndexColorModel getColormap() { return colormap;}

    final public String toString() {

	return "ColormapEvent [IndexColorModel=" + getColormap() + "] source=" + source;
    }
}


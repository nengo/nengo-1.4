
// $Id: PositionListener.java,v 1.2 2002/04/24 14:31:56 cc Exp $
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
 * The interface that all <code>PositionEvent</code> listener
 * (receiver) classes must implement.
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: PositionListener.java,v 1.2 2002/04/24 14:31:56 cc Exp $ 
 *
 * @see PositionEvent
 */
public interface PositionListener extends EventListener {

    /** this is the "callback" used to actually deliver the event */
    void positionChanged( PositionEvent e );

    /** the implementing class has to return some meaningful (ie >0) value 
	only if it actually produces slice data... */
    int getMaxSliceNumber();

    float getOrthoStep();
}


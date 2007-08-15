
// $Id: PositionListenerAdapter.java,v 1.2 2002/04/24 14:31:56 cc Exp $
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
 * An adapter (convenience) class for receiving
 * <code>PositionEvent</code>-s.  Listener classes that don't produce
 * slice data don't have to implement the
 * <code>getMaxSliceNumber</code> and <code>getOrthoStep</code> methods.
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: PositionListenerAdapter.java,v 1.2 2002/04/24 14:31:56 cc Exp $ 
 */
public abstract class PositionListenerAdapter implements PositionListener {

    abstract public void positionChanged( PositionEvent e );

    /** the implementing class has to return some meaningful (ie >0) value 
	only if it actually produces slice data... */
    public int getMaxSliceNumber() { return -1; }

    public float getOrthoStep() { return Float.NaN; }
}


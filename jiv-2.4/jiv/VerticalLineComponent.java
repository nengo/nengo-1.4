
// $Id: VerticalLineComponent.java,v 1.1 2001/04/08 00:04:28 cc Exp $
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

/**
 * A stretchable vertical line (useful as a separator). It's
 * recommended to only stretch it in the vertical direction, because
 * the line will always be drawn at the x=0 horizontal position.
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: VerticalLineComponent.java,v 1.1 2001/04/08 00:04:28 cc Exp $ 
 */
public final class VerticalLineComponent extends Panel {

    static final /*private*/ boolean 	DEBUG= false;
    static final /*private*/ Dimension 	MINIMUM_SIZE= new Dimension( 1, 1);
    
    final public Dimension getMinimumSize() { return MINIMUM_SIZE; }

    final public Dimension getPreferredSize() { return getMinimumSize(); }

    final public void paint( Graphics g) { update( g); }

    final public void update( Graphics g) { 
	if( DEBUG) 
	    System.out.println( this + " update(): getSize()=" + getSize());

	g.setColor( getForeground());
	g.drawLine( 0, 0, 0, getSize().height-1);
    }
}



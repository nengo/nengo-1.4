
// $Id: ViewportCursor.java,v 1.1 2001/04/08 00:04:28 cc Exp $
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

/* Note: currently this is _not_ thread safe... */

/**
 * A graphical position cursor. Currently, the cursor is drawn as a
 * cross-hair with alternating red and blue lines (to improve its
 * visibility over various backgrounds).
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: ViewportCursor.java,v 1.1 2001/04/08 00:04:28 cc Exp $ 
*/
public final class ViewportCursor {

    /*private*/ int x; /** the crt cursor X position (viewport coordinates) */
    /*private*/ int y; /** the crt cursor Y position (viewport coordinates) */

    /**
     * Class constructor.
     *
     * @param x initial cursor X viewport coordinate
     * @param y initial cursor Y viewport coordinate
     */
    public ViewportCursor( int x, int y) { this.x= x; this.y= y; }

    /**
     * Class constructor.
     *
     * @param p initial cursor (X,Y) viewport coordinates
     */
    public ViewportCursor( Point p) { this( p.x, p.y); }

    /**
     * @param x new cursor X viewport coordinate
     * @param y new cursor Y viewport coordinate
     */
    final public void setPosition( final int x, final int y) {
	this.x= x; this.y= y;
    }

    /**
     * @param p new cursor (X,Y) viewport coordinates
     */
    final public void setPosition( final Point p) {
	this.x= p.x; this.y= p.y;
    }

    /**
     * @param result (reference to) <code>Point</code> object where
     * the current viewport cursor position should be stored 
     */
    final public void getPosition( Point result) {
	result.x= x; result.y= y;
    }

    /** start offset (from hair-cross center) for cursor's stripes, in pixels */
    /*private*/ static final int START_OFFSET= 	3;
    /** end offset (from hair-cross center) for cursor's stripes, in pixels */
    /*private*/ static final int END_OFFSET= 	13;

    /**
     * @param result (reference to) <code>Rectangle</code> object
     * where the current cursor's bounding box should be stored 
     */
    final public void getBounds( Rectangle result) {

	result.x= x - END_OFFSET;
	result.y= y - END_OFFSET;
	result.width= 2*END_OFFSET + 1;
	result.height= 2*END_OFFSET + 1;
    }

    /**
     * Draws a graphical cursor at the current cursor viewport
     * coordinates. Currently, the cursor is drawn as a cross-hair
     * with alternating red and blue lines (to improve its
     * visibility over various backgrounds).
     * 
     * @param gr the graphics context to draw on 
     */
    final public void draw( final Graphics gr) {

	/* TODO: since the cursor is always the same size,
	   pre-rendering it into a small image with transparent bg
	   _might_ lead to faster updates?  
	*/
	// optimization: use local ("stack") var-s for speed
	final int x= this.x;
	final int y= this.y;

	gr.setColor( Color.red);
	gr.drawLine( x - END_OFFSET, y, 
		     x - START_OFFSET, y );
	gr.drawLine( x + START_OFFSET, y, 
		     x + END_OFFSET, y );
	gr.drawLine( x, y - END_OFFSET,
		     x, y - START_OFFSET );
	gr.drawLine( x, y + START_OFFSET, 
		     x, y + END_OFFSET );
	gr.setColor( Color.blue);
	gr.drawLine( x - END_OFFSET, y + 1, 
		     x - START_OFFSET, y + 1 );
	gr.drawLine( x + START_OFFSET, y + 1, 
		     x + END_OFFSET, y + 1 );
	gr.drawLine( x - END_OFFSET, y - 1, 
		     x - START_OFFSET, y - 1 );
	gr.drawLine( x + START_OFFSET, y - 1, 
		     x + END_OFFSET, y - 1 );
	gr.drawLine( x + 1, y - END_OFFSET, 
		     x + 1, y - START_OFFSET );
	gr.drawLine( x + 1, y + START_OFFSET, 
		     x + 1, y + END_OFFSET );
	gr.drawLine( x - 1, y - END_OFFSET, 
		     x - 1, y - START_OFFSET );
	gr.drawLine( x - 1, y + START_OFFSET, 
		     x - 1, y + END_OFFSET );
    }

} // end of class ViewportCursor



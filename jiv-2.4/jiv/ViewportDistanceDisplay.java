
// $Id: ViewportDistanceDisplay.java,v 1.3 2001/10/27 15:27:25 cc Exp $
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
 * Graphically displays a "distance measurement" rubber band, complete
 * with a supplied text. The text is intelligently placed and
 * justified, such that it won't overlap with the rubber band.
 * <br><i>NB:</i> the current implementation assumes that the default
 * drawing font won't change after the initialization.
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: ViewportDistanceDisplay.java,v 1.3 2001/10/27 15:27:25 cc Exp $ 
 */
public final class ViewportDistanceDisplay {

    /** Controls weather this class will draw in "xor" mode or in the
        (normal) "paint" mode.  The paint mode generally looks better,
        but, of course, it's not guaranteed to be visible above all
        background colors... */
    /*private*/ static final boolean USE_XOR_MODE= true;

    /** usually set to the predominant background color (?) */
    /*private*/ static final Color   XOR_COLOR= Color.black;
    /*private*/ static final Color   DRAW_COLOR= Color.white; // yellow is also good...
    /** radius (in pixels) of the circle used to mark the start
        position for the measurement */
    /*private*/ static final int     START_MARKER_RADIUS= 7;
    /** clearance (in pixels) of the text label from the start marker */
    /*private*/ static final int     LABEL_CLEARANCE= 4;

    /** viewport coordinates of the measurement start point ("origin") */
    /*private*/ Point 		     start;
    /** viewport coordinates of the measurement end point */
    /*private*/ Point 		     end;
    /** text to be displayed along with the distance measurement
        "rubber band" */
    /*private*/ String		     label= String.valueOf( Float.NaN);
    /** metrics of the default drawing font */
    /*private*/ FontMetrics	     font_metrics;

    /** 
     * Class constructor specifying an initial start point position
     * ("origin"). The end point is initialized to the same position.
     *
     * @param start_x initial start point X viewport coordinate
     * @param start_y initial start point Y viewport coordinate
     *
     * @param font_metrics Metrics of the default drawing
     * font. <i>NB:</i> the current implementation assumes that the
     * default drawing font won't change after the initialization.  
     */
    public ViewportDistanceDisplay( int start_x, int start_y, 
				    FontMetrics font_metrics) {

	start= new Point( start_x, start_y);
	end= new Point( start);
	this.font_metrics= font_metrics;
    }
    
    /** 
     * Class constructor.
     *
     * @param font_metrics Metrics of the default drawing
     * font. <i>NB:</i> the current implementation assumes that the
     * default drawing font won't change after the initialization.  
     */
    public ViewportDistanceDisplay( FontMetrics font_metrics) {

	/* the initial start point defaults to an obviously invalid
           value ... */
	this( -10000, -10000, font_metrics);
    }
    
    /** working (temporary) variable used by <code>getBounds</code> */
    /*private*/ Rectangle2 	     bounds= new Rectangle2();
    /** working (temporary) variable used by <code>getBounds</code> */
    /*private*/ Rectangle 	     xtra_bounds= new Rectangle();

    /**
     * @param result (reference to) <code>Rectangle</code> object
     * where the bounding box for the current graphical output (as it
     * would be produced by <code>draw( Graphics)</code>) should be
     * stored
     * @see #draw( Graphics) 
     */
    final public void getBounds( Rectangle result) {

	// optimization: use local ("stack") var-s for speed
	final Point start= this.start;
	final Point end= this.end;

	bounds.setBounds( start.x - START_MARKER_RADIUS, start.y - START_MARKER_RADIUS, 
			  2*START_MARKER_RADIUS + 1, 2*START_MARKER_RADIUS + 1);
	xtra_bounds.setBounds( Math.min( start.x, end.x), 
			       Math.min( start.y, end.y),
			       1 + Math.abs( start.x - end.x), 
			       1 + Math.abs( start.y - end.y)
			       );
	bounds.expandToInclude( xtra_bounds);
	xtra_bounds.setBounds( _getLabelX(),
			       start.y - font_metrics.getMaxAscent(),
			       1 + _getLabelWidth(),
			       1 + start.y + font_metrics.getMaxDescent()
			       );
	bounds.expandToInclude( xtra_bounds);
	result.setBounds( bounds);
    }
    
    /**
     * @param label new text label to be displayed
     */
    final public void setLabel( final float label) {
	
	/* Note: this is heavy on the memory (heap) manager, but I
           cannot think of any solution that avoids allocating a new
           String object every time the label is changed... :( */

	/* FIXME: there's a formatting issue here -- what if the world
	   coord steps are v small (unlikely for typical brain images,
	   but...) ?  TODO: display with a precision of about 1/10 of
	   the (common_sampling) step ... */

	// only display the 2 most significant fractional digits...
	this.label= String.valueOf( Util.chopFractionalPart( label, 2));
    }

    /**
     * @param x new X viewport coordinate of the measurement start point ("origin")
     * @param y new Y viewport coordinate of the measurement start point ("origin") */
    final public void setStartPosition( final int x, final int y) {
	start.x= x; start.y= y;
    }

    /**
     * @param p new viewport coordinates of the measurement start point ("origin")
     */
    final public void setStartPosition( final Point p) {
	setStartPosition( p.x, p.y);
    }

    /**
     * @param x new X viewport coordinate of the measurement end point
     * @param y new Y viewport coordinate of the measurement end point
     */
    final public void setEndPosition( final int x, final int y) {
	end.x= x; end.y= y;
    }

    /**
     * @param p new viewport coordinates of the measurement end point
     */
    final public void setEndPosition( final Point p) {
	setEndPosition( p.x, p.y);
    }

    /**
     * Draws a graphical representation of the "distance measurement",
     * complete with a text string. 
     * 
     * @param gr the graphics context to draw on 
     */
    final public void draw( final Graphics gr) {

	if( USE_XOR_MODE) gr.setXORMode( XOR_COLOR);
	gr.setColor( DRAW_COLOR);

	// optimization: use local ("stack") var-s for speed
	final Point start= this.start;
	final Point end= this.end;

	gr.drawOval( start.x - START_MARKER_RADIUS, start.y - START_MARKER_RADIUS, 
		     2*START_MARKER_RADIUS, 2*START_MARKER_RADIUS);
	gr.drawLine( start.x, start.y, end.x, end.y);
	// the supplied (x,y) is for the bottom-left corner of the string
	gr.drawString( label, _getLabelX(), start.y);

	if( USE_XOR_MODE) gr.setPaintMode();	// undo XOR painting mode
    }

    /**
     * @return X screen position for the left end of the string,
     * computed such that the text won't overlap with the distance
     * "rubber band" 
     */
    final /*private*/ int _getLabelX() {

	if( start.x >= end.x) {
	    /* line is on the left of the start marker; draw text on the right */
	    return start.x + START_MARKER_RADIUS + LABEL_CLEARANCE;
	}
	else {
	    /* line is on the right of the start marker; draw text on the left */
	    return 
		start.x - (START_MARKER_RADIUS + LABEL_CLEARANCE) - _getLabelWidth();
	}
    }

    /**
     * @return screen width of the current text label
     */
    final /*private*/ int _getLabelWidth() {

	/* PROBLEM: the Java AWT 1.1 API doesn't provide any way of
           obtaining the true bounding box of a rendered string; it
           only gives the "advance width" of a character (i.e. how
           much next character's origin is advanced) which is less
           than the actual width of a character for italic (slanted)
           fonts, etc. Hopefully, the advance width of 'w' will be
           more than the character bounding boxes overlap for any
           italic font... */
	return font_metrics.stringWidth( label) + font_metrics.charWidth( 'w');
    }

} // end of class ViewportDistanceDisplay



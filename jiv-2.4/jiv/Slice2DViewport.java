
// $Id: Slice2DViewport.java,v 1.7 2003/12/21 17:32:17 crisco Exp $
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
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;

/**
 * Provides the orientation-independent functionality of displaying 2D
 * slice image data in a viewport, and of allowing the user to
 * interact with it. The orientation-specific functionality is in its
 * 3 direct subclasses: <code>TransverseSlice2DViewport</code>,
 * <code>CoronalSlice2DViewport</code>, and
 * <code>SagittalSlice2DViewport</code>.
 *
 * In this class, all the references to x,y,z are not to the real
 * x,y,z but to "virtual" (private) ones: x is horizontal, y is
 * vertical, z is orthogonal to the slice plane.  The "translation"
 * between our private x,y,z and the true ones in the outside world is
 * done by the abstract methods: <code>positionChanged</code>,
 * <code>_firePositionEvent</code>, <code>_world2voxel</code> and
 * <code>_voxel2world</code> which are implemented differently by the
 * 3 different subclasses.
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: Slice2DViewport.java,v 1.7 2003/12/21 17:32:17 crisco Exp $ 
 */
abstract public class Slice2DViewport extends Panel 
    implements PositionListener, PositionGenerator {

    /** For development/testing only. Should be set to false in
        production code. */
    protected static final boolean DEBUG= false;

    /** If true, all <code>update()</code> operations will be
	double-buffered.  If false, only double-buffer the operations
	that require clearing some viewport area.  This will speed up
	a little the in-slice cursor movements (except those next to
	the boundaries), but will produce a bit of cursor flickering,
	which gets significantly worse as the scale factor increases. 
    */
    /*private*/ static final boolean ALWAYS_DOUBLE_BUFFER= true;

    /** If true, the image will be prescaled using a
	ReplicateScaleFilter (obtained via a call to
	Image#getScaledInstance()). This should speed things up when
	<code>scale_factor</code> and slice data don't change,
	e.g. for in-plane cursor movements and pan operations.
	BEWARE: there's a serious bug/memory-leak in
	ReplicateScaleFilter -- the java process size grows to
	ridiculous amounts (although the heap size reported by JVM
	stays normal!).

	If false, the built-in scaling capabilities of
	Image#drawImage() will be used. However, this means that you
	have no control on the actual scaling algo used (although
	sun/jdk-based java implementations seem to use "replicate" as
	the default scaling filter). 

	TODO: currently, the entire image is zoomed up, even if only a
	small portion of it will end up being displayed...  rewrite
	the coordinate system handling to avoid that.  
    */
    /*private*/ static final boolean USE_SEPARATE_SCALE_FILTER= false;

    /** If true, use the "new" (Java 1.1) 
	Image#drawImage( Image, 8xint, ...) methods for rendering the
	image to the screen. Otherwise, use the "older" (Java 1.0) 
	Image#drawImage().
    */
    /*private*/ static final boolean USE_NEW_DRAW_IMAGE= false;

    /** Compile-time option to control whether we should call
        consume() on the InputEvent-s that we dealt with; peers ignore
        "consumed" events */
    /*private*/ static final boolean CONSUME_INPUT_EVENTS= true;
    /** For development/testing only. Should be set to false in
        production code. */
    /*private*/ static final boolean DEBUG_INPUT_EVENTS= false;

    /** Mask specifying the "other/secondary" mouse button (i.e. not
     the main/primary one). */
    public static final int OTHER_BUTTON_MASK= 
	MouseEvent.BUTTON2_MASK | MouseEvent.BUTTON3_MASK;
    /** Mask specifying the mouse button "modifier" key(s). */
    public static final int BUTTON_MODIFIER_MASK= 
	MouseEvent.SHIFT_MASK | MouseEvent.CTRL_MASK;

    /*private*/ static int MAX_SCALE_FACTOR;
    static {
	try {
	    String os= System.getProperty( "os.name").trim().toLowerCase();
	    if( os.startsWith( "windows 95") || os.startsWith( "windows 98")) {
		MAX_SCALE_FACTOR= 10;
		System.out.println( "Windows 95/98 OS detected: " +
				    "max zoom factor limited to 10.");
	    }
	    else
		/* impose a upper limit anyway, because the current
                   implementation uses a good deal of memory when
                   doing very large zooms... (TODO: fix this,
                   i.e. implement the new coordinate system!) */
		MAX_SCALE_FACTOR= 25; 
	}
	catch( Exception e) { 
	    // if the JVM is Java 1.1 compliant, we should never get here...
	    MAX_SCALE_FACTOR= 10; 
	    System.out.println( "unknown OS: max zoom factor limited to 10.");
	}
	if( DEBUG) System.out.println( "MAX_SCALE_FACTOR: " + MAX_SCALE_FACTOR);
    }

    /* ************ ************
       these are initialized in the constructor and never changed after that: 
    */
    /** the ImageProducer for <code>original_image</code> */
    /*private*/ ImageProducer	 image_source;
    /** the dimension orthogonal to this viewport (- 1) */
    /*private*/ int 		 max_slice_number; 
    /** the original (not common) step orthogonal to this viewport, in world coords */
    /*private*/ float 		 ortho_step; 
    /** the Image to be displayed; NB: it's a multiframe image! */
    /*private*/ Image		 original_image; 
    /*private*/ int		 original_image_width;
    /*private*/ int 	 	 original_image_height;
    /* ************ ************ */

    /** list of external PositionListener-s who want to be notified of
        cursor position changes originating from this viewport */
    /*private*/ Vector 		 event_listeners;

    /*private*/ Dimension	 preferred_size; /** @see #getPreferredSize */

    /** Current viewport dimensions. */
    /* Beware! when changing this, also change the
       <code>offscreen_{buffer,gc}</code> variables below! */
    /*private*/ Dimension	 vport_dims; 

    /** These are coordinates in viewport (display) space, for
	efficiency.  Chose to use this space over image space because
	most of the time we'll need viewport coords; the only
	situation when we need image space is when the viewport is
	resized (see <code>doLayout</code>), and that's a very
	infrequent event.  */
    /*private*/ Point     image_origin= new Point( 0, 0);

    /** Equal to: original_image_width x scale_factor. Used for saving
        some cycles... */
    /*private*/ int	  scaled_image_width; 
    /** Equal to: original_image_height x scale_factor. Used for saving
        some cycles... */
    /*private*/ int	  scaled_image_height; 
    /** for range checking when zooming out */
    /*private*/ int    	  min_scaled_image_width;
    /** for range checking when zooming in */
    /*private*/ int    	  max_scaled_image_width;
    /** how much should the original image be scaled for display */
    /*private*/ double    scale_factor;

    /** Stores the pre-scaled slice image. Only used when
        USE_SEPARATE_SCALE_FILTER is true (on). */
    /*private*/ Image     scaled_image;

    /** Used for double-buffering the screen updates.  Initialized in
	update(), which is the safest place to do it (because
	Component#createImage() will return null if neither
	Component's peer, neither its Container's peer, does exist;
	peers may not exist before the frames, etc are actually pop'ed
	up on the display...). doLayout() updates it every time
	vport_dims is changed.  */
    /*private*/ Image 	 offscreen_buffer= null;
    /** @see #offscreen_buffer */
    /*private*/ Graphics offscreen_gc;
    
    /** current cursor in world coordinates (3D) */
    protected Point3Dfloat cursor;
    /** The (smart) cross-hair position cursor. Initialized to some
        position clearly outside the viewport, such that it'll be
        obvious if it's not further updated to the proper initial
        position... */
    /*private*/ ViewportCursor vport_cursor= new ViewportCursor( -100, -100);

    /** used in calculating the effect of "mouse drags" (in viewport
        coordinates) */
    /*private*/ Point last_position= new Point();

    /** origin for interactive distance measurements (in world coords).
	'null' means that this interactive feature is off.
	NB: the Z coordinate is (currently) not used!! */
    /*private*/ Point3Dfloat 		distance_origin= null;
    /** Graphical display of the distance measurement. Initialized by
        the first call to <code>startNewDistanceMeasurement</code>. */
    /*private*/ ViewportDistanceDisplay distance_display= null;


    /**
     * Class constructor.
     *
     * @param ip <code>ImageProducer</code> that will be used for
     * feeding <code>original_image</code> (i.e. the 2D image to be
     * displayed).
     * @param pos_listener_for_ip <code>PositionListener</code> that
     * will be used for requesting another slice (2D image) from
     * <code>ip</code>.  
     * @param cursor object that will be used to store the current
     * cursor position (in world coordinates); should be initialized
     * to the desired starting position.
     */
    protected Slice2DViewport( ImageProducer ip, 
			       PositionListener pos_listener_for_ip,
			       Point3Dfloat cursor ) {

	this.cursor= cursor;
	addPositionListener( pos_listener_for_ip); 
	// need to do this if we want to make sure we start with the 
	// slice at the initial 'cursor' value specified above...
	//   (we OR all the masks in order to cover all 3 possible 
	//    subclasses who will call this; this is ok because at this
	//    point we only have 1 listener, which will only look at the
	//    one coordinate it cares about...)
	_firePositionEvent( PositionEvent.X | PositionEvent.Y | PositionEvent.Z);

	original_image= createImage( image_source= ip);
	max_slice_number= pos_listener_for_ip.getMaxSliceNumber();
	ortho_step= pos_listener_for_ip.getOrthoStep();
	original_image_width= original_image.getWidth( this);
	original_image_height= original_image.getHeight( this);

	max_scaled_image_width= original_image_width * MAX_SCALE_FACTOR;
	// this makes sure that none of the dimensions can be less than 1 pixel
	min_scaled_image_width= 1 * 
	    ( (original_image_width < original_image_height) ?
	      ( 1) :
	      (int)Math.ceil( original_image_width/(double)original_image_height) );
	if( DEBUG) 
	    System.out.println( this + 
				" min_scaled_image_width:" + min_scaled_image_width);

	/* Note: The only display-related instance field not properly
	   initialized here is <code>image_origin</code> : however,
	   that's Ok because <code>doLayout()</code> will be called
	   for sure sometime during the initial validation of this
	   panel, and <code>doLayout()</code> will compute the proper
	   (i.e. centered) <code>image_origin</code>.  But
	   <code>vport_dims</code>, <code>scaled_image_{width,height}</code>,
	   <code>scale_factor</code> have to be initialized here
	   because <code>doLayout()</code> needs their previous values
	   in its computations...  */
	preferred_size= new Dimension( original_image_width, original_image_height);
	vport_dims= new Dimension( preferred_size);
	scaled_image_width= _cappedScaledImageWidth( vport_dims.width);
	scaled_image_height= 
	    scaled_image_width * original_image_height / original_image_width;
	scale_factor= ((double)scaled_image_width) / ((double)original_image_width);

	_updateVportCursorPosition();

	enableEvents( AWTEvent.KEY_EVENT_MASK | 
		      AWTEvent.MOUSE_EVENT_MASK |
		      AWTEvent.MOUSE_MOTION_EVENT_MASK );
    }

    /** 
     * Helper method: validates a suggested value for
     * <code>scaled_image_width</code>; if the value is too low or too
     * high, it "caps" it accordingly.  
     *
     * @param value The suggested value.
     * @return valid (possibly "capped") value.
     */
    final /*private*/ int _cappedScaledImageWidth( int value) {

	if( value < min_scaled_image_width)
	    return min_scaled_image_width;
	if( value > max_scaled_image_width)
	    return max_scaled_image_width;
	return value;
    }

    /** 
     * Note: this needs to return 'true' if you want requestFocus() to
     * have any effect (this was not enforced in Java 1.1...)
     *
     * @see java.awt.Component#isFocusTraversable 
     */
    final public boolean isFocusTraversable() { return true; }

    /** 
     * Overrides <code>Component#getPreferredSize</code>. Necessary,
     * otherwise <code>Window#pack()</code> will squish us to a tiny
     * size.  
     *
     * @see java.awt.Component#getPreferredSize
     */
    final public Dimension getPreferredSize() { return preferred_size;}

    /** 
     * @return the vertical dimension of the original (source) image.
     */
    final public int getOriginalImageHeight() { return original_image_height; }

    /**
     * Called by the AWT input event delivery thread.
     * 
     * @param e The (user-produced) mouse event to process.
     *
     * @see #processMouseMotionEvent
     */
    final protected void processMouseEvent( MouseEvent e) {

	if( DEBUG || DEBUG_INPUT_EVENTS)
	    System.out.println( "processMouseEvent: " + e);

	switch( e.getID()) {

	case MouseEvent.MOUSE_ENTERED:
	    /* this is VERY IMPORTANT! otherwise you won't get any KeyEvent-s! */
	    if( DEBUG) { System.out.println( "requesting focus..."); }
	    requestFocus();
	    break;
	case MouseEvent.MOUSE_PRESSED:
	    if( 0 != (e.getModifiers() & 
		      (OTHER_BUTTON_MASK | BUTTON_MODIFIER_MASK)) 
		) {
		/* Note: better to use here getPoint() instead of getX() + getY()
		   because it's an atomic operation (we're modifying the value
		   of <code>last_position</code> which is an instance field, 
		   which is shared amoung threads...
		*/
		/* thread unsafe: */
		// this is needed by the 'drag' features!
		last_position.x= e.getX(); last_position.y= e.getY();

		if( e.getClickCount() > 1)
		    _clearDistanceMeasurement();
	    }
	    else {
		// == SET NEW CURSOR POSITION ==
		_newCursor( e.getX(), e.getY());

		if( e.getClickCount() > 1)
		    _startNewDistanceMeasurement();
		// see the comment at top of file (regarding CONSUME_INPUT_EVENTS)
		if( CONSUME_INPUT_EVENTS) e.consume(); 
	    }
	    break;
	}
	super.processMouseEvent( e);
    }

    /**
     * Called by the AWT input event delivery thread.
     * 
     * @param e The (user-produced) mouse motion event to process.
     *
     * @see #processMouseEvent
     */
    final protected void processMouseMotionEvent( MouseEvent e) {

	if( MouseEvent.MOUSE_DRAGGED == e.getID() ) {

	    if( DEBUG || DEBUG_INPUT_EVENTS) 
		System.out.println( "processMouseMotionEvent: " + e);

	    if( 0 != (e.getModifiers() & OTHER_BUTTON_MASK) ) {
		
		/* thread unsafe: */
		final int delta= last_position.y - e.getY();
		last_position.y= e.getY();

		if( 0 != (e.getModifiers() & BUTTON_MODIFIER_MASK) ) {
		    // == ZOOM ==
		    _doZoom( delta);
		}
		else {
		    // == CHANGE SLICE ==
		    final float multiplication= 0.5f;
		    _newSlice( delta * multiplication * ortho_step);
		}
	    }
	    else
		if( 0 != (e.getModifiers() & BUTTON_MODIFIER_MASK) ) {
		    // == PAN ==
		    final int crt_position_x= e.getX();
		    final int crt_position_y= e.getY();
		    /* optimization: this instance field is accessed many times
		       in here, so use a local ("stack") variable for speed */
		    final Point last_position= this.last_position;
		    image_origin.translate( crt_position_x - last_position.x,
					    crt_position_y - last_position.y);
		    // changed image_origin, so need to call this:
		    _updateVportCursorPosition();
		    /* thread unsafe: */
		    last_position.x= crt_position_x; last_position.y= crt_position_y;
		    repaint();
		}
		else {
		    // == SET NEW CURSOR POSITION (cursor dragged) ==
		    _newCursor( e.getX(), e.getY());
		}
	    // see the comment at top of file (regarding CONSUME_INPUT_EVENTS)
	    if( CONSUME_INPUT_EVENTS) e.consume();
	}
	super.processMouseMotionEvent( e);
    }

    /**
     * Called by the AWT input event delivery thread.
     * 
     * @param e The (user-produced) key event to process.
     */
    final protected void processKeyEvent( KeyEvent e) {

	if( DEBUG || DEBUG_INPUT_EVENTS)
	    System.out.println( "processKeyEvent: " + e);

	/* Note: under win32, holding down Shift or Ctrl generates a
	   stream of auto-repeating KET_PRESSED events...  TODO: is
	   there a way to avoid the performance hit this causes?  */

	if( KeyEvent.KEY_PRESSED == e.getID()) {
	    switch( e.getKeyChar() ) {

	    case '+': 
		_newSlice( ortho_step); break;
	    case '-': 
		_newSlice( -ortho_step); break;
	    case 'd': 
	    case 'D': 
		_startNewDistanceMeasurement(); break;
	    case 'c': 
	    case 'C':
		_clearDistanceMeasurement(); break;
	    default: 
		switch( e.getKeyCode() ) {

		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_UP: 
		    _newSlice( ortho_step); break;
		case KeyEvent.VK_LEFT: 
		case KeyEvent.VK_DOWN: 
		    _newSlice( -ortho_step); break;
		}
	    }
	    // see the comment at top of file (regarding CONSUME_INPUT_EVENTS)
	    if( CONSUME_INPUT_EVENTS) e.consume();
	}
	super.processKeyEvent( e);
    }

    /**
     * Changes the image magnification (scale) factor, by recomputing
     * the image position and dimension in the viewport such that the
     * field of view's center, in the original image space, remains at
     * the same viewport position. It respects the min/max scale
     * factors given by 'min_scaled_image_width' and
     * 'max_scaled_image_width'.
     *
     * @param delta The number of pixels the mouse was dragged in the
     * vertical direction; can be positive or negative.  
     */
    final /*private*/ void _doZoom( final int delta) {

	final int MULTIPLICATION= 3;
	if( DEBUG) System.out.println( "Zoom: " + delta);

	/* optimization: this instance field is accessed a lot in here, 
	   so use a local ("stack") variable for speed */
	int scaled_image_width= this.scaled_image_width;

	// all these are in viewport (display) coord space
	final int old_fov_left= Math.max( 0, image_origin.x);
	final int old_fov_right= Math.min( vport_dims.width, 
					   image_origin.x+scaled_image_width);
	final int old_fov_top= Math.max( 0, image_origin.y);
	final int old_fov_bottom= Math.min( vport_dims.height, 
					    image_origin.y+scaled_image_height);
	final int center_old_fov_x= old_fov_left + (old_fov_right - old_fov_left)/2;
	final int center_old_fov_y= old_fov_top + (old_fov_bottom - old_fov_top)/2;
	// for clarity, make aliases...
	final double old_scale_factor= scale_factor;
	final Point old_image_origin= image_origin;

	/* convert from vport to image coord space
	   (use double instead of int in order to keep truncation errors 
	   under control...) */
	final double center_fov_x=
	    ((center_old_fov_x - old_image_origin.x) / old_scale_factor);
	final double center_fov_y=
	    (center_old_fov_y - old_image_origin.y) / old_scale_factor;

	// do the zoom...
	scaled_image_width= 
	    _cappedScaledImageWidth( scaled_image_width + delta * MULTIPLICATION);
	/* Note: decided not to use Math.round here (a cast, or trunc,
	   is what we do everywhere else, and also what the scaling
	   functions in the Java AWT do to preserve the aspect ratio */
	scaled_image_height= 
	    scaled_image_width*original_image_height/original_image_width;
		    
	/* NB: this is not exactly SF*delta*mult, because a 
	   truncation was done when calculating scaled_image_width ... */
	scale_factor= 
	    ((double)scaled_image_width) / ((double)original_image_width);
	// for clarity, make an alias...
	final double new_scale_factor= scale_factor;
		    
	/* compute the new image_origin s.th. center_old_fov 
	   (in image space) will go (after image->vport coord change)
	   to the same place where it was before... */
	image_origin.x= 
	    Math.round( (float)( center_old_fov_x - center_fov_x * new_scale_factor));
	image_origin.y= 
	    Math.round( (float)( center_old_fov_y - center_fov_y * new_scale_factor));

	// changed image_origin & scale_factor, so need to call this:
	_updateVportCursorPosition();
	// done with scaled_image_width; since we changed it, need to write it back!
	this.scaled_image_width= scaled_image_width;

	if( DEBUG)
	    System.out.println( "image_origin:" + image_origin +
				" scaled_image_width:"+scaled_image_width +
				" scaled_image_height:"+scaled_image_height
				);
	repaint();
    }

    /** for the private internal use of _newCursor( int, int) */
    /*private*/ Point3Dfloat __newCursor_new_world_cursor= new Point3Dfloat();

    /**
     * Gets called in response to this class' own mouse events (so it
     * <i>does</i> perform a range check on the new position); if the
     * new cursor position is within volume's boundaries, it updates
     * the current cursor position and notifies other modules of the
     * change.
     *
     * NOTE: Currently, the cursor is in "grid mode": it automatically
     * "snaps" to voxel centers (i.e. it cannot have arbitrary
     * positions).  This also means that, with the current
     * implementation (19/3/2000) the world position coordinates
     * generated by this class will always be integral values...
     *
     * @param vport_point_x The new viewport X coordinate of the cursor.
     * @param vport_point_y The new viewport Y coordinate of the cursor.
     *
     * @see #_newCursor( float, float, boolean) 
     */
    final synchronized /*private*/ void _newCursor( final int vport_point_x,
						    final int vport_point_y  ) {

	/* TODO (optional): allow arbitrary cursor positions; for
	   this, make the two variables below floats instead of ints,
	   etc (but make sure you change the range check too --
	   world2voxel uses round not trunc, hence a world_x of 90.51
	   will become a voxel_x of 181, which is out of range!)  
	*/
	// NB: in java, the int->float conversion is a 'trunc'
	final int new_cursor_vox_x= (int)
	    ((vport_point_x - image_origin.x)/scale_factor);
	final int new_cursor_vox_y= (int)
	    (original_image_height - (vport_point_y - image_origin.y)/scale_factor);
	if( DEBUG) {
	    System.out.println( "new_cursor_vox_x:" + new_cursor_vox_x + ", " +
				"new_cursor_vox_y:" + new_cursor_vox_y);
	}
	if( !( new_cursor_vox_x >= 0 && new_cursor_vox_x < original_image_width &&
	       new_cursor_vox_y >= 0 && new_cursor_vox_y < original_image_height  )
	    ) 
	    // cursor outside volume range... ignore the request.
	    return;

	_voxel2world( __newCursor_new_world_cursor, 
		      new_cursor_vox_x, new_cursor_vox_y, 0f);
	_newCursor(  __newCursor_new_world_cursor.x,  __newCursor_new_world_cursor.y,
		     true);
    }
    
    /** for the private internal use of _newCursor( float, float, bool) */
    /*private*/ Point __newCursor_old_vport_cursor= new Point();
    /** for the private internal use of _newCursor( float, float, bool) */
    /*private*/ Point __newCursor_new_vport_cursor= new Point();
    /** for the private internal use of _newCursor( float, float, bool) */
    /*private*/ Rectangle2 __newCursor_bounds1= new Rectangle2();
    /** for the private internal use of _newCursor( float, float, bool) */
    /*private*/ Rectangle2 __newCursor_bounds2= new Rectangle2();

    /**
     * Gets called by <code>_newCursor( int, int)</code> or by
     * <code>positionChanged</code>. Updates the graphical cursor
     * representation and the distance measurement graphics (if
     * distance mode is active). Optionally, notifies other
     * modules (which registered their interest by means of
     * <code>addPositionListener</code>) of the cursor position
     * change.
     *
     * @param new_world_x The new world "X" coordinate of the
     * cursor. Not checked if it's withins image volume's boundaries!
     * @param new_world_x The new world "Y" coordinate of the
     * cursor. Not checked if it's withins image volume's boundaries!
     * @param notify_others Indicates if it should notify other modules.
     */
    final synchronized protected void _newCursor( final float new_world_x,
						  final float new_world_y,
						  final boolean notify_others) {

	Rectangle2 old_bounds; 
	vport_cursor.getPosition( __newCursor_old_vport_cursor);
	vport_cursor.getBounds( old_bounds= __newCursor_bounds1);
	cursor.x= new_world_x;
	cursor.y= new_world_y;
	if( null != distance_origin) {
	    Rectangle  distance_bounds= __newCursor_bounds2;
	    distance_display.getBounds( distance_bounds);
	    old_bounds.expandToInclude( distance_bounds);
	    distance_display.setLabel( _distanceInSlice( distance_origin, cursor));
	}
	/* this updates 'vport_cursor' (and possibly 'distance_display') 
	   using the new 'cursor' */
	_updateVportCursorPosition(); 	
	vport_cursor.getPosition( __newCursor_new_vport_cursor);

	/* if we are currently displaying the interactive distance
	   measurement, then we need to repaint even if the cursor
	   didn't visibly move in the viewport (because the displayed
	   distance might have changed)! */
	if( null != distance_origin ||
	    !__newCursor_old_vport_cursor.equals( __newCursor_new_vport_cursor) 
	    ) {
	    Rectangle2 new_bounds;
	    vport_cursor.getBounds( new_bounds= __newCursor_bounds2);
	    // specify a clip rectangle that just covers the old and new cursors!
	    new_bounds.expandToInclude( old_bounds);

	    if( null != distance_origin) {
		Rectangle distance_bounds= __newCursor_bounds1;
		distance_display.getBounds( distance_bounds);
		new_bounds.expandToInclude( distance_bounds);
	    }
	    repaint( new_bounds.x, new_bounds.y, new_bounds.width, new_bounds.height);
	}
	if( notify_others) 
	    _firePositionEvent( PositionEvent.X | PositionEvent.Y);
    }

    /** for the private internal use of _newSlice() */
    /*private*/ Point3Dint __newSlice_new_voxel= new Point3Dint();

    /**
     * Changes the "Z" cursor position, provided the new position is
     * not outside volume's boundaries.  This is done by sending a new
     * position event to the other modules (which registered their
     * interest by means of <code>addPositionListener</code>); one of
     * these PositionListener-s is the slice image producer for this
     * viewport, hence the displayed image gets changed if we cross
     * into a different slice.
     *
     * NOTE: Currently, this method _does_not_ operates in
     * "grid-mode", unlike <code>_newCursor()</code> !  This is
     * required in order for sub-unit multiplication factors to work
     * -- otherwise, we'll be at the same voxel even after 100 "steps"
     * (mouse events) of 0.5 each ...
     *
     * @param increment Change in the "Z" world coordinate (cursor
     * movement in a direction orthogonal to this viewport). Can be
     * positive or negative.  
     */
    final synchronized /*private*/ void _newSlice( final float increment) {

	final float new_cursor_z= cursor.z + increment;
	// NB: this uses 'round' !
	_world2voxel( __newSlice_new_voxel, 0, 0, new_cursor_z);
	if( __newSlice_new_voxel.z >= 0 && __newSlice_new_voxel.z <= max_slice_number
	    ) {
	    cursor.z= new_cursor_z;
	    _firePositionEvent( PositionEvent.Z);
	}
    }

    /** for the private internal use of _{startNew,clear}DistanceMeasurement() */
    /*private*/ Rectangle __DistanceMeasurement_old_bounds= new Rectangle();
    /** for the private internal use of _{startNew,clear}DistanceMeasurement() */
    /*private*/ Rectangle2 __DistanceMeasurement_new_bounds= new Rectangle2();

    /**
     * Marks the current cursor position as the origin (first point)
     * for the in-slice distance measurement. Also, it enables the
     * distance measurement mode, if not already on. 
     */
    final synchronized /*private*/ void _startNewDistanceMeasurement() {

	Rectangle old_bounds= null;

	if( null == distance_display)
	    // NB: this assumes that the default drawing font won't ever change.
	    distance_display= new ViewportDistanceDisplay( getFontMetrics( getFont()) );

	else if( distance_origin != null)
	    // distance measurement mode is already on
	    distance_display.getBounds( old_bounds= __DistanceMeasurement_old_bounds);

	distance_origin= new Point3Dfloat( cursor); 
	distance_display.setLabel( 0f);
	// this will update the start&end in distance_display
	_updateVportCursorPosition();	
	
	Rectangle2 new_bounds; 
	distance_display.getBounds( new_bounds= __DistanceMeasurement_new_bounds);
	if( old_bounds != null)
	    new_bounds.expandToInclude( old_bounds);

	repaint( new_bounds.x, new_bounds.y, new_bounds.width, new_bounds.height);
    }

    /**
     * Disables the distance measurement mode.
     */
    final synchronized /*private*/ void _clearDistanceMeasurement() {

	if( distance_origin == null ) 
	    // the distance measurement mode is already off
	    return;

	Rectangle bounds; 
	distance_display.getBounds( bounds= __DistanceMeasurement_old_bounds);
	distance_origin= null; 

	repaint( bounds.x, bounds.y, bounds.width, bounds.height);
    }

    /** for the private internal use of _updateVportCursorPosition() */
    /*private*/ Point __updateVportCursorPosition_vport_cursor= new Point( -1, -1);

    /* currently, this is thread unsafe */
    /**
     * Updates the viewport positions of <code>vport_cursor</code> and
     * <code>distance_display</code>. They are a function of the
     * following instance fields: 'image_origin', 'scale_factor', and
     * 'cursor' hence this method should be called <i>everytime</i> any
     * of them changes!!
     *
     * @see #_world2viewport 
     */
    final /*private*/ void _updateVportCursorPosition() { 

	_world2viewport( __updateVportCursorPosition_vport_cursor, 
			 cursor.x, cursor.y);
	vport_cursor.setPosition( __updateVportCursorPosition_vport_cursor);

	if( null != distance_origin) {
	    distance_display.setEndPosition( __updateVportCursorPosition_vport_cursor);
	    _world2viewport( __updateVportCursorPosition_vport_cursor, 
			     distance_origin.x, distance_origin.y);
	    distance_display.setStartPosition( __updateVportCursorPosition_vport_cursor);
	}
    }

    /** for the private internal use of _world2viewport() */
    /*private*/ Point3Dint __world2viewport_voxel= new Point3Dint();

    /** 
     * Converts a world (X,Y) in-slice position to viewport (X,Y)
     * coordinates, rounded to the nearest voxel center. The world
     * coordinates are assumed to be within volume's boundaries!
     *
     * @param vport_point Output: reference to Point object where to
     * store the result.
     * @param world_x Input: world "X" coordinate.
     * @param world_y Input: world "Y" coordinate.  
     */
    final /*private*/ void _world2viewport( Point vport_point,
					    final float world_x, final float world_y) {

	// NB: this uses 'round' !
	_world2voxel( __world2viewport_voxel, world_x, world_y, 0);

	int offset= (scaled_image_width / original_image_width) >> 1;
	if( DEBUG)
	    System.out.println( "offset: " + offset);
	/* Note: in java, the float->int conversion is a 'trunc' (i.e. rounding
	   towards zero) */
	vport_point.x= offset + image_origin.x +
	    (int)( __world2viewport_voxel.x * scale_factor);
	vport_point.y= -offset + image_origin.y +
	    (int)( (original_image_height - __world2viewport_voxel.y) * scale_factor);
    }
					    
    /** for the private internal use of paint() */
    /*private*/ int __paint_old_scaled_image_width= -1;

    /**
     * Called by AWT when screen (re)drawing is required; it should be
     * able to redraw everything (e.g. for situations when the window
     * got partially covered or corrupted).
     *
     * @param gr The graphics context to draw on.  
     */
    final public void paint( Graphics gr) { 

	if( DEBUG) { 
	    System.out.println( "*** paint( " + gr + " )");
	    // this is to convince yourself that always there's a clip
	    // window defined, and it's never larger than the real
	    // drawing area...
	    System.out.println( "    getClipBounds: " + gr.getClipBounds());
	    System.out.println( "    image_origin: " + image_origin +
				", scaled_image_width: " + scaled_image_width);	
	}
	// for speed, use stack variables instead of the instance fields:
	final Point image_origin= this.image_origin;
	final int scaled_image_width= this.scaled_image_width;

	if( USE_SEPARATE_SCALE_FILTER) {

	    /* optimization: don't build a new scale filter (that's what
	       getScaledInstance does...) unless necessary */
	    if( null == scaled_image || 
		scaled_image_width != __paint_old_scaled_image_width
		) {
		scaled_image= original_image.getScaledInstance( scaled_image_width, -1, 
								Image.SCALE_REPLICATE);
		__paint_old_scaled_image_width= scaled_image_width;
	    }
	}
	// Note: no need to clip explicitly, as AWT will do it for us anyway 
	// (i.e. won't allow us to draw outside our area, and in this case 
	// the clip window is our entire drawing area anyway...)

	if( USE_SEPARATE_SCALE_FILTER) {

	    if( USE_NEW_DRAW_IMAGE) {
		final int scaled_image_height= this.scaled_image_height;
		gr.drawImage( scaled_image, 
			      image_origin.x, image_origin.y,
			      image_origin.x+scaled_image_width, 
			      image_origin.y+scaled_image_height,
			      0, 0, scaled_image_width, scaled_image_height, null);
	    }
	    else {
		gr.drawImage( scaled_image, image_origin.x, image_origin.y, null);
	    }
	}
	else {
	    if( USE_NEW_DRAW_IMAGE)
		gr.drawImage( original_image, 
			      image_origin.x, image_origin.y,
			      image_origin.x+scaled_image_width, 
			      image_origin.y+scaled_image_height,
			      0, 0, original_image_width, original_image_height, null);
	    else
		gr.drawImage( original_image, image_origin.x, image_origin.y,
			      scaled_image_width, scaled_image_height, null);
	}

	vport_cursor.draw( gr);
	if( null != distance_origin)
	    distance_display.draw( gr);

	// TODO: enable this if you add any lightweight components to this Container!
	if( false) super.paint( gr);
    }

    /**
     * Wrapper around <code>paint(Graphics)</code>; implements the
     * double-buffering. Assumes that 'offscreen_buffer' and
     * 'offscreen_gc' are properly initialized (right size, etc).
     *
     * @param gr The screen graphics context to draw on.  
     */
    /*private*/ final void _doubleBufferedPaint( Graphics gr) {  

	paint( offscreen_gc);

	if( USE_NEW_DRAW_IMAGE) 
	    gr.drawImage( offscreen_buffer, 
			  0, 0, vport_dims.width, vport_dims.height,
			  0, 0, vport_dims.width, vport_dims.height, null);
	else
	    gr.drawImage( offscreen_buffer, 0, 0, null);
    }

    /**
     * Called by AWT when screen redrawing/updating is required (and
     * in response to <code>repaint()</code> requests by the
     * application). It can safely assume that whatever it draw before
     * it's still there (i.e. didn't somehow get erased).
     *
     * @param gr The screen graphics context to draw on.  
     */
    final public void update( Graphics gr) { 

	// for speed, use stack variables instead of the instance fields:
	final Dimension vport_dims= this.vport_dims; 
	final Point image_origin= this.image_origin;
	// for unclear reasons, sometimes this call returns 'null' 
	// the first time when update() is invoked...
	final Rectangle clip= gr.getClipBounds();
	if( DEBUG) { 
	    System.out.println( this+"*** update( " + gr + " )");
	    System.out.println( "    getClipBounds: " + clip);
	    System.out.println( "    vport_dims: " + vport_dims);
	}
	boolean offscreen_buffer_cleared= false;
	if( null == offscreen_buffer) {
	    offscreen_buffer= createImage( vport_dims.width, vport_dims.height);
	    offscreen_gc= offscreen_buffer.getGraphics();
	    offscreen_buffer_cleared= true;
	}
	/* the logic here may be a bit obscure: this test figures out if
	   we actually need to clear some viewport area or not. */
	if( clip == null || 
	    ( clip.x == 0 && clip.y == 0 && 
	      clip.width == vport_dims.width && clip.height == vport_dims.height ) ||
	    /* optimization: see if the clip rectangle extends outside the 
	       scaled image (otherwise no need to clear first, nor to dbl-buffer) */
	    clip.x < image_origin.x || 
	    (clip.x+clip.width) > (image_origin.x+scaled_image_width) ||
	    clip.y < image_origin.y || 
	    (clip.y+clip.height) > (image_origin.y+scaled_image_height)
	    ) {
	    
	    if( clip != null) 
		offscreen_gc.setClip( clip);
	    else 
		offscreen_gc.setClip( 0, 0, vport_dims.width, vport_dims.height);

	    if( !offscreen_buffer_cleared) {
		// clear the viewport
		if( true) {
		    /* MUSING: the java API docs on Graphics#clearRect say that:
		       "the background color of offscreen images may be system dependent"
		       can this be a problem?? (TODO: test it on different platforms)
		    */
		    // this should be faster than the other alternative ...
		    offscreen_gc.clearRect( 0, 0, vport_dims.width, vport_dims.height);
		}
		else {
		    offscreen_gc.setColor( getBackground());
		    offscreen_gc.fillRect( 0, 0, vport_dims.width, vport_dims.height); 
		}
	    }
	    _doubleBufferedPaint( gr);
	    return;
	}
	if( ALWAYS_DOUBLE_BUFFER) {
	    if( clip != null) 
		offscreen_gc.setClip( clip);
	    else 
		offscreen_gc.setClip( 0, 0, vport_dims.width, vport_dims.height);
	    _doubleBufferedPaint( gr);
	}
	else {
	    paint( gr);
	}
    }

    /**
     * Called by AWT (e.g. when the viewport/window size changed).
     * Recomputes the image position and dimension (scale factor) in
     * the viewport such that the old field of view (FOV), in the
     * original image space, is preserved and centered.  
     */
    final synchronized public void doLayout() {
	
	// if we get called, then most probably the viewport size changed ...
	final Dimension new_vport_dims= getSize();
	if( new_vport_dims.height <= 0 || new_vport_dims.width <= 0)
	    // silly values! just ignore them
	    return;

	if( DEBUG) {
	    System.out.println( this + " vport_dims: " + vport_dims);
	    System.out.println( "\timage_origin: " + image_origin);
	    System.out.println( "\tscaled_image_width: " + scaled_image_width);
	}
	// TODO: optimization: maybe do nothing if vport_dims didn't change?
	// (but careful to make sure you _do_ the computations the first time!)

	if( offscreen_buffer != null) {
	    // need to resize our offscreen buffer (used for double-buffered drawing)
	    offscreen_gc.dispose();	// good practice...
	    offscreen_gc= null;
	    offscreen_buffer= null;
	    // a new buffer will be allocated in update() ...
	}

	final double old_scale_factor= scale_factor;
	
	// all these are in viewport (display) coord space
	final int old_fov_left= Math.max( 0, image_origin.x);
	final int old_fov_right= Math.min( vport_dims.width, 
					   image_origin.x+scaled_image_width);
	final int old_fov_top= Math.max( 0, image_origin.y);
	final int old_fov_bottom= Math.min( vport_dims.height, 
					    image_origin.y+scaled_image_height);
	final int vport_center_old_fov_x= 
	    old_fov_left + (old_fov_right - old_fov_left)/2;
	final int vport_center_old_fov_y= 
	    old_fov_top + (old_fov_bottom - old_fov_top)/2;
	// will divide by this, so be extra careful
	final int old_fov_width= Math.max( 1, old_fov_right - old_fov_left);
	// will divide by this, so be extra careful
	final int old_fov_height= Math.max( 1, old_fov_bottom - old_fov_top);
	final float old_fov_aspect= ((float)old_fov_width) / old_fov_height;
	final float new_vport_aspect= 
	    ((float)new_vport_dims.width) / new_vport_dims.height;
	final double new_scale_factor=  old_scale_factor *
	    ( ( new_vport_aspect > old_fov_aspect) ? 
	      ( ((float)new_vport_dims.height) / old_fov_height) :
	      ( ((float)new_vport_dims.width) / old_fov_width)    );
	// for clarity, make an alias (copy the ref) for image_origin 
	final Point old_image_origin= image_origin;

	vport_dims= new_vport_dims;
	/* Note: in java, the float->int conversion is a 'trunc' (i.e. rounding
	   towards zero), which is what we want here (we want the image to fit
	   for sure in the viewport) */
	scaled_image_width= 
	    _cappedScaledImageWidth( (int)( new_scale_factor * original_image_width));
	/* Note: decided not to use Math.round here; a cast, or trunc,
	   is what we do everywhere else, and also what the scaling
	   functions in the Java AWT do to preserve the aspect ratio */
	scaled_image_height= 
	    scaled_image_width*original_image_height/original_image_width;
	// NB: don't just store new_scale_factor, because a truncation was done
	// when calculating scaled_image_width ...
	scale_factor= ((double)scaled_image_width) / ((double)original_image_width);
	
	// convert from vport to image coord space
	//   (use double instead of int in order to keep truncation errors 
	//   under control...)
	final double center_old_fov_x= 
	    (vport_center_old_fov_x - old_image_origin.x) / old_scale_factor;
	final double center_old_fov_y=
	    (vport_center_old_fov_y - old_image_origin.y) / old_scale_factor;

	// compute the new image_origin s.th. center_old_fov (in image space)
	// will go (after image->vport coord change) in the center of the new
	// viewport:
	image_origin.x= Math.round(
	    (float)( vport_dims.width / 2.0 - center_old_fov_x * scale_factor));
	image_origin.y= Math.round(
	    (float)( vport_dims.height / 2.0 - center_old_fov_y * scale_factor));

	// changed image_origin & scale_factor, so need to call this:
	_updateVportCursorPosition();

	if( DEBUG) {
	    System.out.println( "New values: " + " vport_dims: " + vport_dims);
	    System.out.println( "\timage_origin: " + image_origin);
	    System.out.println( "\tscaled_image_width: " + scaled_image_width);
	    System.out.println( "\tscaled_image_height: " + scaled_image_height);
	}

	repaint();
    }

    /**
     * "Callback" used, by an outside event source, to deliver a
     * <code>PositionEvent</code>. Required by the PositionListener
     * interface.
     *
     * @param e The new (imposed from the outside) world cursor position.
     *
     * @see PositionListener 
     */
    abstract public void positionChanged( PositionEvent e );

    /**
     * Required by the PositionListener interface.
     *
     * @return -1 (that is, a clearly invalid value) to indicate that
     * this class is not an <code>ImageProducer</code>.
     * 
     * @see PositionListener 
     */
    final public int getMaxSliceNumber() { return -1; }

    /**
     * Required by the PositionListener interface.
     *
     * @return Float.NaN (that is, a clearly invalid value) since
     * this class is not an <code>ImageProducer</code>.
     * 
     * @see PositionListener 
     */
    final public float getOrthoStep() { return Float.NaN; }

    /**
     * Registers another module who is interested in being notified of
     * cursor position changes originating from this viewport.
     *
     * @param pl Module interested in receiving position events. If
     * the argument is 'null', or if it is already present in the list
     * of event listeners, this method does nothing.  
     */
    final synchronized public void addPositionListener( PositionListener pl) {
	
	if( null == event_listeners) 
	    event_listeners= new Vector();
	if( null == pl || event_listeners.contains( pl))
	    return;
	event_listeners.addElement( pl);
    }

    /**
     * Undoes what <code>addPositionListener</code> did.
     *
     * @param pl Module (position event listener) to remove from the
     * list of modules to notify.
     * 
     * @see #addPositionListener 
     */
    final synchronized public void removePositionListener( PositionListener pl) {

	if( null != event_listeners && null != pl) 
	    event_listeners.removeElement( pl);
    }

    /**
     * Notifies the other (registered) modules about a cursor position
     * change.
     *
     * @param changed_coords_mask Indicates which of (x,y,z) changed
     * (see <code>PositionEvent</code> for legal values).  
     * 
     * @see #__aid_to_firePositionEvent 
     */
    abstract protected void _firePositionEvent( int changed_coords_mask);

    /**
     * Helper method: The orientation-independent functionality of
     * <code>_firePositionEvent</code> (who calls this).
     *
     * @param e The <code>PositionEvent</code> to send out.  
     *
     * @see #_firePositionEvent 
     */
    final protected void __aid_to_firePositionEvent( final PositionEvent e) {

	if( DEBUG) System.out.println( e);

	// deliver the event to each of the listeners
	if( null == event_listeners) 
	    // nobody listening...
	    return;
	for( int i= 0; i < event_listeners.size(); ++i)
	    ((PositionListener)event_listeners.elementAt( i)).positionChanged( e);
    }

    /** 
     * @return world coordinate-space distance, in this slice's plane,
     * between the points specified by <code>world_a</code> and
     * <code>world_b</code>; the Z coordinates are ignored (ie the two
     * points are assumed to be in this slice's plane); also, the
     * world coordinate axes are assumed orthogonal.  
     */
    /*private*/ final float _distanceInSlice( final Point3Dfloat world_a, 
					      final Point3Dfloat world_b) {
	
	/* Note: this assumes that the world coordinate axes are
	   orthogonal (a reasonable assumption?!) FIXME? */
	final float delta_x= world_a.x - world_b.x;
	final float delta_y= world_a.y - world_b.y;
	return (float)Math.sqrt( delta_x * delta_x + delta_y * delta_y);
    }

    /**
     * Converts from voxel to world coordinates, transparently
     * handling the reordering/reshuffling of the "virtual" (versus
     * "real") coordinates.
     *
     * Note: the original reason for using the voxel2world version
     * that reads voxel coords as floats was to be able to handle
     * arbitrary positions in voxel space. However, currently only the
     * "grid-mode" is supported, so using this voxel2world is kind of
     * a waste...
     *
     * @param world Output: reference to Point3Dfloat object where to
     * store the result ("virtual" world coordinates).
     * @param vx Input: voxel "virtual X" coordinate.
     * @param vy Input: voxel "virtual Y" coordinate.  
     * @param vz Input: voxel "virtual Z" coordinate.  
     */
    abstract protected void _voxel2world( Point3Dfloat world, 
					  float vx, float vy, float vz);

    /**
     * Converts from world to voxel coordinates, transparently
     * handling the reordering/reshuffling of the "virtual" (versus
     * "real") coordinates.
     *
     * Note: for supporting arbitrary cursor positions, you'll need an
     * overridden version of this method that returns (outputs) a
     * Point3Dfloat!
     * 
     * @param voxel Output: reference to Point3Dint object where to
     * store the result ("virtual" voxel coordinates).
     * @param wx Input: world "virtual X" coordinate.
     * @param wy Input: world "virtual Y" coordinate.  
     * @param wz Input: world "virtual Z" coordinate.  
     */
    abstract protected void _world2voxel( Point3Dint voxel,
					  float wx, float wy, float wz);

} // end of class Slice2DViewport



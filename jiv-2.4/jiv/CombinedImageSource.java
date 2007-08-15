
// $Id: CombinedImageSource.java,v 1.2 2002/04/24 14:31:56 cc Exp $
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

import java.awt.image.*;
import java.awt.*;
import java.util.*;

/**
 * Template for a class that combines two source images into one
 * output image using the
 * <code>ImageProducer</code>/<code>ImageConsumer</code>
 * interfaces. The source images need to be of the same size and have
 * an <code>IndexColorModel</code>; the output image will always have
 * a <code>DirectColorModel</code>.
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: CombinedImageSource.java,v 1.2 2002/04/24 14:31:56 cc Exp $ 
 */
abstract public class CombinedImageSource implements ImageProducer, PositionListener {

    protected static final boolean DEBUG= false;

    /*private*/ ImageProducer[] 	src_ip= new ImageProducer[ 2]; 
    /*private*/ PositionListener[] 	src_pl= new PositionListener[ 2]; 
    /*private*/ InputReader[]		input_reader= new InputReader[ 2];
    /** needs to be initialized in the constructor! */
    /*private*/ MemoryImageSource	mis; 
    /*private*/ boolean			suspend_combine= true;
    protected byte[][]			src_data= new byte[ 2][];
    protected int			img_width= -1;
    protected int			img_height= -1;
    protected IndexColorModel[]		color_model= new IndexColorModel[ 2];
    protected int[]			outgoing_buffer;

    protected CombinedImageSource( ImageProducer source_1, 
				   PositionListener listener_for_source_1, 
				   ImageProducer source_2,
				   PositionListener listener_for_source_2  ) {

	this.src_ip[ 0]= source_1;
	this.src_pl[ 0]= listener_for_source_1;
	this.src_ip[ 1]= source_2;
	this.src_pl[ 1]= listener_for_source_2;
	
	input_reader[ 0]= new InputReader( 0);
	input_reader[ 1]= new InputReader( 1);

	// NB: the InputReader constructor calls ImageProducer::startProduction, 
	// so at this point the respective ImageProducer-s might have already
	// started calling setDimensions(), setPixels(), etc

	synchronized( this) {
	    if( -1 == img_width || -1 == img_height) {
		// print this even if debug mode is not on...
		System.err.println( this + ": hmmm, none of the producers " 
				    + "notified me yet about the image dimensions...");
				
		// calling createImage is a bit tricky: usually one calls it
		// from a Component (or subclass of) ...
		Image tmp_image= Toolkit.getDefaultToolkit().createImage( source_1);
		img_width= tmp_image.getWidth( null);
		img_height= tmp_image.getHeight( null);
		tmp_image= null; // explicitly discard it (although it will be anyway...)

		if( -1 == img_width || -1 == img_height) {
		    String msg= 
			": couldn't determine source image dimensions fast enough...";
		    throw new IllegalArgumentException( this + msg);
		}
	    }

	    // NB: 'mis' needs to be initialized here in the constructor 
	    // because as soon as we're done with constructing the object
	    // external ImageConsumer-s might start calling addConsumer(), etc
	    
	    // no need to store the colormodel because we won't change it later
	    mis= new 
		MemoryImageSource( img_width, img_height, 
				   // using this constructor, the colors won't
				   // have any transparency (will be opaque)
				   new DirectColorModel( 24, 0x00ff0000, 
							 0x0000ff00, 0x000000ff),
				   outgoing_buffer, 0, img_width);
	    mis.setAnimated(true); 
	    /* after browsing through the AWT public sources, it become obvious
	       that enabling full buff updates will speed thing up in 
	       MemoryImageSource, and possibly down the pipeline too (because
	       the hint TOPDOWNLEFTRIGHT|COMPLETESCANLINES will be sent to 
	       the consumers) ... */
	    mis.setFullBufferUpdates( true); 
	    
	    /* signal that our initialization is done (however, the
               subclass constructor is not yet done at this point in
               time...) */
	    suspend_combine= false;
	}
    }

    /*private*/ synchronized final void _setDimensions( int width, int height) {

	if( null == src_data[ 0] || null == src_data[ 1] ) {
	    // first time we find out about image's dimensions
	    this.img_width= width;
	    this.img_height= height;
	    final int required_size= width * height;
	    src_data[ 0]= new byte[ required_size];
	    src_data[ 1]= new byte[ required_size];
	    outgoing_buffer= new int[ required_size];
	    return;
	}
	// not the first time ...
	if( this.img_width != width || this.img_height != height  ) {
	    String msg= " source images don't have the same dimensions!";
	    throw new IllegalArgumentException( this + msg);
	}		      
    }

    /*private*/ final void _errorWhileReading( int source_reader) {
	// TODO: how do we handle this properly?
    }

    /** used to request an update of the output image.
	If (argument >= 0) : it means that only the data produced by
	src_ip[ arg] has changed. If (argument == -1), then it means
	that both input images have changed (or simply that everything
	has to be recomputed) */
    public synchronized final void newFrame( int source_reader) {

	/* This test here is critical!  This method will get called from 
	   InputReader::imageComplete, which in turn is (practically) called from
	   InputReader.<init> (ie its constructor).  At that time (ie on the first
	   two calls), the subclass that defines combineInputImages() is not yet
	   initialized (remember, we're still running its superclass constructor),
	   hence big headaches...
	   _However_, even this precaution is not necessarily enough: if this
	   method gets called after our constructor is done, but before the
	   subclass construction is complete, combineInputImages() might have
	   to work with uninitialized fields, so headaches again... (FIXME?)
	*/
	if( suspend_combine)
	    return;
	combineInputImages();
	mis.newPixels();
    }

    /** This method does the actual computations for "combining" the two
	input images. It should be implemented by any non-abstract subclass.  
    */
    abstract protected void combineInputImages();


    // ImageProducer interface methods: simply forward the requests to 'mis'

    public final void addConsumer( ImageConsumer ic) { mis.addConsumer( ic); }

    public final boolean isConsumer( ImageConsumer ic) { return mis.isConsumer( ic); }

    public final void removeConsumer( ImageConsumer ic) { mis.removeConsumer( ic); }

    public final void requestTopDownLeftRightResend( ImageConsumer ic) { 

	// MemoryImageSource's implementation does nothing ...
	System.err.println( this + 
			    ": hmmm, requestTopDownLeftRightResend called by " + ic);
	// a call to newPixels() would be the obvious implementation, but
	// that's not quite right if we have more consumers!
	mis.removeConsumer( ic);
	mis.addConsumer( ic);
    }

    public final void startProduction( ImageConsumer ic) { mis.startProduction( ic); }


    // PositionListener interface methods: 
    
    // simply forward the event
    public final void positionChanged( PositionEvent e ) {

	/* Note: no real need to 'synchronize' this method, because
           boolean assignments are atomic in Java... */

	/* Optimization: avoid calling combineInputImages() twice (via
	   the callbacks from the src_ip-s), when we could just call
	   it once after both src_data-s are updated! */
	suspend_combine= true;
	src_pl[ 0].positionChanged( e);
	suspend_combine= false;
	src_pl[ 1].positionChanged( e);
    }

    // NB: this _assumes_ that the two producers 
    //     will give the same answer to this query!
    public final int getMaxSliceNumber() { 
	return src_pl[ 0].getMaxSliceNumber();
    }

    public final float getOrthoStep() { 
	return Math.min( src_pl[ 0].getOrthoStep(), src_pl[ 1].getOrthoStep());
    }


    /** 
     * Inner (member) class: the interface to the source
     * <code>ImageProducer</code>-s.
     *
     * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
     * @version $Id: CombinedImageSource.java,v 1.2 2002/04/24 14:31:56 cc Exp $ 
     */
    /*private*/ final class InputReader implements ImageConsumer {

	/*private*/ final int which_one;

	protected InputReader( int which_one) { 
	    // Note: most natural would be to store a ref to the buffer
	    // to use but when this constructor is called the buffers in  
	    // the enclosing class are not yet allocated (because it waits 
	    // for the dimensions to be specified by a call to setDimensions)
	    this.which_one= which_one;
	    src_ip[ which_one].startProduction( this);
	}

	// the rest are ImageConsumer's methods:

	public final void setProperties( Hashtable props) { /* do nothing */ }	

	public final void setDimensions( int width, int height) { 
	    CombinedImageSource.this._setDimensions( width, height);
	}

	public final void setColorModel( ColorModel model) { 

	    if( !( model instanceof IndexColorModel)) {
		String msg= 
		    " CombinedImageSource can only read data using an IndexColorModel!";
		throw new IllegalArgumentException( this + msg);
	    }		      
	    color_model[ which_one]= (IndexColorModel)model;
	}

	public final void setHints( int hintflags) { /* do nothing */ }	

	public final void setPixels( int x, int y, int w, int h, ColorModel model, 
				     int[] pixels, int off, int scansize) { 
	    
	    System.err.println( this + 
				": hmmm, didn't expect to receive data as int[]");
	    byte[] translated_pixels= new byte[ pixels.length];
	    for( int i= 0; i < pixels.length; ++i)
		translated_pixels[ i]= (byte)( 0xFF & pixels[ i]);
	    // forward this call to the other setPixels() 
	    setPixels( x, y, w, h, model, translated_pixels, off, scansize);
	}

	public final void setPixels( int dest_x, int dest_y, int w, int h, 
				     ColorModel model, 
				     byte[] pixels, int off, int scansize) { 

	    setColorModel( model);	// this checks it for validity...
	    final byte[] read_buffer= src_data[ which_one];
	    // for speed, use a stack variable instead of the instance field:
	    final int img_width= CombinedImageSource.this.img_width;

	    /* TODO: speed optimization: we could only accept data that comes
	       with the hints TOPDOWNLEFTRIGHT|COMPLETESCANLINES (to be tested
	       by setHints above); then, we could simplify the computatations
	       in the loop below:
	    */
	    for( int y= 0; y < h; ++y) 
		System.arraycopy( pixels, off + y * scansize, 
				  read_buffer, dest_x + (dest_y + y) * img_width,
				  w );
	}

	public final void imageComplete( int status) { 
	    
	    switch( status) {
	    case ImageConsumer.IMAGEABORTED:
		System.err.println( this + 
				    "::imageComplete received error status (ABORT):" 
				    + status);
		CombinedImageSource.this._errorWhileReading( which_one);
		break;
	    case ImageConsumer.IMAGEERROR:
		System.err.println( this + 
				    "::imageComplete received error status (ERROR):"
				    + status);
		CombinedImageSource.this._errorWhileReading( which_one);
		break;
	    case ImageConsumer.STATICIMAGEDONE:
		// good practice: unregister ourselves...
		src_ip[ which_one].removeConsumer( this);
		System.err.println( this + 
				    "::imageComplete received status (STATICIMAGEDONE):"
				    + status);
	    case ImageConsumer.SINGLEFRAMEDONE:
		if( DEBUG)
		    System.err.println( this + 
				    "::imageComplete received status (SINGLEFRAMEDONE):"
				    + status);
		CombinedImageSource.this.newFrame( which_one);
		break;
	    default:
		System.err.println( this + 
				    "::imageComplete received unknown error status: " 
				    + status);
	    }
	}
    }
    // end of class InputReader
}

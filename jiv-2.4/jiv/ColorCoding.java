
// $Id: ColorCoding.java,v 1.2 2001/11/28 10:55:16 cc Exp $
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

/** 
 * Produces 8bit colormaps (palettes), according to given
 * specifications. Several common medical imaging color codings are
 * supported. 
 *
 * @author the specifications for the "hotmetal" and "spectral" color
 *       codings are by Peter Neelin, Montreal Neurological Institute
 *       (neelin@bic.mni.mcgill.ca).
 * @author _compute_labels_lookup() is adapted from the "Display" software, 
 *       copyright David MacDonald, Montreal Neurological Institute
 *       (david@bic.mni.mcgill.ca).
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: ColorCoding.java,v 1.2 2001/11/28 10:55:16 cc Exp $ 
 */
public final class ColorCoding {

    /*private*/ static final boolean DEBUG= false;

    public static final int GRAY= 	1;
    public static final int GREY= 	1;
    public static final int HOTMETAL= 	2; 
    public static final int SPECTRAL= 	3; 
    public static final int RED= 	4;
    public static final int GREEN= 	5;
    public static final int BLUE= 	6;
    public static final int MNI_LABELS=	7;
    
    // it's assumed that the given points are equidistant
    /*private*/ static final double[][] spectral_specification = {
	{ 0.00, 0.0000,0.0000,0.0000 },
	{ 0.05, 0.4667,0.0000,0.5333 },
	{ 0.10, 0.5333,0.0000,0.6000 },
	{ 0.15, 0.0000,0.0000,0.6667 },
	{ 0.20, 0.0000,0.0000,0.8667 },
	{ 0.25, 0.0000,0.4667,0.8667 },
	{ 0.30, 0.0000,0.6000,0.8667 },
	{ 0.35, 0.0000,0.6667,0.6667 },
	{ 0.40, 0.0000,0.6667,0.5333 },
	{ 0.45, 0.0000,0.6000,0.0000 },
	{ 0.50, 0.0000,0.7333,0.0000 },
	{ 0.55, 0.0000,0.8667,0.0000 },
	{ 0.60, 0.0000,1.0000,0.0000 },
	{ 0.65, 0.7333,1.0000,0.0000 },
	{ 0.70, 0.9333,0.9333,0.0000 },
	{ 0.75, 1.0000,0.8000,0.0000 },
	{ 0.80, 1.0000,0.6000,0.0000 },
	{ 0.85, 1.0000,0.0000,0.0000 },
	{ 0.90, 0.8667,0.0000,0.0000 },
	{ 0.95, 0.8000,0.0000,0.0000 },
	{ 1.00, 0.8000,0.8000,0.8000 }
    };
    /*private*/ static byte[] hotmetal_lookup_red;
    /*private*/ static byte[] hotmetal_lookup_green;
    /*private*/ static byte[] hotmetal_lookup_blue;
    /*private*/ static byte[] spectral_lookup_red;
    /*private*/ static byte[] spectral_lookup_green;
    /*private*/ static byte[] spectral_lookup_blue;
    /*private*/ static byte[] labels_lookup_red;
    /*private*/ static byte[] labels_lookup_green;
    /*private*/ static byte[] labels_lookup_blue;
    /*private*/ static byte[] zeros;
    /*private*/ byte[] map0;
    /*private*/ byte[] map1;
    /*private*/ byte[] map2;

    // static initializer
    static {
	zeros= new byte[ 256];
	for( int i= 0; i < 256; ++i) 
	    zeros[ i]= 0;

	hotmetal_lookup_red= new byte[ 256];
	hotmetal_lookup_green= new byte[ 256];
	hotmetal_lookup_blue= new byte[ 256];
	_compute_hotmetal_lookup();

	spectral_lookup_red= new byte[ 256];
	spectral_lookup_green= new byte[ 256];
	spectral_lookup_blue= new byte[ 256];
	_compute_spectral_lookup();

	labels_lookup_red= new byte[ 256];
	labels_lookup_green= new byte[ 256];
	labels_lookup_blue= new byte[ 256];
	_compute_labels_lookup();
    }

    /*private*/ static final void _compute_hotmetal_lookup() {

	_compute_linear_ramp( 0, 255, 0, 127, hotmetal_lookup_red);
	_compute_linear_ramp( 0, 255, 64, 191, hotmetal_lookup_green);
	_compute_linear_ramp( 0, 255, 128, 255, hotmetal_lookup_blue);
    }
    
    /*private*/ static final void _compute_spectral_lookup() {

	int[] components= new int[ 3];
	int last_spec= spectral_specification.length - 1;
	final double step= 1.0 / last_spec;

	for( int lookup= 0; lookup <= 255; ++lookup) {

	    if( 255 == lookup) {
		for( int i= 0; i < 3; ++i)
		    components[ i]= 
			(int)Math.round( 255 * spectral_specification[ last_spec][ i+1]);
	    }
	    else {
		double fraction= lookup / 255.0;
		int interval= (int)Math.floor( fraction / step);
		// we're in: [ interval , interval+1 )
		double interpolator= (fraction - interval*step) / step; // [0,1)
		for( int i= 0; i < 3; ++i)
		    components[ i]= 
			(int)Math.round( 255 * 
					 (spectral_specification[ interval][ i+1] * 
					  (1.0-interpolator)
					  + 
					  spectral_specification[ interval+1][ i+1] * 
					  interpolator
					  ) 
					 );
	    }
	    spectral_lookup_red[ lookup]= (byte)components[ 0];
	    spectral_lookup_green[ lookup]= (byte)components[ 1];
	    spectral_lookup_blue[ lookup]= (byte)components[ 2];
	}
    }

    /*private*/ static final void _set_colour_of_label( final int l, 
							final Color col) {
        labels_lookup_red[ l]= (byte)col.getRed();
        labels_lookup_green[ l]= (byte)col.getGreen();
        labels_lookup_blue[ l]= (byte)col.getBlue();
    }

    /* adapted from "Display", copyright David MacDonald, Montreal
       Neurological Institute */
    /*private*/ static final void _compute_labels_lookup() {

	final int  	  n_labels= 256;
	int 		  n_colours, n_around, n_up, u, a;
	float      	  r, g, b, hue, sat;

	n_colours = 0;
	_set_colour_of_label( n_colours++, Color.black);
	_set_colour_of_label( n_colours++, Color.red);
	_set_colour_of_label( n_colours++, Color.green);
	_set_colour_of_label( n_colours++, Color.blue);
	_set_colour_of_label( n_colours++, Color.cyan);
	_set_colour_of_label( n_colours++, Color.magenta);
	_set_colour_of_label( n_colours++, Color.yellow);
	_set_colour_of_label( n_colours++, 
			      new Color( 0.541176f, 0.168627f, 0.886275f ) // BLUE_VIOLET 
				);
	_set_colour_of_label( n_colours++, 
			      new Color( 1.0f, 0.0784314f, 0.576471f ) // DEEP_PINK 
				);
	_set_colour_of_label( n_colours++, 
			      new Color( 0.678431f, 1.0f, 0.184314f ) // GREEN_YELLOW
				);
	_set_colour_of_label( n_colours++, 
			      new Color( 0.12549f, 0.698039f, 0.666667f ) // LIGHT_SEA_GREEN
				);
	_set_colour_of_label( n_colours++,
			      new Color( 0.282353f, 0.819608f, 0.8f ) // MEDIUM_TURQUOISE
				);
	_set_colour_of_label( n_colours++, 
			      new Color( 0.627451f, 0.12549f, 0.941176f ) // PURPLE
				);
	_set_colour_of_label( n_colours++, Color.white);


	// NB: this may be strange/weird/buggy, but that's how David coded it...

	n_around = 12;
	n_up = 1;
	while( n_colours < n_labels ) {

	  for( u= 0; u < n_up; ++u ) {

	    if( (u % 2) == 1 )
	      continue;

	    for( a= 0; a < n_around; ++a ) {

	      hue = (float) a / (float) n_around;
	      //sat = 0.2f + (0.5f - 0.2f) * ((float) u / (float) n_up);
	      sat = 0.5f + (0.9f - 0.5f) * ((float) u / (float) n_up);

	      if( n_colours < n_labels )
		_set_colour_of_label( n_colours++, 
				      Color.getHSBColor( hue, 1.0f, sat) );
	    }
	  }
	  n_up *= 2;
	}

	if( n_labels >= 256 )
	  _set_colour_of_label( 255, Color.black );
    }

    // instance initializer
    {
	// allocate these here, s.th. we don't have to allocate new ones
	// everytime get8bitColormap() is called...
	map0= new byte[ 256];
	map1= new byte[ 256];
	map2= new byte[ 256];
    }

    final public IndexColorModel get8bitColormap( final int color_coding,
						  final int lower_limit,
						  final int upper_limit
						  ) {
	return get8bitColormap( color_coding, lower_limit, upper_limit, null, null);
    }

    /** the "under" and, respectively, "over" areas are 
	(0...lower_limit-1) and (upper_limit+1...255)

	values of null for under_color and over_color mean that
	the first and, respectively, last colors of the color_coding
	should be extended in the "under" and "over" areas
    */
    synchronized final
	public IndexColorModel get8bitColormap( final int color_coding,
						final int lower_limit,
						final int upper_limit,
						final Color under_color,
						final Color over_color   
						) {

	/* Note: currently, this method's implementation is quite convoluted
	   (but all it's in the name of fast execution...)
	*/
	if( DEBUG) {
	    System.out.println( this + " under_color: " + under_color);
	    System.out.println( this + " over_color: " + over_color);
	}

	final byte[] red, green, blue; /** will store references to other arrays */

	if( !( 0 <= lower_limit && lower_limit <= upper_limit && upper_limit <= 255)) {
	    String msg= "invalid range: " + lower_limit + " " + upper_limit;
	    throw new IllegalArgumentException( msg);
	}
	final String msg;

	if( null == under_color && null == over_color) {

	    _compute_linear_ramp( 0, 255, lower_limit, upper_limit, map0);

	    switch( color_coding) {
	    case GRAY:
		red= green= blue= map0;
		break;
	    case RED:
		red= map0; green= blue= zeros;
		break;
	    case GREEN:
		red= zeros; green= map0; blue= zeros;
		break;
	    case BLUE:
		red= green= zeros; blue= map0;
		break;
	    case HOTMETAL:
		red= map0; green= map1; blue= map2;
		// use "stack" variable aliases for faster access
		final byte[] hotmetal_lookup_red= ColorCoding.hotmetal_lookup_red;
		final byte[] hotmetal_lookup_green= ColorCoding.hotmetal_lookup_green;
		final byte[] hotmetal_lookup_blue= ColorCoding.hotmetal_lookup_blue;
		for( int i= 255; i >= 0; --i) {
		    final int idx= 0xFF & map0[ i];
		    red[ i]= hotmetal_lookup_red[ idx];
		    green[ i]= hotmetal_lookup_green[ idx];
		    blue[ i]= hotmetal_lookup_blue[ idx];
		}
		break;
	    case SPECTRAL:
		red= map0; green= map1; blue= map2;
		// use "stack" variable aliases for faster access
		final byte[] spectral_lookup_red= ColorCoding.spectral_lookup_red;
		final byte[] spectral_lookup_green= ColorCoding.spectral_lookup_green;
		final byte[] spectral_lookup_blue= ColorCoding.spectral_lookup_blue;
		for( int i= 255; i >= 0; --i) {
		    final int idx= 0xFF & map0[ i];
		    red[ i]= spectral_lookup_red[ idx];
		    green[ i]= spectral_lookup_green[ idx];
		    blue[ i]= spectral_lookup_blue[ idx];
		}
		break;
	    case MNI_LABELS:
		red= map0; green= map1; blue= map2;
		// use "stack" variable aliases for faster access
		final byte[] labels_lookup_red= ColorCoding.labels_lookup_red;
		final byte[] labels_lookup_green= ColorCoding.labels_lookup_green;
		final byte[] labels_lookup_blue= ColorCoding.labels_lookup_blue;
		for( int i= 255; i >= 0; --i) {
		    final int idx= 0xFF & map0[ i];
		    red[ i]= labels_lookup_red[ idx];
		    green[ i]= labels_lookup_green[ idx];
		    blue[ i]= labels_lookup_blue[ idx];
		}
		break;
	    default:
		msg= color_coding + ": unknown color coding type!";
		throw new IllegalArgumentException( msg);
	    }
	} // end of: if( null == under_color && null == over_color)
	else {	
	    /* have to worry about under_color and over_color ... */

	    red= map0; green= map1; blue= map2; 
	    byte red_value, green_value, blue_value;
	    int range_min= 0;
	    int range_max= 255;
	    int i;
	    if( under_color != null) {
		range_min= lower_limit;
		red_value= (byte)under_color.getRed();
		green_value= (byte)under_color.getGreen();
		blue_value= (byte)under_color.getBlue();
		for( i= lower_limit-1; i >= 0; --i) { 
		    red[ i]= red_value; green[ i]= green_value; blue[ i]= blue_value;
		}
	    }
	    if( over_color != null) {
		range_max= upper_limit;
		red_value= (byte)over_color.getRed();
		green_value= (byte)over_color.getGreen();
		blue_value= (byte)over_color.getBlue();
		for( i= upper_limit+1; i <= 255; ++i) { 
		    red[ i]= red_value; green[ i]= green_value; blue[ i]= blue_value;
		}
	    }

	    /* the stuff in between */
	    int range= range_max - range_min + 1;
	    switch( color_coding) {
	    case GRAY:
		_compute_linear_ramp( range_min, range_max,
				      lower_limit, upper_limit, red);
		System.arraycopy( red, range_min, green, range_min, range);
		System.arraycopy( red, range_min, blue, range_min, range);
		break;
	    case HOTMETAL:
		_compute_linear_ramp( range_min, range_max,
				      lower_limit, upper_limit, red);
		// use "stack" variable aliases for faster access
		final byte[] hotmetal_lookup_red= ColorCoding.hotmetal_lookup_red;
		final byte[] hotmetal_lookup_green= ColorCoding.hotmetal_lookup_green;
		final byte[] hotmetal_lookup_blue= ColorCoding.hotmetal_lookup_blue;
		for( i= range_min; i <= range_max; ++i) {
		    final int idx= 0xFF & red[ i];
		    red[ i]= hotmetal_lookup_red[ idx];
		    green[ i]= hotmetal_lookup_green[ idx];
		    blue[ i]= hotmetal_lookup_blue[ idx];
		}
		break;
	    case SPECTRAL:
		_compute_linear_ramp( range_min, range_max,
				      lower_limit, upper_limit, red);
		// use "stack" variable aliases for faster access
		final byte[] spectral_lookup_red= ColorCoding.spectral_lookup_red;
		final byte[] spectral_lookup_green= ColorCoding.spectral_lookup_green;
		final byte[] spectral_lookup_blue= ColorCoding.spectral_lookup_blue;
		for( i= range_min; i <= range_max; ++i) {
		    final int idx= 0xFF & red[ i];
		    red[ i]= spectral_lookup_red[ idx];
		    green[ i]= spectral_lookup_green[ idx];
		    blue[ i]= spectral_lookup_blue[ idx];
		}
		break;
	    case MNI_LABELS:
		_compute_linear_ramp( range_min, range_max,
				      lower_limit, upper_limit, red);
		// use "stack" variable aliases for faster access
		final byte[] labels_lookup_red= ColorCoding.labels_lookup_red;
		final byte[] labels_lookup_green= ColorCoding.labels_lookup_green;
		final byte[] labels_lookup_blue= ColorCoding.labels_lookup_blue;
		for( i= range_min; i <= range_max; ++i) {
		    final int idx= 0xFF & red[ i];
		    red[ i]= labels_lookup_red[ idx];
		    green[ i]= labels_lookup_green[ idx];
		    blue[ i]= labels_lookup_blue[ idx];
		}
		break;
	    case RED:
		_compute_linear_ramp( range_min, range_max,
				      lower_limit, upper_limit, red);
		System.arraycopy( zeros, range_min, green, range_min, range);
		System.arraycopy( zeros, range_min, blue, range_min, range);
		break;
	    case GREEN:
		System.arraycopy( zeros, range_min, red, range_min, range);
		_compute_linear_ramp( range_min, range_max,
				      lower_limit, upper_limit, green);
		System.arraycopy( zeros, range_min, blue, range_min, range);
		break;
	    case BLUE:
		System.arraycopy( zeros, range_min, red, range_min, range);
		System.arraycopy( zeros, range_min, green, range_min, range);
		_compute_linear_ramp( range_min, range_max,
				      lower_limit, upper_limit, blue);
		break;
	    default:
		msg= color_coding + ": unknown color coding type!";
		throw new IllegalArgumentException( msg);
	    }
	}

	return new IndexColorModel( 8, 256, red, green, blue);
    }

    /** it's assumed that:
	0 <= range_min <= start <= end <= range_max <= 255   and
	destination.length == 256
    */
    /*private*/ static final void _compute_linear_ramp( final int range_min,
							final int range_max,
							final int start,
							final int end,
							byte[] destination ) {
	int i;
	for( i= range_min; i < start; ++i) 
	    destination[ i]= (byte)0;
	
	if( start == end)
	    destination[ start]= (byte)127;
	else {
	    final int end_minus_start= end - start;
	    for( i= start; i <= end; ++i) 
		destination[ i]= (byte)( 255 * (i - start) / end_minus_start);
	}
	for( i= end+1; i <= range_max ; ++i) 
	    destination[ i]= (byte)255;
    }

} // end of class ColorCoding


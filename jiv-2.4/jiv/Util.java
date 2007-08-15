
// $Id: Util.java,v 1.9 2003/09/01 10:16:25 crisco Exp $
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
import java.net.*;
import java.io.*;
import java.util.zip.*;
import java.util.*;

/**
 * A collection of various (<code>static</code>) utility functions.
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: Util.java,v 1.9 2003/09/01 10:16:25 crisco Exp $
 */
public final class Util {

    /** used by reduceSignificantDigits() */
    /*private*/ static final int[] __chopFractionalPart_lookup= {
	1, 10, 100, 1000, 10000, 100000
    };

    /** Limitation: (original * 10^no_of_digits) has to fit in a
	signed integer (java integers are 32bit)
    */
    public static final float chopFractionalPart( float original,
						  int no_of_digits /** 0...5 */
						  ) {
	// use a lookup table for speed
	int mult= __chopFractionalPart_lookup[ no_of_digits];
	return Math.round( original * mult) / (float)mult;
    }


    /*private*/ static final float _log_e_10= (float)Math.log( 10.0);

    public static final float chopToNSignificantDigits( final float original,
							int no_of_sig_digits 
							) {
	if( original == 0f)
	    return 0f;

	/* a plain float->int conversion (which is a 'trunc' in java)
           instead of the 'floor' won't be correct if the argument is
           negative (eg original = 1.23e-7) */
	final int exp= 
	    (int)Math.floor( (float)Math.log( Math.abs( original)) 
			     / _log_e_10
			     - no_of_sig_digits + 1f 
			     );

	/* it's important to use double here! else, '1e{exp}' may be
           <Float.MIN_VALUE for a very small 'original' */
	final double mult= Math.pow( 10.0, exp);

	return (float)( Math.round( original / mult) * mult );
    }


    /**
     * @return first enclosing <code>Frame</code> encountered up the
     * component hierarchy
     */
    public static final Frame getParentFrame( Component component) {
	
	Component f= component;
	while( f != null && !( f instanceof Frame ) ) 
	    f= f.getParent();
	return (Frame)f;
    }


    public static final String[] compress_extension= { ".gz", ".bz2" };

    /**
     * @return "extension" of a filename (or url), ignoring '.gz' and '.bz2'
     */
    public static final String getExtension( String s) {
	
	String ext= "";
	for( int i= 0; i < compress_extension.length; ++i) 
	    if( s.endsWith( compress_extension[ i]) ) {
		ext= compress_extension[ i];
		int chop= s.lastIndexOf( compress_extension[ i]);
		s= s.substring( 0, chop);
		break;
	    }
	int ei= s.lastIndexOf( '.');
	if( ei >= 0 ) 
	    return s.substring( ei) + ext;
	else
	    // hmm, no extension really...
	    return ext;
    }

    /** sleeps the current thread, and (unlike Thread.sleep) doesn't throw
	an exception if interrupted. 
    */
    public static final void sleep( long millis ) {

	try {
	    Thread.sleep( millis);
	}
	catch( InterruptedException e) {}
    }


    /** 
	Note1: it is the caller's responsibility to close the returned
	InputStream.

	Note2: it's not _required_ to declare SecurityException (since
	it's a subclass of RuntimeException), but we do it for clarity
	-- this error is likely to happen when working with url-s, so
	it should be treated as a "checked exception" ...  
    */
    public static final InputStream openURL( URL source_url) 
	throws IOException, SecurityException  {

	if( false ) { System.out.println( "Util::openUrl( " + source_url +" )"); }

	InputStream input_stream= null;

	URLConnection url_connection= source_url.openConnection();
	// NB: the connection is not yet opened at this point; it'll be 
	// actually done when calling getInputStream() below.
	url_connection.setUseCaches( true);

	if( url_connection instanceof HttpURLConnection ) {
	    HttpURLConnection http_conn= (HttpURLConnection)url_connection;
	    if( http_conn.getResponseCode() != HttpURLConnection.HTTP_OK)
		throw new IOException( source_url.toString() + " : "
				       + http_conn.getResponseCode() + " " 
				       + http_conn.getResponseMessage() );
	}	
	input_stream= url_connection.getInputStream();
	if( source_url.toString().endsWith( ".gz")) 
	    // use a larger decompression buffer than the 512 default
	    input_stream= new GZIPInputStream( input_stream, 4096);

	// TODO: I once got a "IOException: server status 206" when loading
	// the applet via http ... but couldn't reproduce it ever since.
	// what went wrong?
	// (btw, 206 == HTTP_PARTIAL : 
	// "HTTP response code that means the partial request has been fulfilled") 

	return input_stream;
    }


    /** @return the Properties object described by the file at
	source_url; trailing whitespace is trimmed off the property
	values (the stock Java Properties.load() doesn't do it!)  
    */
    public static final Properties readProperties( URL source_url, 
						   Properties defaults) 
	throws IOException, SecurityException  {

	if( source_url == null) 
	    return null;

	InputStream input_stream= null;
	try {
	    input_stream= Util.openURL( source_url);
	    return _readAndTrimProperties( input_stream, defaults);
	}
	finally {
	    if( input_stream != null) {
		// TODO: what if we try to close() a stream that wasn't 
		// successfully opened? is this a problem?
		input_stream.close();
	    }
	}
    }

    /** @return the Properties object described inline in the source
	String (with ';' instead of newlines); trailing whitespace is
	trimmed off the property values (the stock Java
	Properties.load() doesn't do it!)  
    */
    public static final Properties readProperties( final String src, 
						   Properties defaults) 
	throws IOException {

	PipedOutputStream os= new PipedOutputStream();
	PipedInputStream is= new PipedInputStream( os);
	final Writer pw= new BufferedWriter( new OutputStreamWriter( os));

	// cannot have a pair of Piped...Stream-s in the same thread
	// (deadlock danger)
	Thread t= new Thread( new Runnable() {
		public void run() { 
		    try {
			String lineSeparator = System.getProperty("line.separator");
			// replace ';' with newline
			StringTokenizer lines= new StringTokenizer( src, ";", false);
			while( lines.hasMoreTokens() ) {
			    pw.write( lines.nextToken() + lineSeparator);
			    pw.flush();
			}
			pw.close();
		    }
		    catch( Exception x) {
			System.err.println( "Exception (" + x + ") when writing to pipe in readProperties( " + src + " )");
		    }
		}
	    });

	try {
	    t.start();
	    Properties ret= _readAndTrimProperties( is, defaults);
	    return ret;
	}
	finally {
	    is.close(); os.close(); 
	}
    }
    
    /** @return trims trailing whitespace off the values (the stock Java
	Properties.load() doesn't do it!)  
    */
    /*private*/ static final Properties _readAndTrimProperties( InputStream src,
								Properties defaults) 
	throws IOException {

	Properties raw= new Properties( defaults);
	raw.load( src);
	Enumeration prop_names;
	Properties ret= new Properties();
	for( prop_names= raw.propertyNames(); prop_names.hasMoreElements(); ) {
	    String key= (String)prop_names.nextElement();
	    ret.put( key, raw.getProperty( key).trim());
	}
	return ret;
    }


    public static final String arrayToString( byte[] arg) {

	StringBuffer s= new StringBuffer( arg.toString() );
	s.append( " : ");
	for( int i= 0; i < arg.length; ++i) {
	    s.append( arg[ i]);
	    s.append( " ");
	}
	return s.toString();
    }

    public static final String arrayToString( int[] arg) {

	StringBuffer s= new StringBuffer( arg.toString() );
	s.append( " : ");
	for( int i= 0; i < arg.length; ++i) {
	    s.append( arg[ i]);
	    s.append( " ");
	}
	return s.toString();
    }

} // end of class Util

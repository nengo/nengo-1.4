
// $Id: StandaloneAppletStub.java,v 1.1 2001/04/08 00:04:28 cc Exp $
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

import java.applet.*;
import java.net.URL;
import java.util.Hashtable;

/**
 * Provides a (very basic) <code>AppletStub</code>, allowing an
 * applet to be run as a standalone application, without an
 * appletviewer or web browser. In other words, this (together with
 * <code>StandaloneAppletContext</code>) replaces the
 * appletviewer/browser.
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: StandaloneAppletStub.java,v 1.1 2001/04/08 00:04:28 cc Exp $
 *
 * @see StandaloneAppletContext 
 */
public final class StandaloneAppletStub implements AppletStub {

    /*private*/ static final boolean			DEBUG= false;

    /*private*/ static final StandaloneAppletContext 	APPLET_CONTEXT= 
	new StandaloneAppletContext();
    /*private*/ URL 					base_url= null;
    /*private*/ Hashtable				parameters= 
	new Hashtable();

    public void appletResize( int width, int height) { }

    public AppletContext getAppletContext() { return APPLET_CONTEXT; }

    public URL getCodeBase() { return getDocumentBase(); }

    public URL getDocumentBase() 
    { 
	if( null == base_url) {

	    String cwd= null;
	    try { 
		cwd= System.getProperty( "user.dir"); 
		if( null == cwd) {
		    System.err.println( "Warning: cannot determine " 
					+ "current working directory!" );
		}
		else {
		    String file_separator= System.getProperty( "file.separator");
		    if( ! cwd.endsWith( file_separator))
			cwd += file_separator;
		} 
	    }
	    catch( SecurityException ex) { System.err.println( ex); }
	    if( DEBUG) System.out.println( "cwd: " + cwd);

	    try { 
		base_url= new URL( "file:" + cwd);
	    }	
	    catch( java.net.MalformedURLException ex) {
		System.err.println( "Internal trouble in " + 
				    "StandaloneAppletStub.getDocumentBase(): " +
				    ex);
	    }
	    if( DEBUG) System.out.println( "base_url: " + base_url);
	}
	return base_url;
    }

    public String getParameter( String name)
    { 
	return (String)parameters.get( name);
    }

    public boolean isActive() { return true; }

    public void setParameter( String param, String value) 
    {
	parameters.put( param, value);
    }

} // end of class StandaloneAppletStub



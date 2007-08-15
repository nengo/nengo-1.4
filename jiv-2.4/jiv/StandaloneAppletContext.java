
// $Id: StandaloneAppletContext.java,v 1.2 2001/05/15 19:21:55 crisco Exp $
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
import java.util.Enumeration;
import java.net.URL;
import java.awt.Image;

/**
 * Provides a (very basic) <code>AppletContext</code>, allowing an
 * applet to be run as a standalone application, without an
 * appletviewer or web browser. In other words, this (together with
 * <code>StandaloneAppletStub</code>) replaces the
 * appletviewer/browser.
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: StandaloneAppletContext.java,v 1.2 2001/05/15 19:21:55 crisco Exp $
 *
 * @see StandaloneAppletStub 
 */
public final class StandaloneAppletContext implements AppletContext {

    /** where to refer users for more information */
    /*private*/ static final String HOME_PAGE= 
	"http://www.bic.mni.mcgill.ca/~crisco/jiv/";
    
    public Applet getApplet( String name) 
    { 
	_notImplemented( "getApplet"); return null;
    }

    public Enumeration getApplets() 
    { 
	_notImplemented( "getApplets"); return null;
    }

    public AudioClip getAudioClip( URL url) 
    { 
	_notImplemented( "getAudioClip"); return null;
    }

    public Image getImage( URL url) 
    { 
	_notImplemented( "getImage"); return null;
    }

    public void showDocument( URL url) 
    { 
	showDocument( url, "_self");
    }

    /* TODO: write a better implementation (e.g. spawn Netscape?) 
     */
    public void showDocument( URL url, String frame) 
    { 
	System.err.println( "=============");
	System.err.println( "Cannot display HTML documents in standalone mode.");
	System.err.println( "Please see " + HOME_PAGE);
	System.err.println( "=============");
    }

    public void showStatus( String message) 
    { 
	System.out.println( "*** " + message + " ***");
    }

    /*private*/ void _notImplemented( String method) 
    {
	System.err.println( "StandaloneAppletContext." + method + 
			    " : not implemented!");
    }

} // end of class StandaloneAppletContext



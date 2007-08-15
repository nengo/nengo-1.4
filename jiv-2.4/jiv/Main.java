
// $Id: Main.java,v 1.19 2003/12/21 22:34:43 crisco Exp $

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

import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;

/**
 * The entry-point class, both when running as an applet and when
 * running as a standalone application. It reads/parses all the input
 * parameters (including the config file), initializes all the
 * necessary objects, and generally handles all the system
 * interaction. It also sets or unsets (at user's request) the "global
 * position sync" mode.
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: Main.java,v 1.19 2003/12/21 22:34:43 crisco Exp $
 */
public final class Main extends java.applet.Applet {

    /*private*/ static final boolean 	DEBUG= false;

    /*private*/ Thread 		initialization_thread;
    /*private*/ Frame 		jiv_frame;
    /** keeps the current state of the postion sync */
    /*private*/ boolean 	position_sync;
    /* these fields initialized by _parseConfig():
     */
    /*private*/ boolean	 	initial_position_sync;	// Java default is false
    /*private*/ boolean	 	byte_voxel_values;	// Java default is false
    /*private*/ boolean	 	enable_world_coords= true;
    /*private*/ int		download_method= Data3DVolume.DOWNLOAD_UPFRONT;
    /** holds VolumeStruct, indexed by alias */
    /*private*/ Hashtable	volumes= new Hashtable();  
    /** holds VolumeHeader, indexed by alias */
    /*private*/ Hashtable	headers= new Hashtable();  
    /** holds PanelStruct (and may contain gaps) */
    /*private*/ Vector 		panels= new Vector(); 
    /*private*/ int		no_of_panels;

    /*private*/ CheckboxMenuItem sync_menu;

    /** entry point when running as a standalone application */
    public static void main( String argv[])
    {
	StandaloneAppletStub stub= new StandaloneAppletStub();
	String config_file= null;

	if( argv.length > 0 )
	    config_file= argv[ 0];
	try {
	    if( null == config_file) 
		config_file= System.getProperty( "config");
	    if( null == config_file) 
		config_file= System.getProperty( "cfg");
	}	
	catch( SecurityException e) { }
	stub.setParameter( "config", config_file);

	Main applet= new Main();
	applet.setStub( stub);
	applet.init();
    }

    /** entry point when running as an applet */
    public void init() 
    {
	/** We shoudn't do any lengthy processing in here, since this
	    runs in a thread which we don't own (and which may have other
	    time critical stuff to do...).  Hence, we start off a thread
	    of our own to do the initialization.
	*/
	// this test probably not needed: init() should always be called, 
	// and exactly once...
	if( initialization_thread == null) {

	    initialization_thread= new Thread( new Runnable() {
		public void run() { _doInitialization(); }
	    });
	    initialization_thread.start();
	}
    }

    public String getAppletInfo()
    {
	return About.getShortVersion();
    }

    public String[][] getParameterInfo()
    {
	final String[][] param_info= {
	    { "config",	"URL", "location of a JIV config file"},
	    { "cfg",	"URL", "location of a JIV config file"},
	    { "inline_config",	"String", "inline JIV config"},
	    { "inline_cfg",	"String", "inline JIV config"}
	};
	return param_info;
    }

    /*private*/ void _doInitialization() {

	int i;
	if( false) {
	    Runtime rt= Runtime.getRuntime();
	    System.out.println( rt);
	    // some JVMs (e.g. kaffe) crash on this:
	    rt.traceMethodCalls( true);
	}
	URL document_base= getDocumentBase();
	if( DEBUG) System.out.println( "document base:" + document_base);
	/* Try everything... (e.g. kaffe 1.0.5 's getDocumentBase()
           returns null sometimes) */
	URL code_base= getCodeBase();
	if( DEBUG) System.out.println( "code base:" + code_base);
	if( null == document_base)
	    document_base= code_base;

	// TODO: test if the following redirect actually works 
	// (with the crt implementation of Data3DVolume)
	// 
	// NS 4.61 throws a SecurityException on this fct call:
	// "netscape.security.AppletSecurityException: security.cannot set factory"
	if( false) HttpURLConnection.setFollowRedirects( true);

	/* try several things, in a certain order, 
	   until we get something or we run out of choices... 
	*/
	String config_file= null; // local ("stack") vars don't have default values
	String inline_config= null;
	VolumeHeader common_sampling= null;

	if( false) {
	    try {
		/* Note: this is most probably pointless -- according
		   to O'Reilly's Java 1.1 books, applets are never
		   allowed to read system properties other than the
		   brief "official" list of sys props for applets
		   ("os.name", "os.arch", etc) ... Moreover, a
		   "certain browser" (MSIE, who else) even prints a
		   (harmless) warning to the Java console.  */
		config_file= System.getProperty( "config");
		if( null == config_file) 
		    config_file= System.getProperty( "cfg");
	    }
	    catch( SecurityException e) { }
	}
	if( null == config_file) 
	    config_file= getParameter( "config");	// i.e. from the html file
	if( null == config_file) 
	    config_file= getParameter( "cfg");
	if( null == inline_config) 
	    inline_config= getParameter( "inline_config");
	if( null == inline_config) 
	    inline_config= getParameter( "inline_cfg");
	try {
	    Properties config= null;
	    URL url_context= document_base;
	    progressMessage( "reading config...");

	    if( config_file != null) {
		/* NB: if config_file is not a full URL, then a non-null
		   context value is expected by this constructor */
		URL config_url= new URL( url_context, config_file);
		if( DEBUG) System.out.println( "config_url:" + config_url);
		config= Util.readProperties( config_url, null);
		url_context= config_url;
	    }
	    if( inline_config != null)
		config= Util.readProperties( inline_config, config);
	    if( config == null) 
		throw new Exception( "no config found!");
	    if( DEBUG) System.out.println( "parsing config...");
	    _parseConfig( config, url_context);
	    if( DEBUG) System.out.println( "config parsed.");

	    /* compute list of volumes that are actually displayed in
               some panel */
	    // holds aliases (String) :
	    Vector displayed_aliases= new Vector();
	    // holds headers (VolumeHeader) :
	    Vector displayed_headers= new Vector();
	    for( i= 0; i < panels.size(); ++i) {
		PanelStruct ps= (PanelStruct)panels.elementAt( i);
		if( null == ps || 
		    ps.alias1 != null) // skip combined panels
		    continue;
		Object v= volumes.get( ps.alias0);
		Object h= headers.get( ps.alias0);
		if( null == v || null == h) 
		    throw new IOException( "volume alias " + ps.alias0 + " not defined");
		displayed_aliases.addElement( ps.alias0);
		displayed_headers.addElement( h);
	    }
	    if( displayed_headers.isEmpty()) 
		throw new IOException( "no individual volume panels to display!?");

	    common_sampling= 
		VolumeHeader.getCommonSampling( displayed_headers.elements());
	    CoordConv.set( common_sampling);

	    progressMessage( "loading data...");
	    Enumeration e;
	    for( e= displayed_aliases.elements(); e.hasMoreElements(); ) {
		String alias= (String)e.nextElement();
		VolumeHeader vh= (VolumeHeader)headers.get( alias);
		VolumeStruct vs= (VolumeStruct)volumes.get( alias);
		vs.data= new Data3DVolume( common_sampling, 
					   new URL( url_context, vs.file), 
					   vh,
					   alias,
					   download_method );
	    }
	}
	// this will catch more than we should care about, but it saves us from
	// writing several catch clauses...
	catch( Exception e) {
	    progressMessage( "error! see console for details...");
	    System.err.println( "Error! " + e);
	    e.printStackTrace( System.err);
	    destroy();
	    return;
	}
	progressMessage( "opening window...");
	jiv_frame = new Frame("JIV");

	/* Note about setting the applet size: it doesn't work in NS,
	   and sort of works in JDK... -- the reliable way to do it to
	   specify the dimension in the applet html tag.  But the best
	   way to do it properly is to open a separate frame! */
	// jiv_frame.setSize() not needed: pack() does a better job!
	jiv_frame.setLayout( new GridBagLayout());
	GridBagConstraints gbc= new GridBagConstraints();
	gbc.fill= GridBagConstraints.VERTICAL;
	gbc.weighty= 1.0;
	gbc.gridy= 0;
	gbc.gridheight= GridBagConstraints.REMAINDER;

	no_of_panels= 0;
	for( i= 0; i < panels.size(); ++i) 
	    if( null != panels.elementAt( i)) 
		++no_of_panels;

	int column;
	for( i= 0, column= 0; i < panels.size(); ++i) {
	    PanelStruct ps= (PanelStruct)panels.elementAt( i);
	    if( null == ps) 	    // skip the gaps in the panel vector
		continue;
	    if( column > 0) {
		/* add the panel separator */
		gbc.gridx= column++;
		jiv_frame.add( new VerticalLineComponent(), gbc);
	    }
	    if( ps.alias1 != null) {
		/* skip combined panels; construct them later, after
                   all individual panels have been initialized */
		column++;
		continue;
	    }
	    Data3DVolume data= ((VolumeStruct)volumes.get( ps.alias0)).data;
	    ps.gui= new IndividualDataVolumePanel( data, jiv_frame, column++,
						   common_sampling.getFOVCenter(),
						   enable_world_coords,
						   byte_voxel_values,
						   ps.range_start, ps.range_end,
						   ps.color_coding, 
						   this);
	}
	for( i= 0, column= 0; i < panels.size(); ++i) {
	    PanelStruct ps= (PanelStruct)panels.elementAt( i);
	    if( null == ps) 	    // skip the gaps in the panel vector
		continue;
	    if( column > 0) {
		// the panel separator (already done) ...
		column++;
	    }
	    if( null == ps.alias1) {
		// an individual panel (already done) ...
		column++;
		continue;
	    }
	    DataVolumePanel p0, p1;
	    p0= ((PanelStruct)panels.elementAt( _panelIndexOf( ps.alias0))).gui;
	    p1= ((PanelStruct)panels.elementAt( _panelIndexOf( ps.alias1))).gui;
	    ps.gui= new CombinedDataVolumePanel( (IndividualDataVolumePanel)p0, 
						 (IndividualDataVolumePanel)p1,
						 jiv_frame, column++, 
						 common_sampling.getFOVCenter(),
						 enable_world_coords, 
						 this);
	}
	setPositionSync( initial_position_sync);

	jiv_frame.addWindowListener( new WindowAdapter() {
	    public void windowClosing( WindowEvent e) { Main.this.destroy(); }
	});

	/* Window::pack() resizes the window according to the preferred size
	   of its components -- Frame is a subclass of Window */
        jiv_frame.pack();
        jiv_frame.show();
	progressMessage( "init done.");
    }

    /** This is called (by the applet's environment) when the applet is about
	to be permanently stopped. It should free up any resources that the
	applet is holding (e.g. frames, etc) 

	We also call it in response to the 'Quit' command from 
	DataVolumePanel-s and in response to "window closing" events received
	by jiv_frame...
    */
    public void destroy()
    {
	if( jiv_frame != null) {
	    jiv_frame.setVisible( false);
	    jiv_frame.dispose();
	    jiv_frame= null;
	}
	if( initialization_thread != null) {
	    /* Note: The isAlive test is necessary -- under the
	       Java1.2 JDK, if you try to stop this thread after it
	       exited its "run()" method, the JVM will throw
	       'java.security.AccessControlException: access denied
	       (java.lang.RuntimePermission modifyThread )' */
	    if( initialization_thread.isAlive()) {
		if( DEBUG) 
		    System.out.println( "init thread still alive: stopping it");
		/* TODO: Thread::stop() is actually not recommended
		   (it's even "deprecated" in Java1.2) -- see
		   [jdk1.2.2]/docs/guide/misc/threadPrimitiveDeprecation.html
		   Q: is this really a problem here? (the applet gets
		   killed anyway, so we don't care if the thread
		   leaves damaged objects behind, right?)  If it is a
		   problem, then find another way to implement this
		   functionality (see the doc file above) */
		initialization_thread.stop();
	    }
	    else {
		if( DEBUG) 
		    System.out.println( "init thread is already dead");
	    }
	    initialization_thread= null;
	}
	if( volumes != null) {
	    for( Enumeration e= volumes.elements(); e.hasMoreElements(); ) {
		Data3DVolume v= ( (VolumeStruct)e.nextElement() ).data;
		if( v != null )
		    v.stopDownloads();
	    }
	    volumes.clear(); volumes= null;
	}
	if( panels != null) {
	    panels.removeAllElements(); panels= null;
	}
	progressMessage( "exited.");

	/* Note: If we run as a standalone application, this will
           terminate the JVM (and the application with it).  If we run
           as an applet, the security manager may not allow
           terminating the JVM (e.g. when running in a browser), but
           it doesn't hurt to try it because we catch the exception.
           However, if we do run inside an applet viewer w/o the usual
           applet restrictions then quiting one applet will kill any
           other applets running inside the same appletviewer
           (e.g. "clones") -- not a feature! 
	*/
	try { System.exit( 0); }
	catch( SecurityException ex) { }
    }

    /* see Java Programmer's FAQ (http://www.best.com/~pvdl/javafaq.html)
       sect12, Q2 for some useful info on which browsers call 
       {init,start,stop,destroy} and when.
    */
    /** TODO? ... */
    public void start() { }

    /** TODO? ... */
    public void stop() { }

    /** if true, the cursor position is sync-ed for all the panels */
    public synchronized void setPositionSync( boolean new_setting) {

	if( new_setting == position_sync)
	    // no change, nothing to do...
	    return;

	PanelStruct panel_struct_a, panel_struct_b;
	DataVolumePanel panel_a, panel_b;
	position_sync= new_setting;
	boolean a_is_leftmost= true;
	for( int a= 0; a < panels.size(); ++a) {
	    // the panels vector may contain "gaps"...
	    if( null == ( panel_struct_a= (PanelStruct)panels.elementAt( a)) ||
		null == ( panel_a= panel_struct_a.gui) )
		continue;
	    for( int b= 0; b < panels.size(); ++b) {
		if( a == b) 
		    continue;
		if( null == ( panel_struct_b= (PanelStruct)panels.elementAt( b)) ||
		    null == ( panel_b= panel_struct_b.gui) )
		    continue;
		if( true == new_setting) {
		    // turn sync mode on
		    panel_a.addPositionListener( panel_b, a_is_leftmost);
		}
		else {
		    // turn sync mode off
		    panel_a.removePositionListener( panel_b);
		}
	    }
	    panel_a.setPositionSync( new_setting);
	    a_is_leftmost= false;
	}
	if( sync_menu != null) 
	    sync_menu.setState( new_setting);
    }

    public boolean getPositionSync() {
	return position_sync;
    }

    public int getNumberOfPanels() {
	return no_of_panels;
    }

    /*private*/ Label msg;

    void progressMessage( String new_message) {

	/* Note: we could also call Applet::showStatus(), but it's
	   (arguably) more reliable to directly call
	   AppletContext::showStatus() in order to deal with any
	   strange (non-standard) implementations of
	   java.applet.Applet ... */
	// this goes to browser's status line (hopefully)
	getAppletContext().showStatus( "JIV: " + new_message); 
	// also display the message in the applet graphics area:
	if( null == msg) {
	    msg= new Label( new_message);
	    add( msg);
	    this.validate();	// on label's container
	}
	else {
	    msg.setText( new_message);
	    msg.invalidate();	// need to do it: labels aren't very smart.
	    this.validate();	// on label's container
	}
    }

    /** used by _parseConfig() 
	in converting from the string representation (in the config file) to 
	the internal int representation */    
    /*private*/ static final Object[][] color_codings_array= {
	{ "grey", new Integer( ColorCoding.GREY) },
	{ "gray", new Integer( ColorCoding.GREY) },
	{ "hotmetal", new Integer( ColorCoding.HOTMETAL) },
	{ "spectral", new Integer( ColorCoding.SPECTRAL) },
	{ "red", new Integer( ColorCoding.RED) },
	{ "green", new Integer( ColorCoding.GREEN) },
	{ "blue", new Integer( ColorCoding.BLUE) },
	{ "mni_labels", new Integer( ColorCoding.MNI_LABELS) },
	{ "mni-labels", new Integer( ColorCoding.MNI_LABELS) }
	};
    /*private*/ static Hashtable color_coding;
    static {
	color_coding= new Hashtable();
	for( int i= 0; i < color_codings_array.length; ++i)
	    color_coding.put( color_codings_array[i][0], color_codings_array[i][1]);
    }

    /** used by _parseConfig() 
	in converting from the string representation (in the config file) to 
	the internal int representation */    
    /*private*/ static final Object[][] dnld_method_array= {
	{ "upfront", new Integer( Data3DVolume.DOWNLOAD_UPFRONT) },
	{ "on_demand", new Integer( Data3DVolume.DOWNLOAD_ON_DEMAND) },
	{ "hybrid", new Integer( Data3DVolume.DOWNLOAD_HYBRID) },
	};
    /*private*/ static Hashtable dnld_method_convert;
    static {
	dnld_method_convert= new Hashtable();
	for( int i= 0; i < dnld_method_array.length; ++i)
	    dnld_method_convert.put( dnld_method_array[i][0], dnld_method_array[i][1]);
    }

    /** fills-in the following fields of Main: volumes, headers, panels, 
	position_sync;
	throws an Exception if any errors were encountered 
    */
    /*private*/ void _parseConfig( Properties config, URL url_context) 
	throws IOException, NumberFormatException {

	Enumeration prop_names;

	/* ** first pass: extract the "volume aliases" and headers ** 
	 */
	VolumeHeader default_header= null;
	for( prop_names= config.propertyNames(); prop_names.hasMoreElements(); ) {
	    String alias;
	    String name= (String)prop_names.nextElement();
	    if( name.startsWith( "jiv."))
		continue;
	    if( name.endsWith( ".header")) {
		alias= name.substring( 0, name.lastIndexOf( '.'));
		if( DEBUG) 
		    System.out.println( "VolumeHeader " + url_context + " + " + 
					config.getProperty( name)
					);
		VolumeHeader vh= 
		    new VolumeHeader( new URL( url_context, config.getProperty( name)));
		headers.put( alias, vh);
		/* the first header encountered becomes the default */
		if( default_header == null ) 
		    default_header= vh;
		continue;
	    }
	    // then it's a "volume alias"!
	    alias= name;
	    VolumeStruct vs= new VolumeStruct();

	    /* the reason why we don't store 'new URL(url_context,file)' 
	       instead is to allow the old inline config hack
	       (Dario's) to work: there will be lots of bogus aliases
	       (which could be invalid URLs), but that's ok (actually
	       not _always_ Ok, but less of a problem anyway) if we
	       delay building URLs out of them until we know which
	       volumes are actually displayed, ie until later, when
	       loading data, see 'new Data3DVolume(...)'  
	    */
	    if( null == ( vs.file= config.getProperty( alias)) ) 
		throw new IOException( "no data file given for volume alias " + alias);
	    volumes.put( alias, vs);
	    /* NB: if the same volume alias is declared several times
               in the config file, it cannot be predicted which one
               declaration will be considered! 
	    */
	}
	if( default_header == null ) 
	    default_header= new VolumeHeader();
	/* set any headers that were not explicitly specified */
	for( Enumeration aliases= volumes.keys(); aliases.hasMoreElements(); ) {
	    String alias= (String)aliases.nextElement();
	    if( headers.get( alias) == null )
		headers.put( alias, default_header);
	}
	/* ** second pass: look for individual panels **
	 */
	Hashtable combined_panels= new Hashtable();
	for( prop_names= config.propertyNames(); prop_names.hasMoreElements(); ) {
	    String name= (String)prop_names.nextElement();
	    StringTokenizer name_tokens= new StringTokenizer( name, ".", false);
	    if( name.endsWith( ".") || name_tokens.countTokens() > 4)
		throw new IOException( "invalid: " + name);
	    if( !"jiv".equals( name_tokens.nextToken()) || 
		!name_tokens.hasMoreTokens() || 
		!"panel".equals( name_tokens.nextToken()) ||
		!name_tokens.hasMoreTokens() )
		continue;	// must be a volume alias ...
	    // this can throw NumberFormatException
	    int panel_number= Integer.parseInt( name_tokens.nextToken());
	    if( panel_number < 0)
		throw new IOException( "negative panel number: " + panel_number);
	    if( panels.size() < panel_number+1) 
		panels.setSize( panel_number+1);
	    PanelStruct panel_struct;
	    if( null == ( panel_struct= (PanelStruct)panels.elementAt( panel_number)) )
		panels.setElementAt( panel_struct= new PanelStruct(), panel_number);
	    if( !name_tokens.hasMoreTokens()) {
		// it's the volume alias for an individual panel!
		if( null == (panel_struct.alias0= config.getProperty( name)) )
		    throw new IOException( name + 
					   " should be followed by a volume alias");
		// just in case we're overwritting a combined volume specification 
		panel_struct.alias1= null; 
		continue;	// go to another config file line...
	    }
	    String last_token= (String)name_tokens.nextElement();
	    if( last_token.equals( "coding")) {
		panel_struct.color_coding= 
		    ((Integer)color_coding.get( config.getProperty( name))).intValue();
		continue;
	    }
	    if( last_token.equals( "range")) {
		StringTokenizer values= 
		    new StringTokenizer( config.getProperty( name), " \t", false);
		// these 2 can throw NoSuchElementException
		String start= values.nextToken();
		String end= values.nextToken();
		// these 2 can throw NumberFormatException
		final short range_start=
		    (short)Math.round( Float.valueOf( start).floatValue() * 255); 
		final short range_end= 
		    (short)Math.round( Float.valueOf( end).floatValue() * 255); 
		if( range_start > range_end || range_start < 0 || range_end > 255)
		    throw new IOException( "invalid range: " + name);
		panel_struct.range_start= range_start;
		panel_struct.range_end= range_end;
		continue;
	    }
	    if( last_token.equals( "combine")) {
		// store it for the next pass...
		combined_panels.put( name, panel_struct);
		continue;
	    }
	    throw new IOException( "invalid: " + name);
	}
	/* ** third pass: parse for combined panels ** 
	 */
	for( prop_names= combined_panels.keys(); prop_names.hasMoreElements(); ) {
	    String panel_name= (String)prop_names.nextElement();
	    PanelStruct panel_struct= (PanelStruct)combined_panels.get( panel_name);
	    StringTokenizer tokens= 
		new StringTokenizer( config.getProperty( panel_name), " \t", false);
	    // these 2 can throw NoSuchElementException
	    String alias0= tokens.nextToken();
	    String alias1= tokens.nextToken();
	    if( _panelIndexOf( alias0) < 0 || _panelIndexOf( alias1) < 0)
		throw new IOException( panel_name + ": the 2 combined volumes " + 
				       "need to be also displayed individually!");
	    panel_struct.alias0= alias0;
	    panel_struct.alias1= alias1;
	}
	/* ** global settings ** 
	 */
	String tmp_string= config.getProperty( "jiv.sync");
	if( null != tmp_string) 
	    initial_position_sync= Boolean.valueOf( tmp_string).booleanValue();
	tmp_string= config.getProperty( "jiv.byte_values");
	if( null != tmp_string) 
	    byte_voxel_values= Boolean.valueOf( tmp_string).booleanValue();
	tmp_string= config.getProperty( "jiv.world_coords");
	if( null != tmp_string) 
	    enable_world_coords= Boolean.valueOf( tmp_string).booleanValue();
	tmp_string= config.getProperty( "jiv.download");
	if( null != tmp_string) 
	    download_method= 
		( (Integer)dnld_method_convert.get( tmp_string) ).intValue();
	if( DEBUG)
	    System.out.println( "* download method : " + download_method);

	if( DEBUG) {
	    System.out.println( "*** volumes:");
	    for( Enumeration keys= volumes.keys(); keys.hasMoreElements(); ) {
		String alias= (String)keys.nextElement();
		VolumeStruct vs= (VolumeStruct)volumes.get( alias);
		System.out.println( alias + ":" + vs.file);
	    }
	    System.out.println( "*** panels:");
	    for( int i= 0; i < panels.size(); ++i) {
		PanelStruct ps= (PanelStruct)panels.elementAt( i);
		if( null == ps) continue;
		System.out.println( i + ":" + ps + 
				    ":" + ps.alias0 + "," + ps.alias1 + "," +
				    ps.color_coding + "," + ps.range_start + "," +
				    ps.range_end);
	    }
	} 
    } // end of _parseConfig()
    
    /** returns the first _individual_ panel index displaying the argument 
	or -1 if none found
     */
    /*private*/ int _panelIndexOf( String volume_alias) {
	
	for( int i= 0; i < panels.size(); ++i) {
	    PanelStruct ps= (PanelStruct)panels.elementAt( i);
	    if( null == ps || null == ps.alias0)
		continue;
	    if( null == ps.alias1 && ps.alias0.equals( volume_alias))
		return i;
	}
	return -1;
    }

    /* ***** helper (member) classes ***** */

    /**
     * Helper data structure: represents an individual 3D image
     * volume.
     *
     * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
     * @version $Id: Main.java,v 1.19 2003/12/21 22:34:43 crisco Exp $
     */
    /*private*/ final class VolumeStruct {
	String 		file;
	Data3DVolume	data;
    }

    /**
     * Helper data structure: represents a GUI panel. There are 2
     * types of panels: individual and combined. Not all fields
     * are valid for all panel types: 
     * <dl>
     * <dt>individual: 
     * <dd>alias0 (alias1 should be null!), color_coding, range_* 
     * <dt>combined: 
     * <dd>alias0, alias1
     * </dl>
     *
     * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
     * @version $Id: Main.java,v 1.19 2003/12/21 22:34:43 crisco Exp $
     */
    /*private*/ final class PanelStruct {
	String		alias0;
	String		alias1;
	/** should be one of the static constants declared by ColorCoding */
	int		color_coding= ColorCoding.GREY;
	short		range_start= 0;
	short		range_end= 255;
	DataVolumePanel	gui;
    }

} // end of class Main


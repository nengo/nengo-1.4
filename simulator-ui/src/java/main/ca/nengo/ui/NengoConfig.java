package ca.nengo.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;


import java.io.File;
import java.util.logging.Logger;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;
import java.util.prefs.BackingStoreException;

import javax.swing.JFrame;

/**
 * NengoConfig holds all the configuration information for
 * Nengo. The implementation is a little confusing: it's an
 * enum because we only one one instance of NengoConfig
 * (enums with one element is essentially a singleton).
 * 
 * Configuration information is stored in the user's home directory,
 * under DIRNAME. Any persistent configuration should be done
 * by interacting with NengoConfig, NOT by writing your own methods
 * for creating and manipulating files in the installation directory;
 * we should never touch the installation directory!
 * 
 * @author tbekolay
 *
 */
public class NengoConfig {
	private static Preferences prefs = FilePreferences.userRoot();
	
	public static final String B_GRID = "show_grid";
	public static final boolean B_GRID_DEF = true;
	
	public static final String B_TOOLTIPS = "show_tooltips";
	public static final boolean B_TOOLTIPS_DEF = false;
	
	public static final String B_WELCOME = "show_welcome";
	public static final boolean B_WELCOME_DEF = true;
	
	public static final String F_PLOTTER_TAU = "plotter_tau";
	public static final float F_PLOTTER_TAU_DEF = 0.01f;
	
	public static final String I_MAXIMIZED = "window_maximized";
	public static final int I_MAXIMIZED_DEF = JFrame.MAXIMIZED_BOTH;
	
	public static final String I_PLOTTER_SUBSAMPLING = "plotter_subsampling";
	public static final int I_PLOTTER_SUBSAMPLING_DEF = 0;
	
	public static final String S_WORKING_DIRECTORY = "working_directory";
	public static final String S_WORKING_DIRECTORY_DEF = System.getProperty("user.home");
	
	public static final String S_SIMULATOR_SRC_PATH = "simulator_source";
	public static final String S_SIMULATOR_SRC_PATH_DEF = "../simulator/src/java/main";
	
	public static Preferences getPrefs() {
		System.out.println(prefs.toString());
		return prefs;
	}
}

/**
 * Preferences implementation that stores to a user-defined directory.
 * See NengoPreferencesFactory.
 *
 * @author Trevor Bekolay, based on implementation by David Croft
 */
class FilePreferences extends AbstractPreferences {
//	private static final Logger log = Logger.getLogger(FilePreferences.class.getName());
 
	private Map<String, String> root;
	private Map<String, FilePreferences> children;
	private boolean isRemoved = false;
 
	public FilePreferences(AbstractPreferences parent, String name) {
		super(parent, name);
 
//		log.finest("Instantiating node " + name);
 
		root = new TreeMap<String, String>();
		children = new TreeMap<String, FilePreferences>();
 
		try {
			sync();
		}
		catch (BackingStoreException e) {
//			log.log(Level.SEVERE, "Unable to sync on creation of node " + name, e);
		}
	}
 
	protected void putSpi(String key, String value) {
		root.put(key, value);
		try {
			flush();
		} catch (BackingStoreException e) {
//			log.log(Level.SEVERE, "Unable to flush after putting " + key, e);
		}
	}
 
	protected String getSpi(String key) {
		return root.get(key);
	}
 
	protected void removeSpi(String key) {
		root.remove(key);
		try {
			flush();
		} catch (BackingStoreException e) {
//			log.log(Level.SEVERE, "Unable to flush after removing " + key, e);
		}
	}
 
	protected void removeNodeSpi() throws BackingStoreException {
		isRemoved = true;
		flush();
	}
 
	protected String[] keysSpi() throws BackingStoreException {
		return root.keySet().toArray(new String[root.keySet().size()]);
	}
 
	protected String[] childrenNamesSpi() throws BackingStoreException {
		return children.keySet().toArray(new String[children.keySet().size()]);
	}
 
	protected FilePreferences childSpi(String name) {
		FilePreferences child = children.get(name);
		if (child == null || child.isRemoved()) {
			child = new FilePreferences(this, name);
			children.put(name, child);
		}
		return child;
	}
 
	protected void syncSpi() throws BackingStoreException {
		if (isRemoved()) return;
 
		final File file = NengoPreferencesFactory.getPreferencesFile();
 
		if (!file.exists()) return;
 
		synchronized (file) {
			Properties p = new Properties();
			try {
				p.load(new FileInputStream(file));
 
				StringBuilder sb = new StringBuilder();
				getPath(sb);
				String path = sb.toString();
 
				final Enumeration<?> pnen = p.propertyNames();
				while (pnen.hasMoreElements()) {
					String propKey = (String) pnen.nextElement();
					if (propKey.startsWith(path)) {
						String subKey = propKey.substring(path.length());
						// Only load immediate descendants
						if (subKey.indexOf('.') == -1) {
							root.put(subKey, p.getProperty(propKey));
						}
					}
				}
			} catch (IOException e) {
				throw new BackingStoreException(e);
			}
		}
	}
 
	private void getPath(StringBuilder sb) {
		final FilePreferences parent = (FilePreferences) parent();
		if (parent == null) return;
 
		parent.getPath(sb);
		sb.append(name()).append('.');
	}
 
	protected void flushSpi() throws BackingStoreException {
		final File file = NengoPreferencesFactory.getPreferencesFile();
 
		synchronized (file) {
			Properties p = new Properties();
			try {
				StringBuilder sb = new StringBuilder();
				getPath(sb);
				String path = sb.toString();
 
				if (file.exists()) {
					p.load(new FileInputStream(file));
 
					List<String> toRemove = new ArrayList<String>();
          
					// Make a list of all direct children of this node to be removed
	     			final Enumeration<?> pnen = p.propertyNames();
	     			while (pnen.hasMoreElements()) {
	     				String propKey = (String) pnen.nextElement();
	     				if (propKey.startsWith(path)) {
	     					String subKey = propKey.substring(path.length());
	     					// Only do immediate descendants
	     					if (subKey.indexOf('.') == -1) {
	     						toRemove.add(propKey);
	     					}
	     				}
	     			}
	 
	     			// Remove them now that the enumeration is done with
	     			for (String propKey : toRemove) {
	     				p.remove(propKey);
	     			}
	     		}
 
	     		// If this node hasn't been removed, add back in any values
	     		if (!isRemoved) {
	     			for (String s : root.keySet()) {
	     				p.setProperty(path + s, root.get(s));
	     			}
	     		}
	 
	     		p.store(new FileOutputStream(file),
	     				"## Nengo Preferences ###\nLast updated");
	    	} catch (IOException e) {
	    		throw new BackingStoreException(e);
	    	}
		}
	}
}
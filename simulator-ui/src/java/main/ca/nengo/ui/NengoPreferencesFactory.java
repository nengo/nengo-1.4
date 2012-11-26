package ca.nengo.ui;

import java.io.File;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/**
 * PreferencesFactory implementation that stores the preferences in a
 * user-defined file. To use it, set the system property
 * <tt>java.util.prefs.PreferencesFactory</tt> to
 * <tt>ca.nengo.ui.NengoPreferencesFactory</tt>
 *
 * @author Trevor Bekolay, based on implementation by David Croft
 */
public class NengoPreferencesFactory implements PreferencesFactory {
//	private static final Logger log = Logger.getLogger(NengoPreferencesFactory.class.getName());
 
	Preferences rootPreferences;
	private static final String SAVEDIRNAME = ".nengo";
	private static final String SAVEFILENAME = "nengo-ui.properties";
  
	public Preferences systemRoot() {
		return userRoot();
	}
 
	public Preferences userRoot() {
		if (rootPreferences == null) {
//			log.finer("Instantiating root preferences");
 
			rootPreferences = new FilePreferences(null, "");
		}
		return rootPreferences;
	}
 
	private static File preferencesFile;
 
	public static File getPreferencesFile() {
		if (preferencesFile == null) {
			String homedir = System.getProperty("user.home");
			File savedir = new File(homedir + File.separator + SAVEDIRNAME);
			if (!savedir.exists()) {
				savedir.mkdir();
			}
			preferencesFile = new File(savedir, SAVEFILENAME).getAbsoluteFile();
//      	log.finer("Preferences file is " + preferencesFile);
		}
		return preferencesFile;
	}
}
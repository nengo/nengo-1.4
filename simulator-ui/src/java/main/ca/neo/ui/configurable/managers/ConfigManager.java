package ca.neo.ui.configurable.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;

import ca.neo.ui.NeoGraphics;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.IConfigurable;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.Util;

/**
 * Configuration Manager used to configure IConfigurable objects
 * 
 * @author Shu Wu
 * 
 */
public abstract class ConfigManager {
	/**
	 * Name of directory where to store saved configuration
	 */
	static final String SAVED_CONFIG_DIR = NeoGraphics.USER_FILE_DIR
			+ "/Config";

	/**
	 * Creates a saved objects folder if it isn't already there
	 * 
	 * @return The Saved Objects folder
	 */
	private static File getSavedObjectsFolder() {
		File file = new File(SAVED_CONFIG_DIR);
		if (!file.exists())
			file.mkdir();
		return file;
	}

	/**
	 * @returns the file name prefix given per class
	 */
	@SuppressWarnings("unchecked")
	protected static String getFileNamePrefix(IConfigurable obj) {

		return obj.getTypeName() + "_Props_";

	}

	/**
	 * Object to be configured
	 */
	private final IConfigurable configurable;

	/**
	 * Set of attributes that will be set during configuration
	 */
	private MutableAttributeSet properties;

	/**
	 * @param configurable
	 *            Object to be configured
	 */
	public ConfigManager(IConfigurable configurable) {
		super();
		properties = new SimpleAttributeSet();
		this.configurable = configurable;

	}

	/**
	 * Configures the IConfigurable object and waits until the configuration
	 * finishes
	 */
	protected abstract void configureAndWait() throws ConfigException;

	/**
	 * @param name
	 *            filename prefix
	 * 
	 */
	protected void deletePropertiesFile(String name) {
		File file = new File(getSavedObjectsFolder(),
				getFileNamePrefix(configurable) + name);

		System.gc();
		if (file.exists()) {
			boolean val = file.delete();
			if (val == false) {
				UserMessages.showError("Could not delete file");
			}

		}
	}

	/**
	 * @return Object to be configured
	 */
	protected IConfigurable getConfigurable() {
		return configurable;
	}

	/**
	 * @return Set of properties to be set during the configuration process
	 */
	protected MutableAttributeSet getProperties() {
		return properties;
	}

	/**
	 * @param name
	 *            Name of property
	 * @return Value of property
	 */
	protected Object getProperty(String name) {
		return getProperties().getAttribute(name);
	}

	/**
	 * @return List of fileNames which point to saved configuration files
	 */
	protected String[] getPropertyFiles() {
		File file = getSavedObjectsFolder();
		/*
		 * Gets a list of property files
		 */
		String[] files = file.list(new ConfigFilesFilter(configurable));
		/*
		 * Return the file names without the prefix
		 */
		String[] files0 = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			files0[i] = files[i].replaceFirst(getFileNamePrefix(configurable),
					"");
		}
		return files0;

	}

	/**
	 * @param name
	 *            Name of the properties set to be loaded
	 * 
	 */
	protected void loadPropertiesFromFile(String name) {

		FileInputStream f_in;

		try {
			f_in = new FileInputStream(SAVED_CONFIG_DIR + "/"
					+ getFileNamePrefix(configurable) + name);

			ObjectInputStream obj_in = new ObjectInputStream(f_in);

			Object obj;

			obj = obj_in.readObject();

			if (obj == null) {
				UserMessages.showError("Could not load file: " + name);
			} else {

				this.properties = (MutableAttributeSet) obj;
			}
			obj_in.close();

		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found exception");
		} catch (InvalidClassException e) {
			System.out.println("Invalid class exception");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param name
	 *            name of the properties set to be saved
	 * 
	 */
	protected void savePropertiesFile(String name) {

		// Write to disk with FileOutputStream
		FileOutputStream f_out;
		try {
			File objectsFolder = getSavedObjectsFolder();
			File file = new File(objectsFolder, getFileNamePrefix(configurable)
					+ name);

			if (file.exists()) {
				Util.debugMsg("Replaced existing file: " + file.getName());
			}
			f_out = new FileOutputStream(file);

			ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
			obj_out.writeObject(properties);
			obj_out.close();

			f_out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected void setProperty(String name, Object value) {
		getProperties().addAttribute(name, value);
	}

}

/**
 * Filters files needed by ConfigManager
 * 
 * @author Shu
 * 
 */
class ConfigFilesFilter implements FilenameFilter {
	IConfigurable parent;

	public ConfigFilesFilter(IConfigurable parent) {
		super();
		this.parent = parent;
	}

	public boolean accept(File file, String name) {

		if (name.startsWith(ConfigManager.getFileNamePrefix(parent))) {
			return true;
		} else
			return false;

	}
}

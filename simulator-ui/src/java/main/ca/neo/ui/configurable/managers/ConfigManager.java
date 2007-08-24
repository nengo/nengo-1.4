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
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.IConfigurable;
import ca.shu.ui.lib.util.Util;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PObjectOutputStream;

public abstract class ConfigManager {
	static final String FILE_SAVED_OBJECTS_DIR = NeoGraphics.USER_FILE_DIR;

	/**
	 * Creates a saved objects folder if it isn't already there
	 * 
	 * @return The Saved Objects folder
	 */
	public static File getSavedObjectsFolder() {
		File file = new File(FILE_SAVED_OBJECTS_DIR);
		if (!file.exists())
			file.mkdir();
		return file;
	}

	/**
	 * @returns the file name prefix given per class
	 */
	@SuppressWarnings("unchecked")
	protected static String getFileNamePrefix(IConfigurable obj) {
		// if (obj instanceof Class) {
		// return ((Class) obj).getName() + "_";
		// } else {
		return obj.getTypeName() + "_Props_";
		// }

	}

	private final IConfigurable configurable;

	MutableAttributeSet properties;

	public ConfigManager(IConfigurable configurable) {
		super();
		properties = new SimpleAttributeSet();
		this.configurable = configurable;

	}

	/**
	 * @param name
	 *            filename prefix
	 * 
	 */
	public void deletePropertiesFile(String name) {
		File file = new File(getSavedObjectsFolder(),
				getFileNamePrefix(configurable) + name);

		System.gc();
		if (file.exists()) {
			boolean val = file.delete();
			if (val == false) {
				Util.UserError("Could not delete file");
			}

		}
	}

	public IConfigurable getConfigurable() {
		return configurable;
	}

	// public void savePropertiesToFile(String fileName) {
	// saveProperty(configurable, configurable.getPropertiesReference(),
	// fileName);
	//
	// // saveStatic(properties, fileName);
	// }

	public MutableAttributeSet getProperties() {
		return properties;
	}

	// public static void deletePropretiesFile(IConfigurable configurable,
	// String fileName) {
	// Util.deleteProperty(configurable, fileName);
	// }

	public Object getProperty(ConfigParamDescriptor prop) {
		return getProperty(prop.getName());
	}

	public Object getProperty(String name) {
		return getProperties().getAttribute(name);
	}

	/**
	 * 
	 * @param configurable
	 *            The Object to be configured
	 * @return the list of files for the parent
	 */
	public String[] getPropertyFiles() {
		File file = getSavedObjectsFolder();
		/*
		 * Gets a list of property files
		 */
		String[] files = file.list(new CustomFileNameFilter(configurable));
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
	 * @param configurable
	 *            The object holding the property
	 * @param name
	 *            Name of the property to be loaded
	 * 
	 */
	public void loadPropertiesFromFile(String name) {

		FileInputStream f_in;

		try {
			f_in = new FileInputStream(FILE_SAVED_OBJECTS_DIR + "/"
					+ getFileNamePrefix(configurable) + name);

			ObjectInputStream obj_in = new ObjectInputStream(f_in);

			Object obj;

			obj = obj_in.readObject();

			if (obj == null) {
				Util.UserError("Could not load file: " + name);
			} else {

				this.properties = (MutableAttributeSet) obj;
			}
			obj_in.close();

		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Class not found exception");
		} catch (InvalidClassException e) {
			System.out.println("Invalid class exception");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param name
	 *            name prefix given to the properties file
	 * 
	 */
	public void savePropertiesFile(String name) {

		// Write to disk with FileOutputStream
		FileOutputStream f_out;
		try {
			File objectsFolder = getSavedObjectsFolder();
			File file = new File(objectsFolder, getFileNamePrefix(configurable)
					+ name);

			// String fileName = FILE_SAVED_OBJECTS_DIR + "/"
			// + getFileNamePrefix(configurable) + name;
			// String fileName = "SavedObjects/" + name;

			if (file.exists()) {
				System.out.println("Replaced existing file: " + file.getName());
			}
			f_out = new FileOutputStream(file);

			if (properties instanceof PNode) {
				PObjectOutputStream obj_out = new PObjectOutputStream(f_out);
				obj_out.writeObjectTree(properties);
				obj_out.close();
			} else {
				ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
				obj_out.writeObject(properties);
				obj_out.close();
			}

			f_out.close();
			// System.out.println("Saved: " + fileName);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setProperty(String name, Object value) {
		getProperties().addAttribute(name, value);
	}

	/**
	 * Configures the IConfigurable object
	 */
	protected abstract void configureAndWait() throws ConfigException;

}

/**
 * A FilenameFilter that can be used statically to filter files related to a
 * object
 * 
 * @author Shu
 * 
 */
class CustomFileNameFilter implements FilenameFilter {
	IConfigurable parent;

	public CustomFileNameFilter(IConfigurable parent) {
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

package ca.shu.ui.lib.util;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ListIterator;

import javax.swing.JOptionPane;

import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.impl.GFrame;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PObjectOutputStream;
import edu.umd.cs.piccolo.util.PStack;

public class Util {
	static final String ZEROES = "000000000000";

	static final String BLANKS = "            ";

	static public Color colorTimes(Color c1, double f) {
		int r = (int) Math.min(c1.getRed() * f, 255);
		int g = (int) Math.min(c1.getGreen() * f, 255);
		int b = (int) Math.min(c1.getBlue() * f, 255);
		return new Color(r, g, b);
	}

	static public Color colorAdd(Color c1, Color c2) {
		int r = Math.min(c1.getRed() + c2.getRed(), 255);
		int g = Math.min(c1.getGreen() + c2.getGreen(), 255);
		int b = Math.min(c1.getBlue() + c2.getBlue(), 255);
		return new Color(r, g, b);
	}

	public static void Error(String msg) {
		Assert(false, msg);

	}

	public static void Warning(String msg) {
		showMsg("Warning: " + msg);
	}

	public static void showMsg(String msg) {
		JOptionPane.showMessageDialog(GraphicsEnvironment.getInstance(), msg);
	}

	public static void Assert(boolean bool, String msg) {
		if (!bool) {
			showMsg("Error: " + msg);
			(new Exception(msg)).printStackTrace();
		}
	}

	public static String format(double val, int n, int w) {
		// rounding
		double incr = 0.5;
		for (int j = n; j > 0; j--)
			incr /= 10;
		val += incr;

		String s = Double.toString(val);
		int n1 = s.indexOf('.');
		int n2 = s.length() - n1 - 1;

		if (n > n2)
			s = s + ZEROES.substring(0, n - n2);
		else if (n2 > n)
			s = s.substring(0, n1 + n + 1);

		if (w > 0 & w > s.length())
			s = BLANKS.substring(0, w - s.length()) + s;
		else if (w < 0 & (-w) > s.length()) {
			w = -w;
			s = s + BLANKS.substring(0, w - s.length());
		}
		return s;
	}

	public static void openURL(String url) {

		String[] cmd = new String[4];
		cmd[0] = "cmd.exe";
		cmd[1] = "/C";
		cmd[2] = "start";
		cmd[3] = url;

		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return The first node on the pick path that matches the parameter type
	 * 
	 * @param event
	 *            Event sent from Piccolo
	 * @param type
	 *            The type of node to be picked from the pick tree
	 */
	public static Object getNodeFromPickPath(PInputEvent event, Class type) {
		PStack nodeStack = event.getPath().getNodeStackReference();
		ListIterator it = nodeStack.listIterator(nodeStack.size());

		while (it.hasPrevious()) {
			Object node = it.previous();

			if (type.isInstance(node)) {
				return node;
			}

			/*
			 * Stop picking objects at the boundary of the worlds
			 */
			if (node instanceof World) {
				return null;
			}

		}
		return null;
	}

	/**
	 * @param parent
	 *            The object holding the property
	 * @param name
	 *            Name of the property to be loaded
	 * 
	 */
	public static Object loadProperty(Object parent, String name) {

		FileInputStream f_in;

		try {
			f_in = new FileInputStream(FILE_SAVED_OBJECTS_DIR + "/"
					+ getFileNamePrefix(parent) + name);

			ObjectInputStream obj_in = new ObjectInputStream(f_in);

			Object obj;

			obj = obj_in.readObject();

			return obj;
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
		return null;
	}

	static Object parentStatic;

	/**
	 * 
	 * @param parent
	 *            of the files
	 * @return the list of files for the parent
	 */
	public static String[] getPropertyFiles(Object parent) {
		File file = getSavedObjectsFolder();
		/*
		 * Gets a list of property files
		 */
		String[] files = file.list(new CustomFileNameFilter(parent));
		/*
		 * Return the file names without the prefix
		 */
		String[] files0 = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			files0[i] = files[i].replaceFirst(getFileNamePrefix(parent), "");
		}
		return files0;

	}

	static final String FILE_SAVED_OBJECTS_DIR = "SavedObjects";

	/**
	 * @returns the file name prefix given per class
	 */
	@SuppressWarnings("unchecked")
	protected static String getFileNamePrefix(Object obj) {
		if (obj instanceof Class) {
			return ((Class) obj).getName() + "_";
		} else {
			return obj.getClass().getName() + "_";
		}

	}

	/**
	 * @param parent
	 *            The Parent class holding the saved object
	 * @param name
	 *            Name of the saved object to be removed
	 * 
	 */
	public static void deleteProperty(Object parent, String name) {
		getSavedObjectsFolder();
		String fileName = FILE_SAVED_OBJECTS_DIR + "/"
				+ getFileNamePrefix(parent) + name;

		File file = new File(fileName);

		System.gc();
		if (file.exists()) {
			boolean val = file.delete();
			if (val == false) {
				Util.Error("Could not delete file");
			}

		}
	}

	/**
	 * Creates a saved objects folder if it isn't already there
	 * 
	 * @return The Saved Objects folter
	 */
	public static File getSavedObjectsFolder() {
		File file = new File(FILE_SAVED_OBJECTS_DIR);
		if (!file.exists())
			file.mkdir();
		return file;
	}

	/**
	 * @param parent
	 *            The Parent object holding the property
	 * @param property
	 *            Property to be saved
	 * @param name
	 *            Filename given to instance of this property
	 * 
	 */
	public static void saveProperty(Object parent, Object property, String name) {

		// Write to disk with FileOutputStream
		FileOutputStream f_out;
		try {
			getSavedObjectsFolder();

			String fileName = FILE_SAVED_OBJECTS_DIR + "/"
					+ getFileNamePrefix(parent) + name;
			// String fileName = "SavedObjects/" + name;

			if ((new File(fileName)).exists()) {
				System.out.println("Replaced existing file: " + fileName);
			}
			f_out = new FileOutputStream(fileName);

			if (property instanceof PNode) {
				PObjectOutputStream obj_out = new PObjectOutputStream(f_out);
				obj_out.writeObjectTree(property);
			} else {
				ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
				obj_out.writeObject(property);
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
}

/**
 * A FilenameFilter that can be used statically to filter files related to a
 * object
 * 
 * @author Shu
 * 
 */
class CustomFileNameFilter implements FilenameFilter {
	Object parent;

	public CustomFileNameFilter(Object parent) {
		super();
		this.parent = parent;
	}

	public boolean accept(File file, String name) {

		if (name.startsWith(Util.getFileNamePrefix(parent))) {
			return true;
		} else
			return false;

	}
}

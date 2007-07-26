package ca.shu.ui.lib.util;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ListIterator;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.handlers.IContextMenu;
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
		JOptionPane.showMessageDialog(null, msg);
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

	/*
	 * Returns the first node on the pick path that matches the type
	 */
	public static Object getNodeFromPickPath(PInputEvent event, Class type) {
		PStack nodeStack = event.getPath().getNodeStackReference();
		ListIterator it = nodeStack.listIterator(nodeStack.size());

		while (it.hasPrevious()) {
			Object node = it.previous();

			if (type.isInstance(node)) {
				return node;
			}
		}
		return null;
	}

	public static Object loadObject(String name) {

		FileInputStream f_in;

		try {
			f_in = new FileInputStream("SavedObjects/" + name);

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

	public static void saveObject(Object obj, String name) {
		// Write to disk with FileOutputStream
		FileOutputStream f_out;
		try {
			File file = new File("SavedObjects");
			if (!file.exists())
				file.mkdir();

			String fileName = "SavedObjects/" + name;

			if ((new File(fileName)).exists()) {
				System.out.println("Replaced existing file: " + name);
			}
			f_out = new FileOutputStream(fileName);

			if (obj instanceof PNode) {
				PObjectOutputStream obj_out = new PObjectOutputStream(f_out);
				obj_out.writeObjectTree(obj);
			} else {
				ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
				obj_out.writeObject(obj);
			}

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

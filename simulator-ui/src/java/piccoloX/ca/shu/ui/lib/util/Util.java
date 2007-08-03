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

	static final boolean DEBUG_ENABLED = true;

	public static void debugMsg(String msg) {
		if (DEBUG_ENABLED) {
			System.out.println("DebugMSG: " + msg);
		}

	}

	public static void Warning(String msg) {
		JOptionPane.showMessageDialog(UIEnvironment.getInstance(), msg,
				"Warning", JOptionPane.WARNING_MESSAGE);
	}

	public static void Assert(boolean bool, String msg) {
		if (!bool) {
			JOptionPane.showMessageDialog(UIEnvironment.getInstance(), msg,
					"Error", JOptionPane.ERROR_MESSAGE);
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

}

package ca.nengo.ui;

import java.util.prefs.Preferences;

import javax.swing.JFrame;

public class NengoConfig {
	private static Preferences prefs = Preferences.userNodeForPackage(NengoGraphics.class);
	
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

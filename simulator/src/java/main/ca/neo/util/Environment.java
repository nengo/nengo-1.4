/*
 * Created on 19-Jul-07
 */
package ca.neo.util;

/**
 * Provides information about the context in which the code is running. 
 * 
 * @author Bryan Tripp
 */
public abstract class Environment {

	/**
	 * Name of system property underlying inUserInterface()
	 */
	public static String USER_INTERFACE = "user-interface";

	/**
	 * Name of String property that contains path of user's working directory  
	 */
	public static String WORKING_DIRECTORY = "working-directory";	
	
	
	/**
	 * @return True if the system is running within a user interface (default is false; 
	 * 		can be configured with system property "user-interface" = "true")  
	 */
	public static boolean inUserInterface() {
		String ui = System.getProperty(USER_INTERFACE); 
		return (ui != null && ui.equalsIgnoreCase("true"));
	}
	
	public static void setUserInterface(boolean inUI) {
		System.setProperty(USER_INTERFACE, inUI ? "true" : "false");
	}

}

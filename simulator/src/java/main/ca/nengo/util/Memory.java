/*
 * Created on 27-Jul-2006
 */
package ca.nengo.util;

import org.apache.log4j.Logger;

/**
 * Internal tools for checking memory usage. 
 *  
 * @author Bryan Tripp
 */
public class Memory {
	
	private static Logger ourLogger = Logger.getLogger(Memory.class);

	/**
	 * Prints a message to the console regarding current memory usage. 
	 */
	public static void report(String context) {
		System.gc();
		long free = Runtime.getRuntime().freeMemory();
		long total = Runtime.getRuntime().totalMemory();
		long max = Runtime.getRuntime().maxMemory();
		
		ourLogger.info("Used: " + (total-free) + " Total: " + total + " Max: " + max + " (" + context + ")");
	}
}

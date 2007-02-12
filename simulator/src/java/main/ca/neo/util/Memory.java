/*
 * Created on 27-Jul-2006
 */
package ca.neo.util;

/**
 * Internal tools for checking memory usage. 
 *  
 * @author Bryan Tripp
 */
public class Memory {

	/**
	 * Prints a message to the console regarding current memory usage. 
	 */
	public static void report(String context) {
		System.gc();
		long free = Runtime.getRuntime().freeMemory();
		long total = Runtime.getRuntime().totalMemory();
		long max = Runtime.getRuntime().maxMemory();
		
		System.out.println("Used: " + (total-free) + " Total: " + total + " Max: " + max + " (" + context + ")");
	}
}

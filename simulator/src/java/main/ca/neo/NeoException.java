/*
 * Created on May 23, 2006
 */
package ca.neo;

/**
 * A Computational Neuroscience exception. This type is the parent of all exceptions 
 * defined in this simulator software. 
 *  
 * @author Bryan Tripp
 */
public class NeoException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message Text explanation of the exception. 
	 */
	public NeoException(String message) {
		super(message);
	}

	/**
	 * @param cause Another throwable that indicates a problem underlying this 
	 * 		exception.  
	 */
	public NeoException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message Text explanation of the exception. 
	 * @param cause Another throwable that indicates a problem underlying this 
	 * 		exception.  
	 */
	public NeoException(String message, Throwable cause) {
		super(message, cause);
	}

}

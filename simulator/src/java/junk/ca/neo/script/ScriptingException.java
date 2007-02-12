/*
 * Created on 12-Jun-2006
 */
package ca.neo.script;

/**
 * A non-checked exception that represents a problem arising during 
 * script execution. Methods defined in the script package are allowed 
 * to throw this and no other type of exception.  
 *  
 * @author Bryan Tripp
 */
public class ScriptingException extends RuntimeException {

	private static final long serialVersionUID = 1L;	

	/**
	 * @param message Text explanation of the exception. 
	 */
	public ScriptingException(String message) {
		super(message);
	}

	/**
	 * @param message Text explanation of the exception. 
	 * @param cause Optional exception/error that indicates a problem underlying this 
	 * 		exception.  
	 */
	public ScriptingException(String message, Throwable cause) {
		super(message, cause);
	}

}

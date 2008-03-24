/*
 * Created on May 23, 2006
 */
package ca.nengo.model;

import ca.nengo.NengoException;

/**
 * A problem encountered while trying to run a simulation.
 *    
 * @author Bryan Tripp
 */
public class SimulationException extends NengoException {

	private static final long serialVersionUID = 1L;
	
	private String myEnsembleName; 

	/**
	 * @param message Text explanation of the exception. 
	 */
	public SimulationException(String message) {
		super(message);
	}

	/**
	 * @param cause Another throwable that indicates a problem underlying this 
	 * 		exception.  
	 */
	public SimulationException(Throwable cause) {
		super(cause); 
	}

	/**
	 * @param message Text explanation of the exception. 
	 * @param cause Another throwable that indicates a problem underlying this 
	 * 		exception.  
	 */
	public SimulationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Adds ensemble name to message. 
	 */
	public String getMessage() {
		String message = super.getMessage();
		
		if (myEnsembleName != null) {
			message = message + " (ensemble: " + myEnsembleName + ")";
		}
		
		return message;
	}
	
	/**
	 * @param name Name of the ensemble in which the exception occured
	 */
	public void setEnsemble(String name) {
		myEnsembleName = name;
	}

}

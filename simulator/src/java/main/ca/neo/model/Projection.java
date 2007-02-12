/*
 * Created on May 5, 2006
 */
package ca.neo.model;

import java.io.Serializable;

/**
 * A connection between an Origin and a Termination. 
 *    
 * @author Bryan Tripp
 */
public interface Projection extends Serializable {

	/**
	 * @return Origin of this Projection (where information comes from)
	 */
	public Origin getOrigin();
	
	/**
	 * @return Termination of this Projection (where information goes)
	 */
	public Termination getTermination();

}

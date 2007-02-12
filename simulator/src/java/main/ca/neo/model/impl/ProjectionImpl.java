/*
 * Created on May 5, 2006
 */
package ca.neo.model.impl;

import ca.neo.model.Origin;
import ca.neo.model.Projection;
import ca.neo.model.Termination;

/**
 * Default implementation of <code>Projection</code>. 
 * 
 * @author Bryan Tripp
 */
public class ProjectionImpl implements Projection {

	private static final long serialVersionUID = 1L;
	
	private Origin myOrigin;
	private Termination myTermination;
	
	/**
	 * @param origin  @see ca.bpt.cn.model.Projection#getOrigin()
	 * @param termination  @see ca.bpt.cn.model.Projection#getTermination()
	 */
	public ProjectionImpl(Origin origin, Termination termination) {
		myOrigin = origin;
		myTermination = termination;
	}

	/**
	 * @see ca.neo.model.Projection#getOrigin()
	 */
	public Origin getOrigin() {
		return myOrigin;
	}

	/**
	 * @see ca.neo.model.Projection#getTermination()
	 */
	public Termination getTermination() {
		return myTermination;
	}

}

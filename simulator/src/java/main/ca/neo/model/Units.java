/*
 * Created on May 4, 2006
 */
package ca.neo.model;

import org.apache.commons.lang.enums.Enum;

/**
 * Models units for physical quantities. We use this class to model both fundamental units 
 * (e.g. 's') and composite units (e.g. 'spikes/s').
 *    
 * @author  Bryan Tripp
 */
public class Units extends Enum {

	private static final long serialVersionUID = 1L;
	
	public static final Units S = new Units("s"); //seconds
	
	//Neural 
	public static final Units mV = new Units("mV"); //millivolts
	public static final Units uA = new Units("uA"); //micro-amps
	public static final Units ACU = new Units("ACU"); //arbitrary current units 
	public static final Units AVU = new Units("AVU"); //arbitrary voltage units
	public static final Units UNK = new Units("UNK"); //unknown units
	public static final Units SPIKES = new Units("spikes"); //spikes (count)
	public static final Units SPIKES_PER_S = new Units("spikes/s"); //spike rate

	//Mechanical
	public static final Units N = new Units("N"); //Newtons
	public static final Units Nm = new Units("Nm"); //Newton metres
	public static final Units M = new Units("m"); //metres 
	public static final Units RAD = new Units("rad"); //radians 
	public static final Units M_PER_S = new Units("m/s"); //metres per second
	public static final Units RAD_PER_S = new Units("rad/s"); //radians per second
	
	/**
	 * @param name Standard name of units
	 */
	private Units(String name) {
		super(name);
	}
	
	/**
	 * Returns an array of Units in which all Units are the same. 
	 * 
	 * @param units Units to be returned in every element 
	 * @param length Number of elements
	 * @return Array of given length with given Units in every element
	 */
	public static Units[] uniform(Units units, int length) {
		Units[] result = new Units[length];
		
		for (int i = 0; i < length; i++) {
			result[i] = units;
		}
		
		return result;
	}

}

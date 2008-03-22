/*
 * Created on May 4, 2006
 */
package ca.neo.model;

/**
 * Models units for physical quantities. We use this class to model both fundamental units 
 * (e.g. 's') and composite units (e.g. 'spikes/s').
 *    
 * @author  Bryan Tripp
 */
public enum Units {

	S("s"), //seconds
	
	//Neural 
	mV("mV"), //millivolts
	uA("uA"), //micro-amps
	uAcm2("uA/cm^2"), //micro-amps per cm^2
	ACU("ACU"), //arbitrary current units 
	AVU("AVU"), //arbitrary voltage units
	UNK("UNK"), //unknown units
	SPIKES("spikes"), //spikes (count)
	SPIKES_PER_S("spikes/s"), //spike rate

	//Mechanical
	N("N"), //Newtons
	Nm("Nm"), //Newton metres
	M("m"), //metres 
	RAD("rad"), //radians 
	M_PER_S("m/s"), //metres per second
	RAD_PER_S("rad/s"); //radians per second
	
	public String myName;
	
	/**
	 * @param name Standard name of units
	 */
	private Units(String name) {
		myName = name;
	}
	
	
	@Override
	public String toString() {
		return myName;
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

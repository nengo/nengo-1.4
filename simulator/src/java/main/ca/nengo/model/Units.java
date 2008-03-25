/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "Units.java". Description: 
""

The Initial Developer of the Original Code is Bryan Tripp & Centre for Theoretical Neuroscience, University of Waterloo. Copyright (C) 2006-2008. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of the GNU 
Public License license (the GPL License), in which case the provisions of GPL 
License are applicable  instead of those above. If you wish to allow use of your 
version of this file only under the terms of the GPL License and not to allow 
others to use your version of this file under the MPL, indicate your decision 
by deleting the provisions above and replace  them with the notice and other 
provisions required by the GPL License.  If you do not delete the provisions above,
a recipient may use your version of this file under either the MPL or the GPL License.
*/

/*
 * Created on May 4, 2006
 */
package ca.nengo.model;

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

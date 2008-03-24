/*
 * Created on 2-Jun-2006
 */
package ca.nengo.util.impl;

import ca.nengo.util.IndexFinder;

/**
 * An IndexFinder that searches linearly, starting where the last answer was. This is 
 * a good choice if many interpolations will be made on the same series, and adjacent 
 * requests will be close to each other.  
 * 
 * TODO: test
 * 
 * @author Bryan Tripp
 */
public class StatefulIndexFinder implements IndexFinder {

	private float[] myValues;
	private int myIndex;
	
	/**
	 * @param values Must be monotonically increasing. 
	 */
	public StatefulIndexFinder(float[] values) {
		assert areMonotonicallyIncreasing(values);
		
		myValues = values;
		myIndex = 0;
	}

	public int findIndexBelow(float value) {
		
		if (myValues[myIndex] <= value) { //forward
			while (myIndex < (myValues.length-1) && myValues[++myIndex] <= value);
			myIndex--;
		} else {  //backward
			while (myIndex > 0 && myValues[--myIndex] > value);
		}
		
		return myIndex;
	}
	
	/**
	 * @param values A list of values  
	 * @return True if list values increases monotonically, false otherwise  
	 */
	public static boolean areMonotonicallyIncreasing(float[] values) {
		boolean result = true;

		for (int i = 1; i < values.length && result == true; i++) {
			if (values[i] < values[i-1]) {
				result = false;
			}
		}
		
		return result;
	}

	@Override
	public StatefulIndexFinder clone() throws CloneNotSupportedException {
		StatefulIndexFinder result = new StatefulIndexFinder(myValues.clone());
		result.myIndex = myIndex;
		return result;
	}

}

/*
 * Created on 2-Jun-2006
 */
package ca.neo.util;

/**
 * Searches a monotonically increasing list of floating-point values for the 
 * largest one that is less than or equal to a requested value. The list of values 
 * is typically set at construction time. 
 * 
 * @author Bryan Tripp
 */
public interface IndexFinder extends Cloneable {

	/**
	 * @param value A floating-point value that the list is expected to span 
	 * @return The index of the largest value in the list which is smaller than 
	 * 		the 'value' arg
	 */
	public int findIndexBelow(float value);
	
	public IndexFinder clone() throws CloneNotSupportedException;
		
}

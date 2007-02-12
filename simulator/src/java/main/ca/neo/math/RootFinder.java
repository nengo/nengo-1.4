/*
 * Created on 20-Jul-2006
 */
package ca.neo.math;

/**
 * Finds a root of a function. 
 * 
 * @author Bryan Tripp
 */
public interface RootFinder {

	/**
	 * @param function Function f(x) to find root of
	 * @param startLow Low-valued x from which to start search 
	 * @param startHigh High-valued x from which to start. You typically give startLow and startHigh so that
	 * 		you expect the signs of the functions at these values to be different.  
	 * @param tolerance Max acceptable |f(x)| for which to return x  
	 * @return x for which |f(x)| <= tolerance
	 */
	public float findRoot(Function function, float startLow, float startHigh, float tolerance);
	
//	/**
//	 * A function for which f(x)=0 for some x. 
//	 *  
//	 * @author Bryan Tripp
//	 */
//	public static interface Function {
//
//		/**
//		 * @param value Value at which to evaluate function
//		 * @return f(value)
//		 */
//		public float map(float value);		
//	}
}

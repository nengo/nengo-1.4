/*
 * Created on 14-Jun-2006
 */
package ca.neo.util;

import ca.neo.model.StructuralException;

/**
 * <p>Provides data that specifies how to construct an object or set of objects
 * with desired parameters. </p>
 * 
 * <p>Code that uses a ConstructorSpec will call getClassOfProduct() to determine the 
 * class of object that is to be created. Then it will call getNumConstructorArgs() to 
 * determine which constructor to use (it will use the first one with the given number of 
 * arguments). Then to obtain values for each argument, one of the getXArg() methods will 
 * be called (depending on the required type of the argument). This will allow the code 
 * to construct a new object as specified. </p>
 * 
 * <p>Each method can return a different value each time it is called, allowing the spec
 * to describe a range of diverse objects of the same or related types. </p>
 * 
 * @author Bryan Tripp
 */
public interface ConstructorSpec {

	/**
	 * @return The class of the object that is to be produced
	 */
	public Class getClassOfProduct();
	
	/**
	 * @return The number of arguments to the constructor that is to be used 
	 */
	public int getNumConstructorArgs();
	
	/**
	 * @param index Index of an argument to the contructor (from 0)
	 * @return An object that is to be used as the constructor argument at this index
	 * @throws StructuralException if the given index is out of range
	 */
	public Object getObjectArg(int index) throws StructuralException;

	/**
	 * @param index Index of an argument to the contructor (from 0)
	 * @return A float that is to be used as the constructor argument at this index
	 * @throws StructuralException if the given index is out of range, or if a  
	 * 		float can not be obtained from the spec at the given index
	 */
	public float getFloatArg(int index) throws StructuralException;

	/**
	 * @param index Index of an argument to the contructor (from 0)
	 * @return A boolean that is to be used as the constructor argument at this index
	 * @throws StructuralException if the given index is out of range, or if a  
	 * 		boolean can not be obtained from the spec at the given index
	 */
	public boolean getBooleanArg(int index) throws StructuralException;	
	
}

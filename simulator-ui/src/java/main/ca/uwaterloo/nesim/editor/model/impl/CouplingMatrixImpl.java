/*
 * Created on Jan 6, 2004
 */
package ca.uwaterloo.nesim.editor.model.impl;

import ca.uwaterloo.nesim.editor.model.CouplingMatrix;

/**
 * Default implementation of coupling matrix. 
 * 
 * @author Bryan Tripp
 */
public class CouplingMatrixImpl implements CouplingMatrix {

	private int myFromSize; 
	private int myToSize; 
	private float[][] myData;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param theSize the number of rows/columns in the matrix
	 */
	public CouplingMatrixImpl(int theFromSize, int theToSize) {
		myFromSize = theFromSize;
		myToSize = theToSize;
		myData = new float[theToSize][theFromSize];
	}
	
	public CouplingMatrixImpl() {
		
	}
	
	public float[][] getData() {
		return myData;
	}
	
	public void setData(float[][] theData) {
		myData = theData;
	}

	public int getFromSize() {
		return myFromSize;
	}

	public int getToSize() {
		return myToSize;
	}

	/** 
	 * @see ca.uwaterloo.nesim.editor.model.CouplingMatrix#getElement(int, int)
	 */
	public float getElement(int theRow, int theCol) {
		checkIndex(theRow, true);
		checkIndex(theCol, false);		
		return myData[theRow-1][theCol-1];
	}
	
	//convenience method for producing an error message 
	private void checkIndex(int theIndex, boolean isRow) {
		String rowOrCol = (isRow) ? "row" : "col";
		int size = (isRow) ? getToSize() : getFromSize();
		if (theIndex < 1 || theIndex > size) {
			throw new IllegalArgumentException("There is no " + rowOrCol + "#" + theIndex);
		}
	}

	public void setElement(float theValue, int row, int col) {
		myData[row-1][col-1] = theValue;		
	}

}

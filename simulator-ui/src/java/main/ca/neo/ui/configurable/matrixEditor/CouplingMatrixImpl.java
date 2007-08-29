/*
 * Created on Jan 6, 2004
 */
package ca.neo.ui.configurable.matrixEditor;

/**
 * Default implementation of coupling matrix.
 * 
 * @author Bryan Tripp
 */
public class CouplingMatrixImpl implements CouplingMatrix {

	private float[][] myData;
	private int myFromSize;
	private int myToSize;

	public CouplingMatrixImpl() {

	}

	/**
	 * Creates a new instance.
	 * 
	 * @param theSize
	 *            the number of rows/columns in the matrix
	 */
	public CouplingMatrixImpl(int theFromSize, int theToSize) {
		myFromSize = theFromSize;
		myToSize = theToSize;
		myData = new float[theToSize][theFromSize];
	}

	// convenience method for producing an error message
	private void checkIndex(int theIndex, boolean isRow) {
		String rowOrCol = (isRow) ? "row" : "col";
		int size = (isRow) ? getToSize() : getFromSize();
		if (theIndex < 1 || theIndex > size) {
			throw new IllegalArgumentException("There is no " + rowOrCol + "#"
					+ theIndex);
		}
	}

	public float[][] getData() {
		return myData;
	}

	/**
	 * @see ca.neo.ui.configurable.matrixEditor.CouplingMatrix#getElement(int,
	 *      int)
	 */
	public float getElement(int theRow, int theCol) {
		checkIndex(theRow, true);
		checkIndex(theCol, false);
		return myData[theRow - 1][theCol - 1];
	}

	public int getFromSize() {
		return myFromSize;
	}

	public int getToSize() {
		return myToSize;
	}

	public void setData(float[][] theData) {
		myData = theData;
	}

	public void setElement(float theValue, int row, int col) {
		myData[row - 1][col - 1] = theValue;
	}

}

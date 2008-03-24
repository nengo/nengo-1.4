/*
 * Created on Jan 6, 2004
 */
package ca.nengo.ui.configurable.matrixEditor;

/**
 * A specification for the coupling of a projection.
 * 
 * @author Bryan Tripp
 */
public interface CouplingMatrix {

	/**
	 * Returns the element at the given matrix location.
	 * 
	 * @param row
	 *            the row number
	 * @param col
	 *            the column number
	 */
	public float getElement(int row, int col);

	public int getFromSize();

	public int getToSize();

	public void setElement(float theValue, int row, int col);

}

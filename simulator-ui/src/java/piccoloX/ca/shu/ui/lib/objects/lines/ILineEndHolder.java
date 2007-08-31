package ca.shu.ui.lib.objects.lines;

/**
 * Holds an line end
 * 
 * @author Shu Wu
 */
public interface ILineEndHolder {

	/**
	 * @return Line end being held
	 */
	public LineEnd getLineEnd();

	/**
	 * @param lineEnd
	 *            Line end to hold
	 * @return Whether operation was successfull
	 */
	public boolean setLineEnd(LineEnd lineEnd);
}

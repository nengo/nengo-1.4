package ca.neo.ui.configurable.matrixEditor;

import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertyInputPanel;
import ca.neo.ui.configurable.IConfigurable;

/**
 * Configurable transformation matrix
 * 
 * @author Shu Wu
 */
public class ConfigurableMatrix implements IConfigurable {

	/**
	 * Number of columns
	 */
	private int fromSize;

	/**
	 * Matrix to be created
	 */
	private float[][] myMatrix;

	/**
	 * Config Descriptor for the Matrix
	 */
	private PropertyDescriptor pMatrix;

	/**
	 * Number of rows
	 */
	private int toSize;

	/**
	 * @param matrixValues
	 *            Starting values for the matrix
	 */
	public ConfigurableMatrix(float[][] matrixValues) {
		super();
		this.fromSize = matrixValues[0].length;
		this.toSize = matrixValues.length;

		pMatrix = new CouplingMatrixProp(matrixValues);

	}

	/**
	 * @param fromSize
	 *            From size of the matrix to be created
	 * @param toSize
	 *            To size of the matrix to be created
	 */
	public ConfigurableMatrix(int fromSize, int toSize) {
		super();
		this.fromSize = fromSize;
		this.toSize = toSize;

		pMatrix = new CouplingMatrixProp(fromSize, toSize);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.configurable.IConfigurable#completeConfiguration(ca.neo.ui.configurable.ConfigParam)
	 */
	public void completeConfiguration(PropertySet properties) {
		myMatrix = (float[][]) properties.getProperty(pMatrix);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.configurable.IConfigurable#getConfigSchema()
	 */
	public PropertyDescriptor[] getConfigSchema() {
		PropertyDescriptor[] props = new PropertyDescriptor[1];
		props[0] = pMatrix;
		return props;
	}

	/**
	 * @return Matrix
	 */
	public float[][] getMatrix() {
		return myMatrix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.configurable.IConfigurable#getTypeName()
	 */
	public String getTypeName() {
		return fromSize + " to " + toSize + " Coupling Matrix";
	}

}

/**
 * Input panel for a matrix
 * 
 * @author Shu Wu
 */
class CouplingMatrixInputPanel extends PropertyInputPanel {

	private static final long serialVersionUID = 1L;
	private CouplingMatrixImpl couplingMatrix;

	/**
	 * Editor responsible for creating the spreadsheet-like matrix editing
	 * Interface
	 */
	private MatrixEditor editor;

	public CouplingMatrixInputPanel(PropertyDescriptor property, int fromSize,
			int toSize) {
		super(property);

		couplingMatrix = new CouplingMatrixImpl(fromSize, toSize);
		editor = new MatrixEditor(couplingMatrix);

		addToPanel(editor);
	}

	@Override
	public float[][] getValue() {
		editor.finishEditing();

		return couplingMatrix.getData();
	}

	@Override
	public boolean isValueSet() {
		return true;
	}

	@Override
	public void setValue(Object value) {
		/*
		 * Transfers the new matrix values to the editor
		 */
		if (value instanceof float[][]) {
			float[][] matrix = (float[][]) value;

			for (int i = 0; i < matrix.length; i++) {

				for (int j = 0; j < matrix[i].length; j++) {
					editor.setValueAt(matrix[i][j], i, j);
				}
			}
		}
	}
}

/**
 * Config Descriptor for a Coupling Matrix
 * 
 * @author Shu Wu
 */
class CouplingMatrixProp extends PropertyDescriptor {

	private static final long serialVersionUID = 1L;
	private int fromSize, toSize;

	public CouplingMatrixProp(float[][] matrixValues) {
		super("Editor", matrixValues);
		init(matrixValues[0].length, matrixValues.length);
	}

	public CouplingMatrixProp(int fromSize, int toSize) {
		super("Editor");
		init(fromSize, toSize);
	}

	private void init(int fromSize, int toSize) {
		this.fromSize = fromSize;
		this.toSize = toSize;
	}

	@Override
	public PropertyInputPanel createInputPanel() {
		return new CouplingMatrixInputPanel(this, fromSize, toSize);
	}

	@Override
	public Class<float[][]> getTypeClass() {
		return float[][].class;
	}

	@Override
	public String getTypeName() {
		return "Coupling Matrix";
	}

}

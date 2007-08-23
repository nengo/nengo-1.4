package ca.neo.ui.configurable.targets;

import javax.swing.JPanel;

import ca.neo.ui.configurable.AbstractConfigurable;
import ca.neo.ui.configurable.PropertyInputPanel;
import ca.neo.ui.configurable.managers.PropertySet;
import ca.neo.ui.configurable.matrixEditor.CouplingMatrixImpl;
import ca.neo.ui.configurable.matrixEditor.MatrixEditor;
import ca.neo.ui.configurable.struct.PropDescriptor;

public class ConfigurableMatrix extends AbstractConfigurable {
	int fromSize, toSize;

	float[][] myMatrix;

	PropDescriptor pMatrix;
	public ConfigurableMatrix(float[][] matrixValues) {
		super();
		this.fromSize = matrixValues[0].length;
		this.toSize = matrixValues.length;

		pMatrix = new CouplingMatrixProp(matrixValues);

	}

	public ConfigurableMatrix(int fromSize, int toSize) {
		super();
		this.fromSize = fromSize;
		this.toSize = toSize;

		pMatrix = new CouplingMatrixProp(fromSize, toSize);

	}

	@Override
	public void completeConfiguration(PropertySet properties) {
		super.completeConfiguration(properties);
		myMatrix = (float[][]) properties.getProperty(pMatrix);

	}

	@Override
	public PropDescriptor[] getConfigSchema() {
		PropDescriptor[] props = new PropDescriptor[1];
		props[0] = pMatrix;
		return props;
	}

	public float[][] getMatrix() {
		return myMatrix;
	}

	@Override
	public String getTypeName() {
		return fromSize + " to " + toSize + " Coupling Matrix";
	}

}

class CouplingMatrixInputPanel extends PropertyInputPanel {

	private static final long serialVersionUID = 1L;
	CouplingMatrixImpl couplingMatrix;

	MatrixEditor editor;

	int fromSize, toSize;

	public CouplingMatrixInputPanel(PropDescriptor property, int fromSize,
			int toSize) {
		super(property);
		this.fromSize = fromSize;
		this.toSize = toSize;

		couplingMatrix = new CouplingMatrixImpl(fromSize, toSize);
		editor = new MatrixEditor(couplingMatrix);

		addToPanel(editor);
	}
	@Override
	public float[][] getValue() {
		return couplingMatrix.getData();
	}

	@Override
	public void init(JPanel panel) {
		/*
		 * do nothing here
		 */
	}

	@Override
	public boolean isValueSet() {
		return true;
	}

	@Override
	public void setValue(Object value) {
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

class CouplingMatrixProp extends PropDescriptor {

	private static final long serialVersionUID = 1L;
	int fromSize, toSize;

	public CouplingMatrixProp(float[][] matrixValues) {
		this(matrixValues[0].length, matrixValues.length);
		setDefaultValue(matrixValues);

	}

	public CouplingMatrixProp(int fromSize, int toSize) {
		super("Editor");
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

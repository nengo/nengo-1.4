package ca.neo.ui.configurable.targets;

import ca.neo.ui.configurable.ConfigParam;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.ConfigParamInputPanel;
import ca.neo.ui.configurable.IConfigurable;
import ca.neo.ui.configurable.matrixEditor.CouplingMatrixImpl;
import ca.neo.ui.configurable.matrixEditor.MatrixEditor;

public class ConfigurableMatrix implements IConfigurable {
	int fromSize, toSize;

	float[][] myMatrix;

	ConfigParamDescriptor pMatrix;

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

	public void completeConfiguration(ConfigParam properties) {

		myMatrix = (float[][]) properties.getProperty(pMatrix);

	}

	public ConfigParamDescriptor[] getConfigSchema() {
		ConfigParamDescriptor[] props = new ConfigParamDescriptor[1];
		props[0] = pMatrix;
		return props;
	}

	public float[][] getMatrix() {
		return myMatrix;
	}

	public String getTypeName() {
		return fromSize + " to " + toSize + " Coupling Matrix";
	}

}

class CouplingMatrixInputPanel extends ConfigParamInputPanel {

	private static final long serialVersionUID = 1L;
	CouplingMatrixImpl couplingMatrix;

	MatrixEditor editor;

	int fromSize, toSize;

	public CouplingMatrixInputPanel(ConfigParamDescriptor property,
			int fromSize, int toSize) {
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
	public void initPanel() {
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

class CouplingMatrixProp extends ConfigParamDescriptor {

	private static final long serialVersionUID = 1L;
	int fromSize, toSize;

	public CouplingMatrixProp(float[][] matrixValues) {
		super("Editor", matrixValues);
		init(matrixValues[0].length, matrixValues.length);
	}

	public CouplingMatrixProp(int fromSize, int toSize) {
		super("Editor");
		init(fromSize, toSize);
	}

	@Override
	public ConfigParamInputPanel createInputPanel() {
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

	private void init(int fromSize, int toSize) {
		this.fromSize = fromSize;
		this.toSize = toSize;
	}

}

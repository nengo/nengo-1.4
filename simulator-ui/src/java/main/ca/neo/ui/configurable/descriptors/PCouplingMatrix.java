package ca.neo.ui.configurable.descriptors;

import ca.neo.ui.configurable.Property;
import ca.neo.ui.configurable.PropertyInputPanel;
import ca.neo.ui.configurable.panels.CouplingMatrixPanel;

/**
 * Config Descriptor for a Coupling Matrix
 * 
 * @author Shu Wu
 */
public class PCouplingMatrix extends Property {

	private static final long serialVersionUID = 1L;
	private int fromSize, toSize;

	public PCouplingMatrix(float[][] matrixValues) {
		super("Editor", matrixValues);
		init(matrixValues[0].length, matrixValues.length);
	}

	public PCouplingMatrix(int fromSize, int toSize) {
		super("Editor");
		init(fromSize, toSize);
	}

	private void init(int fromSize, int toSize) {
		this.fromSize = fromSize;
		this.toSize = toSize;
	}

	@Override
	protected PropertyInputPanel createInputPanel() {
		return new CouplingMatrixPanel(this, fromSize, toSize);
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

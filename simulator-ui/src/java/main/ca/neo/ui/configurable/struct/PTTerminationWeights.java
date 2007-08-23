package ca.neo.ui.configurable.struct;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ca.neo.ui.configurable.PropertyInputPanel;
import ca.neo.ui.configurable.managers.UserTemplateConfig;
import ca.neo.ui.configurable.targets.ConfigurableMatrix;
import ca.shu.ui.lib.util.Util;

public class PTTerminationWeights extends PropDescriptor {

	private static final long serialVersionUID = 1L;
	private int ensembleDimensions;

	public PTTerminationWeights(String name, int dimensions) {
		super(name);
		this.ensembleDimensions = dimensions;

	}

	@Override
	public PropertyInputPanel createInputPanel() {
		return new DimensionAndWeightsInputPanel(this);

	}

	public int getEnsembleDimensions() {
		return ensembleDimensions;
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

class DimensionAndWeightsInputPanel extends PropertyInputPanel {

	private static final long serialVersionUID = 1L;

	private JTextField tf;

	float[][] matrix;

	boolean matrixEdited = false;

	public DimensionAndWeightsInputPanel(PTTerminationWeights property) {
		super(property);
	}

	@Override
	public PTTerminationWeights getDescriptor() {
		return (PTTerminationWeights) super.getDescriptor();
	}

	public int getDimensions() {

		Integer integerValue = new Integer(tf.getText());
		return integerValue.intValue();

	}

	@Override
	public float[][] getValue() {
		return matrix;
	}

	@Override
	public void init(JPanel panel) {
		JLabel dimensions = new JLabel("Input Dim: ");
		tf = new JTextField(10);
		panel.add(dimensions);
		panel.add(tf);

		JButton configureFunction = new JButton(new EditMatrixAction());

		panel.add(tf);
		panel.add(configureFunction);

	}

	public boolean isDimensionsSet() {
		String textValue = tf.getText();

		if (textValue == null || textValue.compareTo("") == 0)
			return false;

		try {
			@SuppressWarnings("unused")
			Integer value = getDimensions();

		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}
	@Override
	public boolean isValueSet() {
		if (matrixEdited && matrix != null) {
			if (matrix[0].length == getDimensions())
				return true;

		}

		return false;
	}

	public void setDimensions(int dimensions) {
		tf.setText(dimensions + "");

	}

	@Override
	public void setValue(Object value) {
		matrix = (float[][]) value;

		setDimensions(matrix[0].length);

	}

	protected void editMatrix() {
		if (!isDimensionsSet()) {
			Util.UserWarning("Input dimensions not set");
			return;
		}

		/*
		 * get the JDialog parent
		 */
		Container parent = getParent();
		while (parent != null) {
			if (parent instanceof JDialog)
				break;
			parent = parent.getParent();
		}

		if (parent != null && parent instanceof JDialog) {
			ConfigurableMatrix configurableMatrix = new ConfigurableMatrix(
					getFromSize(), getToSize());

			UserTemplateConfig config = new UserTemplateConfig(configurableMatrix,
					(JDialog) parent);
			config.configureAndWait();

			if (configurableMatrix.isConfigured()) {
				setValue(configurableMatrix.getMatrix());
				matrixEdited = true;
			}

		} else {
			Util.UserError("Could not attach properties dialog");
		}

	}

	protected int getFromSize() {
		return getDimensions();
	}

	protected int getToSize() {
		return getDescriptor().getEnsembleDimensions();
	}

	class EditMatrixAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public EditMatrixAction() {
			super("Set Weights");
		}

		public void actionPerformed(ActionEvent e) {
			editMatrix();

		}

	}
}

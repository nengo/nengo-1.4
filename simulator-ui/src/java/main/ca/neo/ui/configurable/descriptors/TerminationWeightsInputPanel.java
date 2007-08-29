package ca.neo.ui.configurable.descriptors;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.ConfigParamInputPanel;
import ca.neo.ui.configurable.managers.UserTemplateConfigurer;
import ca.neo.ui.configurable.matrixEditor.ConfigurableMatrix;
import ca.shu.ui.lib.util.Util;

/**
 * Input panel for Termination Weights Matrix
 * 
 * @author Shu
 * 
 */
class TerminationWeightsInputPanel extends ConfigParamInputPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * The termination weights matrix
	 */
	private float[][] matrix;

	/**
	 * Whether the matrix has been user edited
	 */
	private boolean matrixEdited = false;

	/**
	 * Text field containing the user-entered dimensions of the weights
	 */
	private JTextField tf;

	public TerminationWeightsInputPanel(CTerminationWeights property) {
		super(property);
	}

	/**
	 * @return The dimensions of this termination
	 */
	private int getDimensions() {

		Integer integerValue = new Integer(tf.getText());
		return integerValue.intValue();

	}

	/**
	 * @return True if dimensions have been set
	 */
	private boolean isDimensionsSet() {
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

	/**
	 * @param dimensions
	 *            New dimensions
	 */
	private void setDimensions(int dimensions) {
		tf.setText(dimensions + "");

	}

	/**
	 * Edits the termination weights matrix
	 */
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

			UserTemplateConfigurer config = new UserTemplateConfigurer(
					configurableMatrix, (JDialog) parent);
			try {
				config.configureAndWait();
			} catch (ConfigException e) {
				e.defaultHandledBehavior();
			}

			setValue(configurableMatrix.getMatrix());
			matrixEdited = true;

		} else {
			Util.UserError("Could not attach properties dialog");
		}

	}

	/**
	 * @return From size, of the matrix to be created
	 */
	protected int getFromSize() {
		return getDimensions();
	}

	/**
	 * @return To size, of the matrix to be created
	 */
	protected int getToSize() {
		return getDescriptor().getEnsembleDimensions();
	}

	@Override
	protected void initPanel() {
		JLabel dimensions = new JLabel("Input Dim: ");
		tf = new JTextField(10);
		addToPanel(dimensions);
		addToPanel(tf);

		JButton configureFunction = new JButton(new EditMatrixAction());

		addToPanel(tf);
		addToPanel(configureFunction);

	}

	@Override
	public CTerminationWeights getDescriptor() {
		return (CTerminationWeights) super.getDescriptor();
	}

	@Override
	public float[][] getValue() {
		return matrix;
	}

	@Override
	public boolean isValueSet() {
		if (matrixEdited && matrix != null) {
			if (matrix[0].length == getDimensions())
				return true;

		} else {

			setStatusMsg("matrix not set");
		}

		return false;
	}

	@Override
	public void setValue(Object value) {
		if (value != null) {
			matrix = (float[][]) value;

			setDimensions(matrix[0].length);

			setStatusMsg("");

		}

	}

	/**
	 * User triggered action to edit the termination weights matrix
	 * 
	 * @author Shu Wu
	 * 
	 */
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


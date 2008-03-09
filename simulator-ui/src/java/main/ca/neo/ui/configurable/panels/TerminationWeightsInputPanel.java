package ca.neo.ui.configurable.panels;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertyInputPanel;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.descriptors.PCouplingMatrix;
import ca.neo.ui.configurable.descriptors.PTerminationWeights;
import ca.neo.ui.configurable.managers.ConfigManager;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.Util;

/**
 * Input panel for Termination Weights Matrix
 * 
 * @author Shu
 */
public class TerminationWeightsInputPanel extends PropertyInputPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * The termination weights matrix
	 */
	private float[][] matrix;

	/**
	 * Text field containing the user-entered dimensions of the weights
	 */
	private JTextField tf;

	public TerminationWeightsInputPanel(PTerminationWeights property) {
		super(property);
		initPanel();
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
			UserMessages.showWarning("Input dimensions not set");
			return;
		}

		/*
		 * get the JDialog parent
		 */
		Container parent = getJPanel().getParent();
		while (parent != null) {
			if (parent instanceof JDialog)
				break;
			parent = parent.getParent();
		}

		if (parent != null && parent instanceof JDialog) {
			PropertyDescriptor pCouplingMatrix;
			if (isValueSet()) {
				/*
				 * Create a property descriptor with a set matrix
				 */
				pCouplingMatrix = new PCouplingMatrix(getValue());
			} else {
				/*
				 * Create a property descriptor with no default value
				 */
				pCouplingMatrix = new PCouplingMatrix(getFromSize(), getToSize());
			}

			String configName = getFromSize() + " to " + getToSize() + " Coupling Matrix";

			try {
				PropertySet result = ConfigManager.configure(
						new PropertyDescriptor[] { pCouplingMatrix }, configName, parent,
						ConfigManager.ConfigMode.STANDARD);

				setValue((float[][]) result.getProperty(pCouplingMatrix));
			} catch (ConfigException e) {
				e.defaultHandleBehavior();
			}

		} else {
			UserMessages.showError("Could not attach properties dialog");
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

	private void initPanel() {
		JLabel dimensions = new JLabel("Input Dim: ");
		tf = new JTextField(10);
		add(dimensions);
		add(tf);

		JButton configureFunction = new JButton(new EditMatrixAction());

		add(tf);
		add(configureFunction);
	}

	@Override
	public PTerminationWeights getDescriptor() {
		return (PTerminationWeights) super.getDescriptor();
	}

	@Override
	public float[][] getValue() {
		return matrix;
	}

	@Override
	public boolean isValueSet() {
		if (matrix != null && matrix[0].length == getDimensions()) {
			return true;
		}
		setStatusMsg("matrix not set");
		return false;
	}

	@Override
	public void setValue(Object value) {
		if ((value != null) && (value instanceof float[][])
				&& (getToSize() == ((float[][]) value).length)) {
			matrix = (float[][]) value;
			setDimensions(matrix[0].length);

			if (isValueSet())
				setStatusMsg("");
		} else {
			Util.debugMsg("Saved termination weights don't fit, they will be replaced");
		}
	}

	/**
	 * User triggered action to edit the termination weights matrix
	 * 
	 * @author Shu Wu
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

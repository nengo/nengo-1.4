package ca.neo.ui.configurable.panels;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ca.neo.math.Function;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.ConfigSchemaImpl;
import ca.neo.ui.configurable.ConfigSchema;
import ca.neo.ui.configurable.IConfigurable;
import ca.neo.ui.configurable.Property;
import ca.neo.ui.configurable.PropertyInputPanel;
import ca.neo.ui.configurable.ConfigResult;
import ca.neo.ui.configurable.descriptors.PFunction;
import ca.neo.ui.configurable.descriptors.PFunctionArray;
import ca.neo.ui.configurable.managers.UserConfigurer;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.Util;

/**
 * Input panel for entering an Array of Functions
 * 
 * @author Shu Wu
 */
public class FunctionArrayPanel extends PropertyInputPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Function array
	 */
	private Function[] myFunctionsWr;

	/**
	 * Text field component for entering the dimensions of the function array
	 */
	private JTextField tf;
	private int inputDimension;

	public FunctionArrayPanel(PFunctionArray property, int inputDimension) {
		super(property);
		initPanel();
		this.inputDimension = inputDimension;
	}

	/**
	 * Edits the Function Array using a child dialog
	 */
	protected void editFunctionArray() {
		if (!isOutputDimensionsSet()) {
			UserMessages.showWarning("Output dimensions not set");
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
			ConfigurableFunctionArray configurableFunctions = new ConfigurableFunctionArray(
					getInputDimension(), getOutputDimension(), getValue());

			UserConfigurer config = new UserConfigurer(configurableFunctions, (JDialog) parent);
			try {
				config.configureAndWait();
				setValue(configurableFunctions.getFunctions());
			} catch (ConfigException e) {
				e.defaultHandleBehavior();
			}

		} else {
			UserMessages.showError("Could not attach properties dialog");
		}

	}

	@Override
	public PFunctionArray getDescriptor() {
		return (PFunctionArray) super.getDescriptor();
	}

	public int getOutputDimension() {

		Integer integerValue = new Integer(tf.getText());
		return integerValue.intValue();

	}

	@Override
	public Function[] getValue() {
		return myFunctionsWr;
	}

	private void initPanel() {
		JLabel dimensions = new JLabel("Output Dimensions: ");
		tf = new JTextField(10);
		add(dimensions);
		add(tf);

		JButton configureFunction = new JButton(new EditFunctions());
		add(tf);
		add(configureFunction);

	}

	/**
	 * @return True if Function Array dimensions has been set
	 */
	public boolean isOutputDimensionsSet() {
		String textValue = tf.getText();

		if (textValue == null || textValue.compareTo("") == 0)
			return false;

		try {
			@SuppressWarnings("unused")
			Integer value = getOutputDimension();

		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isValueSet() {
		if (myFunctionsWr != null && (myFunctionsWr.length == getOutputDimension())) {
			return true;
		} else {
			setStatusMsg("Functions not set");
		}

		return false;
	}

	/**
	 * @param dimensions
	 *            Dimensions of the function array
	 */
	public void setDimensions(int dimensions) {
		tf.setText(dimensions + "");

	}

	@Override
	public void setValue(Object value) {
		Function[] functions = (Function[]) value;

		/*
		 * Check that the functions are of the correct dimension before
		 * committing
		 */
		for (int i = 0; i < functions.length; i++) {
			if (functions[i].getDimension() != getInputDimension()) {
				Util.debugMsg("Saved functions are of a different dimension, they can't be used");
				return;
			}
		}

		if (value != null) {
			myFunctionsWr = functions;
			setDimensions(myFunctionsWr.length);
			setStatusMsg("");
		} else {

		}
	}

	/**
	 * Edit Functions Action
	 * 
	 * @author Shu Wu
	 */
	class EditFunctions extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public EditFunctions() {
			super("Set Functions");
		}

		public void actionPerformed(ActionEvent e) {
			editFunctionArray();

		}

	}

	public int getInputDimension() {
		return inputDimension;
	}
}

/**
 * Configurable object which creates an array of functions
 * 
 * @author Shu Wu
 */
/**
 * @author Shu
 */
class ConfigurableFunctionArray implements IConfigurable {

	/**
	 * Number of functions to be created
	 */
	private int outputDimension;

	/**
	 * Dimensions of the functions to be created
	 */
	private int inputDimension;

	/**
	 * Array of functions to be created
	 */
	private Function[] myFunctions;

	private Function[] defaultValues;

	/**
	 * @param outputDimension
	 *            Number of functions to create
	 */
	public ConfigurableFunctionArray(int inputDimension, int outputDimension,
			Function[] defaultValues) {
		super();
		this.defaultValues = defaultValues;
		init(inputDimension, outputDimension);

	}

	/**
	 * Initializes this instance
	 * 
	 * @param outputDimension
	 *            number of functions to create
	 */
	private void init(int inputDimension, int outputDimension) {
		this.inputDimension = inputDimension;
		this.outputDimension = outputDimension;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.configurable.IConfigurable#completeConfiguration(ca.neo.ui.configurable.ConfigParam)
	 */
	public void completeConfiguration(ConfigResult properties) {
		myFunctions = new Function[outputDimension];
		for (int i = 0; i < outputDimension; i++) {
			myFunctions[i] = ((Function) properties.getValue("Function " + i));

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.configurable.IConfigurable#getConfigSchema()
	 */
	public ConfigSchema getSchema() {
		Property[] props = new Property[outputDimension];

		for (int i = 0; i < outputDimension; i++) {

			Function defaultValue = null;

			if (defaultValues != null && i < defaultValues.length && defaultValues[i] != null) {
				defaultValue = defaultValues[i];

			}
			PFunction function = new PFunction("Function " + i, inputDimension, false, defaultValue);

			props[i] = function;
		}

		return new ConfigSchemaImpl(props);
	}

	/**
	 * @return Functions created
	 */
	public Function[] getFunctions() {
		return myFunctions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.configurable.IConfigurable#getTypeName()
	 */
	public String getTypeName() {
		return outputDimension + "x Functions";
	}

	public void preConfiguration(ConfigResult props) throws ConfigException {
		// do nothing
	}

	public String getDescription() {
		return getTypeName();
	}

}

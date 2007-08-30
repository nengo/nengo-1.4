package ca.neo.ui.configurable.descriptors;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ca.neo.math.Function;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.ConfigParam;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.ConfigParamInputPanel;
import ca.neo.ui.configurable.IConfigurable;
import ca.neo.ui.configurable.managers.UserTemplateConfigurer;
import ca.shu.ui.lib.util.Util;

/**
 * Input panel for entering an Array of Functions
 * 
 * @author Shu Wu
 * 
 */
public class FunctionArrayPanel extends ConfigParamInputPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Function array
	 */
	private Function[] functions;

	/**
	 * Whether function array has been user edited
	 */
	private boolean functionsEdited = false;

	/**
	 * Text field component for entering the dimensions of the function array
	 */
	private JTextField tf;

	public FunctionArrayPanel(CFunctionArray property) {
		super(property);
	}

	/**
	 * Edits the Function Array using a child dialog
	 */
	protected void editFunctionArray() {
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
			ConfigurableFunctionArray configurableFunctions = new ConfigurableFunctionArray(
					getDimensions());

			UserTemplateConfigurer config = new UserTemplateConfigurer(
					configurableFunctions, (JDialog) parent);
			try {
				config.configureAndWait();
			} catch (ConfigException e) {
				e.defaultHandleBehavior();
			}

			setValue(configurableFunctions.getFunctions());
			functionsEdited = true;

		} else {
			Util.UserError("Could not attach properties dialog");
		}

	}

	@Override
	public CFunctionArray getDescriptor() {
		return (CFunctionArray) super.getDescriptor();
	}

	public int getDimensions() {

		Integer integerValue = new Integer(tf.getText());
		return integerValue.intValue();

	}

	@Override
	public Function[] getValue() {
		return functions;
	}

	@Override
	public void initPanel() {
		JLabel dimensions = new JLabel("Dimensions: ");
		tf = new JTextField(10);
		addToPanel(dimensions);
		addToPanel(tf);

		JButton configureFunction = new JButton(new EditFunctions());
		addToPanel(tf);
		addToPanel(configureFunction);

	}

	/**
	 * @return True if Function Array dimensions has been set
	 */
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
		if (functionsEdited && functions != null) {
			if (functions.length == getDimensions())
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
		if (value != null) {
			functions = (Function[]) value;
			setDimensions(functions.length);
			setStatusMsg("");
		} else {

		}
	}

	/**
	 * Edit Functions Action
	 * 
	 * @author Shu Wu
	 * 
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
}

/**
 * Configurable object which creates an array of functions
 * 
 * @author Shu Wu
 * 
 */
/**
 * @author Shu
 * 
 */
class ConfigurableFunctionArray implements IConfigurable {

	/**
	 * Number of functions to be created
	 */
	private int dimension;

	/**
	 * Array of functions to be created
	 */
	private Function[] myFunctions;

	/**
	 * @param functions
	 *            Existing function array to be used as starting values
	 */
	public ConfigurableFunctionArray(Function[] functions) {
		super();
		myFunctions = functions;
		init(functions.length);
	}

	/**
	 * @param dimension
	 *            Number of functions to create
	 */
	public ConfigurableFunctionArray(int dimension) {
		super();
		init(dimension);

	}

	/**
	 * Initializes this instance
	 * 
	 * @param dimension
	 *            number of functions to create
	 */
	private void init(int dimension) {
		this.dimension = dimension;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.configurable.IConfigurable#completeConfiguration(ca.neo.ui.configurable.ConfigParam)
	 */
	public void completeConfiguration(ConfigParam properties) {
		myFunctions = new Function[dimension];
		for (int i = 0; i < dimension; i++) {
			myFunctions[i] = (Function) properties.getProperty("Function " + i);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.configurable.IConfigurable#getConfigSchema()
	 */
	public ConfigParamDescriptor[] getConfigSchema() {
		ConfigParamDescriptor[] props = new ConfigParamDescriptor[dimension];

		for (int i = 0; i < dimension; i++) {
			Function defaultFunction = null;
			if (myFunctions != null) {
				defaultFunction = myFunctions[i];
			}
			CFunction function = new CFunction("Function " + i,
					defaultFunction);

			props[i] = function;
		}

		return props;
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
		return dimension + "x Functions";
	}
}

package ca.neo.ui.configurable.struct;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ca.neo.math.Function;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.ConfigParamInputPanel;
import ca.neo.ui.configurable.managers.UserTemplateConfig;
import ca.neo.ui.configurable.targets.ConfigurableFunctionArray;
import ca.shu.ui.lib.util.Util;

public class FunctionArrayPanel extends ConfigParamInputPanel {

	private static final long serialVersionUID = 1L;

	private JTextField tf;

	Function[] functions;

	boolean functionsEdited = false;

	public FunctionArrayPanel(PTFunctionArray property) {
		super(property);
//		setValue(null);
	}

	@Override
	public PTFunctionArray getDescriptor() {
		return (PTFunctionArray) super.getDescriptor();
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
			ConfigurableFunctionArray configurableFunctions = new ConfigurableFunctionArray(
					getDimensions());

			UserTemplateConfig config = new UserTemplateConfig(
					configurableFunctions, (JDialog) parent);
			try {
				config.configureAndWait();
			} catch (ConfigException e) {
				e.defaultHandledBehavior();
			}

			setValue(configurableFunctions.getFunctions());
			functionsEdited = true;

		} else {
			Util.UserError("Could not attach properties dialog");
		}

	}

	class EditFunctions extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public EditFunctions() {
			super("Set Functions");
		}

		public void actionPerformed(ActionEvent e) {
			editMatrix();

		}

	}
}

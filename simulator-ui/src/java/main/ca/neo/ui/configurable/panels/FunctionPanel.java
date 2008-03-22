package ca.neo.ui.configurable.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import ca.neo.math.Function;
import ca.neo.ui.actions.PlotFunctionAction;
import ca.neo.ui.configurable.PropertyInputPanel;
import ca.neo.ui.configurable.descriptors.PFunction;
import ca.neo.ui.configurable.descriptors.functions.AbstractFn;
import ca.neo.ui.configurable.descriptors.functions.ConfigurableFunction;
import ca.neo.ui.configurable.descriptors.functions.FnAdvanced;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.Util;

/**
 * Input Panel for editing an individual Function
 * 
 * @author Shu Wu
 */
public class FunctionPanel extends PropertyInputPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Combo box for selecting a function type, types of function are stored in
	 * PTFunction.functions
	 */
	private JComboBox comboBox;

	private ConfigurableFunction[] configurableFunctionsList;

	/**
	 * Function
	 */
	private Function function = null;

	private JButton newBtn;

	private JButton previewBtn;
	private JButton configureBtn;
	/**
	 * Currently selected item in the comboBox
	 */
	private ConfigurableFunction selectedConfigurableFunction;

	public FunctionPanel(PFunction property, ConfigurableFunction[] functions) {
		super(property);
		this.configurableFunctionsList = functions;

		initPanel();
	}

	private void initPanel() {
		comboBox = new JComboBox(configurableFunctionsList);
		selectedConfigurableFunction = (AbstractFn) comboBox.getSelectedItem();

		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (comboBox.getSelectedItem() != selectedConfigurableFunction) {
					setValue(null);
					updateSelection(comboBox.getSelectedItem());
				}
			}
		});

		add(comboBox);

		newBtn = new JButton(new NewParametersAction());
		add(newBtn);

		configureBtn = new JButton(new EditAction());
		add(configureBtn);

		previewBtn = new JButton(new PreviewFunctionAction());
		add(previewBtn);

		updateSelection(comboBox.getSelectedItem());
	}

	private void updateSelection(Object selectedItem) {
		selectedConfigurableFunction = (ConfigurableFunction) comboBox.getSelectedItem();

		if (selectedItem instanceof FnAdvanced) {
			newBtn.setEnabled(true);
		} else {
			newBtn.setEnabled(false);
		}

	}

	/**
	 * Previews the function
	 */
	protected void previewFunction() {

		if (function != null) {
			(new PlotFunctionAction("Function preview", function, getDialogParent())).doAction();
		} else {
			UserMessages.showWarning("Please set this function first.");
		}
	}

	/**
	 * Sets up the function using the configurable Function wrapper
	 * 
	 * @param resetValue
	 *            Whether to reset the ConfigurableFunction's value before
	 *            editing
	 */
	protected void setParameters(boolean resetValue) {

		/*
		 * get the JDialog parent
		 */
		JDialog parent = getDialogParent();

		if (parent != null) {
			if (resetValue) {
				selectedConfigurableFunction.setFunction(null);
			}

			/*
			 * Configure the function
			 */
			Function function = selectedConfigurableFunction.configureFunction(parent);

			setValue(function);
		} else {
			UserMessages.showError("Could not attach properties dialog");
		}

	}

	@Override
	public PFunction getDescriptor() {
		return (PFunction) super.getDescriptor();
	}

	@Override
	public Function getValue() {
		return function;
	}

	@Override
	public boolean isValueSet() {
		if (function != null) {

			if (function.getDimension() != getDescriptor().getInputDimension()) {
				setStatusMsg("input dimension must be " + getDescriptor().getInputDimension()
						+ ", it is currently " + function.getDimension());
				return false;
			}
			return true;

		} else {
			setStatusMsg("function parameters not set");

			return false;
		}

	}

	@Override
	public void setValue(Object value) {

		if (value != null && value instanceof Function) {

			function = (Function) value;
			boolean configurableFunctionFound = false;

			/*
			 * Updates the combo box to reflect the function type set
			 */
			for (int i = 0; i < configurableFunctionsList.length; i++) {

				if (configurableFunctionsList[i].getFunctionType().isInstance(function)) {
					selectedConfigurableFunction = configurableFunctionsList[i];
					selectedConfigurableFunction.setFunction(function);

					comboBox.setSelectedItem(selectedConfigurableFunction);
					configurableFunctionFound = true;
					break;
				}
			}

			if (!configurableFunctionFound) {
				Util.Assert(false, "Unsupported function");
			}

			if (isValueSet()) {
				setStatusMsg("");
			}

		} else {
			function = null;
		}

	}

	/**
	 * Set up the parameters of a new function
	 * 
	 * @author Shu Wu
	 */
	class NewParametersAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public NewParametersAction() {
			super("New");
		}

		public void actionPerformed(ActionEvent e) {
			setParameters(true);
		}

	}

	/**
	 * Preview the funciton
	 * 
	 * @author Shu Wu
	 */
	class PreviewFunctionAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public PreviewFunctionAction() {
			super("Preview");
		}

		public void actionPerformed(ActionEvent e) {
			previewFunction();
		}

	}

	/**
	 * Set up the parameters of the existing function
	 * 
	 * @author Shu Wu
	 */
	class EditAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public EditAction() {
			super("Set");
		}

		public void actionPerformed(ActionEvent e) {
			setParameters(false);
		}

	}

}

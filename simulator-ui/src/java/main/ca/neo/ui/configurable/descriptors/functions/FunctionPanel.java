package ca.neo.ui.configurable.descriptors.functions;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import ca.neo.ui.actions.PlotFunctionAction;
import ca.neo.ui.configurable.PropertyInputPanel;
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

	/**
	 * Function
	 */
	private FunctionWrapper functionWr = null;

	/**
	 * Currently selected item in the comboBox
	 */
	private AbstractConfigurableFunction selectedConfigurableFunction;

	private JButton previewBtn;

	private AbstractConfigurableFunction[] configurableFunctionsList;

	public FunctionPanel(PFunction property,
			AbstractConfigurableFunction[] functions) {
		super(property);
		this.configurableFunctionsList = functions;

		initPanel();
	}

	/**
	 * Previews the function
	 */
	protected void previewFunction() {

		if (functionWr != null) {
			(new PlotFunctionAction("Function preview", functionWr.unwrap(),
					getDialogParent())).doAction();
		} else {
			UserMessages.showWarning("Please set this function first.");
		}
	}

	/**
	 * Sets up the function using the configurable Function wrapper
	 */
	protected void setParameters() {

		selectedConfigurableFunction = (AbstractConfigurableFunction) comboBox
				.getSelectedItem();

		/*
		 * get the JDialog parent
		 */
		JDialog parent = getDialogParent();

		if (parent != null) {
			/*
			 * Configure the function
			 */
			// selectedType.setFunction(null);
			selectedConfigurableFunction.configure(parent);

		} else {
			UserMessages.showError("Could not attach properties dialog");
		}
		setValue(selectedConfigurableFunction.getFunctionWrapper());

	}

	@Override
	public FunctionWrapper getValue() {
		return functionWr;
	}

	private void initPanel() {
		comboBox = new JComboBox(configurableFunctionsList);
		selectedConfigurableFunction = (AbstractConfigurableFunction) comboBox
				.getSelectedItem();

		comboBox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				if (comboBox.getSelectedItem() != selectedConfigurableFunction) {
					setValue(null);
				}

			}

		});
		JButton configureBtn = new JButton(new SetParametersAction());

		addToPanel(comboBox);
		addToPanel(configureBtn);

		previewBtn = new JButton(new PreviewFunctionAction());
		addToPanel(previewBtn);

	}

	@Override
	public boolean isValueSet() {
		if (functionWr != null
				&& functionWr.unwrap().getDimension() == getDescriptor()
						.getInputDimension()) {

			return true;

		} else {
			setStatusMsg("function parameters not set");

			return false;
		}

	}

	@Override
	public void setValue(Object value) {

		if (value != null && value instanceof FunctionWrapper) {

			functionWr = (FunctionWrapper) value;

			boolean configurableFunctionFound = false;

			/*
			 * Updates the combo box to reflect the function type set
			 */
			for (int i = 0; i < configurableFunctionsList.length; i++) {

				if (configurableFunctionsList[i].getTypeName().compareTo(
						functionWr.getTypeName()) == 0) {
					selectedConfigurableFunction = configurableFunctionsList[i];
					selectedConfigurableFunction.setFunctionWrapper(functionWr);

					comboBox.setSelectedItem(selectedConfigurableFunction);
					configurableFunctionFound = true;
					break;
				}
			}

			Util.Assert(configurableFunctionFound, "Unsupported function");

			if (isValueSet()) {
				setStatusMsg("");
			}

		} else {
			functionWr = null;
		}

	}

	/**
	 * Set up the parameters of the function
	 * 
	 * @author Shu Wu
	 */
	class SetParametersAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SetParametersAction() {
			super("Set");
		}

		public void actionPerformed(ActionEvent e) {
			setParameters();
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

	@Override
	public PFunction getDescriptor() {
		return (PFunction) super.getDescriptor();
	}

}

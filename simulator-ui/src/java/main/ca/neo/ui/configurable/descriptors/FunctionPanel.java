package ca.neo.ui.configurable.descriptors;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import ca.neo.math.Function;
import ca.neo.ui.actions.PlotFunctionAction;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertyInputPanel;
import ca.neo.ui.configurable.descriptors.functions.AbstractConfigurableFunction;
import ca.neo.ui.configurable.managers.UserTemplateConfigurer;
import ca.shu.ui.lib.util.UserMessages;

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
	private Function function = null;

	/**
	 * Type of configuration function wrapper selected
	 */
	private AbstractConfigurableFunction selectedType;

	private JButton previewBtn;

	public FunctionPanel(PropertyDescriptor property) {
		super(property);
		initPanel();
	}

	/**
	 * Previews the function
	 */
	protected void previewFunction() {

		if (function != null) {
			(new PlotFunctionAction("Function preview", "Preview function",
					function, getDialogParent())).doAction();
		} else {
			UserMessages.showWarning("Please set this function first.");
		}
	}

	/**
	 * Sets up the function using the configurable Function wrapper
	 */
	protected void setParameters() {
		selectedType = (AbstractConfigurableFunction) comboBox
				.getSelectedItem();

		if (selectedType == null)
			return;

		/*
		 * get the JDialog parent
		 */
		JDialog parent = getDialogParent();

		if (parent != null ) {
			/*
			 * Configure the function
			 */
			selectedType.setFunction(null);
			UserTemplateConfigurer config = new UserTemplateConfigurer(
					selectedType, (JDialog) parent);
			try {
				config.configureAndWait();
			} catch (ConfigException e) {
				e.defaultHandleBehavior();
			}

		} else {
			UserMessages.showError("Could not attach properties dialog");
		}
		setValue(selectedType.getFunction());

	}

	@Override
	public Function getValue() {
		return function;
	}

	private void initPanel() {
		comboBox = new JComboBox(PFunction.functions);
		selectedType = (AbstractConfigurableFunction) comboBox
				.getSelectedItem();

		comboBox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				if (comboBox.getSelectedItem() != selectedType) {
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
		if (function != null) {

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

			/*
			 * Updates the combo box to reflect the function type set
			 */
			for (int i = 0; i < PFunction.functions.length; i++) {

				if ((PFunction.functions[i].getFunctionType())
						.isInstance(function)) {
					selectedType = PFunction.functions[i];
					comboBox.setSelectedItem(PFunction.functions[i]);
				}
			}

			setStatusMsg("");

		} else {
			function = null;
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

}

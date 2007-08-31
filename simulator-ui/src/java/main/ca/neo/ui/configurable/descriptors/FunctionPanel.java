package ca.neo.ui.configurable.descriptors;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import ca.neo.math.Function;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.ConfigParamInputPanel;
import ca.neo.ui.configurable.managers.UserTemplateConfigurer;
import ca.shu.ui.lib.util.UserMessages;

/**
 * Input Panel for editing an individual Function
 * 
 * @author Shu Wu
 * 
 */
public class FunctionPanel extends ConfigParamInputPanel {

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
	private ConfigurableFunction selectedType;

	public FunctionPanel(ConfigParamDescriptor property) {
		super(property);
	}

	/**
	 * Sets up the function using the configurable Function wrapper
	 */
	protected void setParameters() {
		selectedType = (ConfigurableFunction) comboBox.getSelectedItem();

		if (selectedType == null)
			return;

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
	public Object getValue() {
		return function;
	}

	@Override
	public void initPanel() {
		comboBox = new JComboBox(CFunction.functions);
		selectedType = (ConfigurableFunction) comboBox.getSelectedItem();

		comboBox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				if (comboBox.getSelectedItem() != selectedType) {
					setValue(null);
				}

			}

		});
		JButton configureFunction = new JButton(new SetParametersAction());

		addToPanel(comboBox);
		addToPanel(configureFunction);
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
			for (int i = 0; i < CFunction.functions.length; i++) {

				if ((CFunction.functions[i].getFunctionType())
						.isInstance(function)) {
					selectedType = CFunction.functions[i];
					comboBox.setSelectedItem(CFunction.functions[i]);

				}
			}

			setStatusMsg("");

		} else {
			function = null;
		}

	}

	/**
	 * Action triggered by the user to set up the parameters of the function
	 * 
	 * @author Shu Wu
	 * 
	 */
	class SetParametersAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SetParametersAction() {
			super("Set Parameters");
		}

		public void actionPerformed(ActionEvent e) {
			setParameters();
		}

	}

}

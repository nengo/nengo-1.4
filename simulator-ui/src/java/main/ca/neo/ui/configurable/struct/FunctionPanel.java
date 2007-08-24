package ca.neo.ui.configurable.struct;

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
import ca.neo.ui.configurable.managers.UserTemplateConfig;
import ca.shu.ui.lib.util.Util;

public class FunctionPanel extends ConfigParamInputPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JComboBox comboBox;

	private Function function = null;

	public FunctionPanel(ConfigParamDescriptor property) {
		super(property);
		// setValue(null);
	}

	@Override
	public Object getValue() {
		return function;
	}

	ConfigurableFunction selectedFunctionType;

	@Override
	public void initPanel() {
		comboBox = new JComboBox(PTFunction.functions);
		selectedFunctionType = (ConfigurableFunction) comboBox
				.getSelectedItem();

		comboBox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				if (comboBox.getSelectedItem() != selectedFunctionType) {
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
			for (int i = 0; i < PTFunction.functions.length; i++) {

				if ((PTFunction.functions[i].getFunctionClass())
						.isInstance(function)) {
					selectedFunctionType = PTFunction.functions[i];
					comboBox.setSelectedItem(PTFunction.functions[i]);

				}
			}

			setStatusMsg("");

		} else {
			function = null;
		}

	}

	protected void setParameters() {
		selectedFunctionType = (ConfigurableFunction) comboBox
				.getSelectedItem();

		if (selectedFunctionType == null)
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
			selectedFunctionType.setFunction(null);
			UserTemplateConfig config = new UserTemplateConfig(
					selectedFunctionType, (JDialog) parent);
			try {
				config.configureAndWait();
			} catch (ConfigException e) {
				e.defaultHandledBehavior();
			}

		} else {
			Util.UserError("Could not attach properties dialog");
		}
		setValue(selectedFunctionType.getFunction());

	}

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

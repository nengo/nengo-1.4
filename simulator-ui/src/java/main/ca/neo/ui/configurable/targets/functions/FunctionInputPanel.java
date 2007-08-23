package ca.neo.ui.configurable.targets.functions;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import ca.neo.math.Function;
import ca.neo.ui.configurable.PropertyInputPanel;
import ca.neo.ui.configurable.managers.UserTemplateConfig;
import ca.neo.ui.configurable.struct.PropDescriptor;
import ca.shu.ui.lib.util.Util;

public class FunctionInputPanel extends PropertyInputPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * TODO: make meta properties static to save memory
	 * 
	 */
	static final ConfigurableFunction[] functions = new ConfigurableFunction[] {

	new FConstantFunction(), new FFourierFunction(), new FGaussianPDF(), };

	JComboBox comboBox;

	Function function = null;

	public FunctionInputPanel(PropDescriptor property) {
		super(property);
		// TODO Auto-generated constructor stub
		setValue(null);
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return function;
	}

	@Override
	public void init(JPanel panel) {
		comboBox = new JComboBox(functions);
		JButton configureFunction = new JButton(new SetParametersAction());

		panel.add(comboBox);
		panel.add(configureFunction);
	}

	@Override
	public boolean isValueSet() {
		if (function != null)
			return true;
		else
			return false;
	}

	@Override
	public void setValue(Object value) {
		if (value != null && value instanceof Function) {
			function = (Function) value;
			// System.out.println("setting status msg");
			setStatusMsg("");

		} else {
			setStatusMsg("function parameters not set");
		}

	}

	protected void setParameters() {
		ConfigurableFunction fnDescriptor = (ConfigurableFunction) comboBox
				.getSelectedItem();

		if (fnDescriptor == null)
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
			fnDescriptor.setFunction(null);
			UserTemplateConfig config = new UserTemplateConfig(fnDescriptor,
					(JDialog) parent);
			config.configureAndWait();

		} else {
			Util.UserError("Could not attach properties dialog");
		}
		setValue(fnDescriptor.getFunction());

	}

	class SetParametersAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SetParametersAction() {
			super("Set Parameters");
			// TODO Auto-generated constructor stub
		}

		public void actionPerformed(ActionEvent e) {
			setParameters();

		}

	}

}

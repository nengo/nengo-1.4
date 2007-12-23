package ca.neo.ui.configurable.descriptors.functions;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import ca.neo.math.Function;
import ca.neo.math.FunctionInterpreter;
import ca.neo.math.impl.DefaultFunctionInterpreter;
import ca.neo.math.impl.PostfixFunction;
import ca.neo.ui.actions.PlotFunctionAction;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.IConfigurable;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertyInputPanel;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.descriptors.PFunction;
import ca.neo.ui.configurable.descriptors.PInt;
import ca.neo.ui.configurable.descriptors.PString;
import ca.neo.ui.configurable.managers.ConfigDialog;
import ca.neo.ui.configurable.managers.ConfigManager;
import ca.neo.ui.configurable.managers.UserConfigurer;
import ca.neo.ui.configurable.panels.StringPanel;
import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.util.UserMessages;

public class FnCustom extends AbstractFn {

	private static final String DIMENSION_STR = "Input Dimensions";

	private static final String EXPRESSION_STR = "Expression";
	private static DefaultFunctionInterpreter interpreter = new DefaultFunctionInterpreter();

	private int myInputDimensions;
	private PropertyDescriptor pExpression;
	InterpreterFunctionConfigurer configurer;
	boolean isInputDimEditable;

	public FnCustom(int inputDimensions, boolean isInputDimEditable) {
		super("User-defined Function", PostfixFunction.class);
		this.myInputDimensions = inputDimensions;
		this.isInputDimEditable = isInputDimEditable;

	}

	private Function parseFunction(PropertySet props) throws ConfigException {
		String expression = (String) props.getProperty(pExpression);
		int dimensions = (Integer) props.getProperty(DIMENSION_STR);

		Function function;
		try {
			function = interpreter.parse(expression, dimensions);
		} catch (Exception e) {
			throw new ConfigException(e.getMessage());
		}

		return function;
	}

	@Override
	protected Function createFunction(PropertySet props) throws ConfigException {
		return parseFunction(props);
	}

	@Override
	public void configure(JDialog parent) {
		if (configurer == null)
			configurer = new InterpreterFunctionConfigurer(this, parent,
					interpreter);
		try {
			configurer.configureAndWait();
		} catch (ConfigException e) {
			e.defaultHandleBehavior();
		}

	}

	public PropertyDescriptor[] getConfigSchema() {
		String expression = null;
		int dim = myInputDimensions;

		PostfixFunction function = getFunction();

		if (function != null) {
			expression = function.getExpression();

			if (isInputDimEditable)
				dim = function.getDimension();
		}

		pExpression = new PString(EXPRESSION_STR, expression);
		PropertyDescriptor pDimensions = new PInt(DIMENSION_STR, dim);

		if (isInputDimEditable) {

			pDimensions.setEditable(true);
		}

		PropertyDescriptor[] props = new PropertyDescriptor[] { pExpression,
				pDimensions };
		return props;
	}

	@Override
	public PostfixFunction getFunction() {
		return (PostfixFunction) super.getFunction();
	}

	@Override
	public void preConfiguration(PropertySet props) throws ConfigException {
		/*
		 * Try to parse the expression and throw an exception if it dosen't
		 * succeed
		 */
		parseFunction(props);
	}

	/**
	 * Property descriptor for a function expression.
	 * 
	 * @author Shu Wu
	 */
	class PExpression extends PString {
		private static final long serialVersionUID = 1L;

		public PExpression(String name) {
			super(name);
		}

		@Override
		protected PropertyInputPanel createInputPanel() {
			/*
			 * This custom String Panel will check that the expression is
			 * correct before returning that value is set. This allows a UI to
			 */

			return new StringPanel(this) {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isValueSet() {
					return super.isValueSet();
				}

			};
		}

	}
}

/**
 * This Configurer uses a custom panel for registering new functions
 * 
 * @author Shu Wu
 */
class InterpreterFunctionConfigurer extends UserConfigurer {
	FunctionInterpreter interpreter;
	Dialog parent;

	public InterpreterFunctionConfigurer(IConfigurable configurable,
			Dialog parent, FunctionInterpreter interpreter) {
		super(configurable, parent);
		this.interpreter = interpreter;
		this.parent = parent;
	}

	@Override
	protected ConfigDialog createConfigDialog() {
		return new FunctionDialog(this, parent);
	}

	/**
	 * This config dialog contains additional elements for configuring
	 * registered functions
	 * 
	 * @author Shu Wu
	 */
	class FunctionDialog extends ConfigDialog {

		private static final long serialVersionUID = 1L;

		private JComboBox registeredFunctionsList;

		public FunctionDialog(UserConfigurer configManager, Dialog owner) {
			super(configManager, owner);

		}

		@Override
		protected void initPanelBottom(JPanel panel) {

			JPanel savedFilesPanel = new JCustomPanel();

			JPanel dropDownPanel = new JCustomPanel();

			Map<String, Function> reigsteredFunctions = interpreter
					.getRegisteredFunctions();

			registeredFunctionsList = new JComboBox(reigsteredFunctions
					.keySet().toArray());

			savedFilesPanel.add(new JLabel("Registered Functions"));

			dropDownPanel.add(registeredFunctionsList);
			dropDownPanel
					.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			savedFilesPanel.add(dropDownPanel);

			JPanel buttonsPanel = new JCustomPanel();
			buttonsPanel
					.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
			buttonsPanel.add(Box.createHorizontalGlue());
			buttonsPanel
					.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 5));

			JButton button;
			button = new JButton("New");
			button.addActionListener(new NewFunctionAL());
			button.setFont(Style.FONT_SMALL);
			buttonsPanel.add(button);

			button = new JButton("Remove");
			button.addActionListener(new RemoveFunctionAL());
			button.setFont(Style.FONT_SMALL);
			buttonsPanel.add(button);

			button = new JButton("Preview");
			button.addActionListener(new PreviewFunctionAL());
			button.setFont(Style.FONT_SMALL);
			buttonsPanel.add(button);

			savedFilesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10,
					10, 10));

			savedFilesPanel.add(buttonsPanel);

			JPanel wrapperPanel = new JCustomPanel();
			wrapperPanel.setBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.LOWERED));
			wrapperPanel.add(savedFilesPanel);

			JPanel seperator = new JCustomPanel();
			seperator.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

			panel.add(wrapperPanel);
			panel.add(seperator);
		}

		class NewFunctionAL implements ActionListener {

			public void actionPerformed(ActionEvent e) {
				try {
					PString pFnName = new PString("Name");
					PFunction pFunction = new PFunction("New Function", 1,
							true, null);

					PropertySet props = ConfigManager.configure(
							new PropertyDescriptor[] { pFnName, pFunction },
							"Register fuction", FunctionDialog.this,
							ConfigMode.TEMPLATE_NOT_CHOOSABLE);

					String name = (String) props.getProperty(pFnName);
					Function fn = (Function) props.getProperty(pFunction);

					interpreter.registerFunction(name, fn);
					registeredFunctionsList.addItem(name);
				} catch (ConfigException e1) {
					e1.defaultHandleBehavior();
				}
			}
		}

		class PreviewFunctionAL implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				String functionName = (String) registeredFunctionsList
						.getSelectedItem();

				if (functionName != null) {
					Function function = interpreter.getRegisteredFunctions()
							.get(functionName);

					if (function != null) {
						PlotFunctionAction action = new PlotFunctionAction(
								"Function preview", function,
								FunctionDialog.this);
						action.doAction();

					} else {
						UserMessages.showWarning("No function selected");
					}
				}
			}
		}

		class RemoveFunctionAL implements ActionListener {

			public void actionPerformed(ActionEvent e) {
				String functionName = (String) registeredFunctionsList
						.getSelectedItem();
				if (functionName != null) {
					interpreter.removeRegisteredFunction(functionName);
					registeredFunctionsList.removeItem(functionName);
				} else {
					UserMessages.showWarning("No function selected");
				}
			}

		}
	}

}

/**
 * A JPanel which has some commonly used settings
 * 
 * @author Shu
 */
class JCustomPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public JCustomPanel() {
		super();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentY(TOP_ALIGNMENT);
		setAlignmentX(LEFT_ALIGNMENT);
	}

}

//
// /**
// * Used to allow the user to configure a new function to be registered in the
// * function interpreter.
// *
// * @author Shu Wu
// */
// public class NewFunction extends PropertyDescriptor {
//
// @Override
// protected PropertyInputPanel createInputPanel() {
// //
// return null;
// }
//
// @Override
// public Class getTypeClass() {
// //
// return Function.class;
// }
//
// @Override
// public String getTypeName() {
// //
// return null;
// }
//
// }

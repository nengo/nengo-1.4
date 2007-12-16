package ca.neo.ui.models.nodes.widgets;

import java.util.List;

import javax.swing.JComboBox;

import ca.neo.math.Function;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.StructuralException;
import ca.neo.model.impl.AbstractEnsemble;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertyInputPanel;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.descriptors.PFunctionArray;
import ca.neo.ui.configurable.descriptors.PString;
import ca.neo.ui.models.nodes.UINEFEnsemble;

/**
 * UI Wrapper for a Decoded Termination
 * 
 * @author Shu Wu
 */
public class UIDecodedOrigin extends UIOrigin {

	private static final PropertyDescriptor pName = new PString("Name");

	private static final long serialVersionUID = 1L;

	private static final String typeName = "Decoded Origin";
	private int inputDimensions;

	private PropertyDescriptor pFunctions;

	private PropertyDescriptor pNodeOrigin;

	public UIDecodedOrigin(UINEFEnsemble ensembleProxy) {
		super(ensembleProxy);
		this.inputDimensions = ensembleProxy.getModel().getDimension();
	}

	@Override
	protected Object configureModel(PropertySet configuredProperties)
			throws ConfigException {
		Origin origin = null;

		try {
			origin = getNodeParent().getModel().addDecodedOrigin(
					(String) configuredProperties.getProperty(pName),
					(Function[]) configuredProperties.getProperty(pFunctions),
					(String) configuredProperties.getProperty(pNodeOrigin));

			getNodeParent().showPopupMessage(
					"New decoded origin added to ensemble");

			setName(origin.getName());
		} catch (StructuralException e) {
			throw new ConfigException(e.getMessage());
		}

		return origin;
	}

	protected int getInputDimensions() {
		return inputDimensions;
	}

	@Override
	protected void prepareForDestroy() {
		if (isModelExists()) {
			getNodeParent().getModel().removeDecodedTermination(
					getModel().getName());
			showPopupMessage("decoded termination removed from ensemble");
		}

		super.prepareForDestroy();
	}

	@Override
	public PropertyDescriptor[] getConfigSchema() {
		pFunctions = new PFunctionArray("Functions", getInputDimensions());

		// Find common nodes
		Node[] nodes = getNodeParent().getModel().getNodes();
		List<String> commonNodes = AbstractEnsemble.findCommon1DOrigins(nodes);

		pNodeOrigin = new OriginSelector("Node Origin Name", commonNodes
				.toArray(new String[0]));

		PropertyDescriptor[] zProperties = { pName, pFunctions, pNodeOrigin };
		return zProperties;

	}

	@Override
	public UINEFEnsemble getNodeParent() {
		return (UINEFEnsemble) super.getNodeParent();
	}

	@Override
	public String getTypeName() {
		return typeName;
	}

}

/**
 * Swing component for selecting a origin
 * 
 * @author Shu Wu
 */
class OriginInputPanel extends PropertyInputPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Selector of the Node Origin
	 */
	private JComboBox comboBox;

	String[] origins;

	public OriginInputPanel(OriginSelector property, String[] originNames) {
		super(property);
		this.origins = originNames;

		comboBox = new JComboBox(origins);
		addToPanel(comboBox);
	}

	@Override
	public String getValue() {
		return (String) comboBox.getSelectedItem();
	}

	@Override
	public boolean isValueSet() {
		return true;
	}

	@Override
	public void setValue(Object value) {
		if (value != null && value instanceof String) {
			for (int i = 0; i < comboBox.getItemCount(); i++) {
				String item = (String) comboBox.getItemAt(i);

				if (item.compareTo((String) value) == 0) {
					comboBox.setSelectedIndex(i);
					return;
				}

			}
		}
	}

}

/**
 * Selects an available Node Origin
 * 
 * @author Shu Wu
 */
class OriginSelector extends PropertyDescriptor {

	private static final long serialVersionUID = 1L;
	String[] origins;

	public OriginSelector(String name, String[] originNames) {
		super(name);
		this.origins = originNames;
	}

	@Override
	protected OriginInputPanel createInputPanel() {
		return new OriginInputPanel(this, origins);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class getTypeClass() {

		return String.class;
	}

	@Override
	public String getTypeName() {
		return "Node Origin Selector";
	}

}

package ca.nengo.ui.models.constructors;

import java.util.List;

import javax.swing.JComboBox;

import ca.nengo.math.Function;
import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.StructuralException;
import ca.nengo.model.impl.AbstractEnsemble;
import ca.nengo.model.nef.NEFEnsemble;
import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.ConfigResult;
import ca.nengo.ui.configurable.ConfigSchema;
import ca.nengo.ui.configurable.ConfigSchemaImpl;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.PropertyInputPanel;
import ca.nengo.ui.configurable.descriptors.PFunctionArray;
import ca.nengo.ui.configurable.descriptors.PString;
import ca.nengo.ui.models.nodes.widgets.UIDecodedOrigin;

public class CDecodedOrigin extends AbstractConstructable {
	private static final Property pName = new PString("Name");

	private NEFEnsemble enfEnsembleParent;

	private Property pFunctions;

	private Property pNodeOrigin;

	public CDecodedOrigin(NEFEnsemble enfEnsembleParent) {
		super();
		this.enfEnsembleParent = enfEnsembleParent;
	}

	@Override
	protected Object configureModel(ConfigResult configuredProperties) throws ConfigException {
		Origin origin = null;

		try {
			origin = enfEnsembleParent.addDecodedOrigin((String) configuredProperties
					.getValue(pName), (Function[]) configuredProperties.getValue(pFunctions),
					(String) configuredProperties.getValue(pNodeOrigin));

		} catch (StructuralException e) {
			throw new ConfigException(e.getMessage());
		}

		return origin;
	}

	@Override
	public ConfigSchema getSchema() {
		pFunctions = new PFunctionArray("Functions", enfEnsembleParent.getDimension());

		// Find common nodes
		Node[] nodes = enfEnsembleParent.getNodes();
		List<String> commonNodes = AbstractEnsemble.findCommon1DOrigins(nodes);

		pNodeOrigin = new OriginSelector("Node Origin Name", commonNodes.toArray(new String[0]));

		Property[] zProperties = { pName, pFunctions, pNodeOrigin };
		return new ConfigSchemaImpl(zProperties);

	}

	public String getTypeName() {
		return UIDecodedOrigin.typeName;
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
		add(comboBox);
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
class OriginSelector extends Property {

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

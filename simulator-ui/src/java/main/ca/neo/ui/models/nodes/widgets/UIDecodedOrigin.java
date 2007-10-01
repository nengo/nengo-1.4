package ca.neo.ui.models.nodes.widgets;

import javax.swing.JComboBox;

import ca.neo.math.Function;
import ca.neo.model.Origin;
import ca.neo.model.StructuralException;
import ca.neo.ui.configurable.ConfigParam;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.ConfigParamInputPanel;
import ca.neo.ui.configurable.descriptors.CFunctionArray;
import ca.neo.ui.configurable.descriptors.CString;
import ca.neo.ui.models.nodes.UINEFEnsemble;

/**
 * UI Wrapper for a Decoded Termination
 * 
 * @author Shu Wu
 */
public class UIDecodedOrigin extends UIOrigin {

	private static final ConfigParamDescriptor pName = new CString("Name");

	private ConfigParamDescriptor pNodeOrigin;

	private static final long serialVersionUID = 1L;
	private static final String typeName = "Decoded Origin";

	private ConfigParamDescriptor pFunctions;

	public UIDecodedOrigin(UINEFEnsemble ensembleProxy) {
		super(ensembleProxy);
	}

	@Override
	protected Object configureModel(ConfigParam configuredProperties) {
		Origin origin = null;

		try {
			origin = getNodeParent().getModel().addDecodedOrigin(
					(String) configuredProperties.getProperty(pName),
					(Function[]) configuredProperties.getProperty(pFunctions),
					(String) configuredProperties.getProperty(pNodeOrigin));

			getNodeParent().popupTransientMsg(
					"New decoded origin added to ensemble");

			setName(origin.getName());
		} catch (StructuralException e) {
			e.printStackTrace();
		}

		return origin;
	}

	@Override
	protected void prepareForDestroy() {
		getNodeParent().getModel().removeDecodedTermination(
				getModel().getName());
		popupTransientMsg("decoded termination removed from ensemble");

		super.prepareForDestroy();
	}

	@Override
	public ConfigParamDescriptor[] getConfigSchema() {
		pFunctions = new CFunctionArray("Functions");

		Origin[] origins = getNodeParent().getModel().getOrigins();
		String[] originNames = new String[origins.length];
		for (int i = 0; i < origins.length; i++) {
			originNames[i] = origins[i].getName();
		}

		pNodeOrigin = new OriginSelector("Node Origin Name", originNames);

		ConfigParamDescriptor[] zProperties = { pName, pFunctions, pNodeOrigin };
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
class OriginInputPanel extends ConfigParamInputPanel {
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
class OriginSelector extends ConfigParamDescriptor {

	private static final long serialVersionUID = 1L;
	String[] origins;

	public OriginSelector(String name, String[] originNames) {
		super(name);
		this.origins = originNames;
	}

	@Override
	public OriginInputPanel createInputPanel() {
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

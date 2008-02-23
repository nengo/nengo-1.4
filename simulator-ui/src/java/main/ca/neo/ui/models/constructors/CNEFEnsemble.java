package ca.neo.ui.models.constructors;

import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFEnsembleFactory;
import ca.neo.model.nef.impl.NEFEnsembleFactoryImpl;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.descriptors.PInt;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.nodes.UINEFEnsemble;

public class CNEFEnsemble extends ConstructableNode {
	public CNEFEnsemble(INodeContainer nodeContainer) {
		super(nodeContainer);
	}

	static final PropertyDescriptor pDim = new PInt("Dimensions");

	static final PropertyDescriptor pNumOfNeurons = new PInt("Number of Neurons");

	/**
	 * Config descriptors
	 */
	static final PropertyDescriptor[] zConfig = { pNumOfNeurons, pDim };

	protected Node createNode(PropertySet prop, String name) {
		try {

			NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();

			Integer numOfNeurons = (Integer) prop.getProperty(pNumOfNeurons);
			Integer dimensions = (Integer) prop.getProperty(pDim);

			NEFEnsemble ensemble = ef.make(name, numOfNeurons, dimensions);

			return ensemble;
		} catch (StructuralException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public PropertyDescriptor[] getNodeConfigSchema() {
		return zConfig;
	}

	public String getTypeName() {
		return UINEFEnsemble.typeName;
	}

}

package ca.neo.ui.models.constructors;

import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFEnsembleFactory;
import ca.neo.model.nef.impl.NEFEnsembleFactoryImpl;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.descriptors.PInt;
import ca.neo.ui.configurable.descriptors.PString;
import ca.neo.ui.models.nodes.UINEFEnsemble;

public class CNEFEnsemble extends Constructable {
	static final PropertyDescriptor pDim = new PInt("Dimensions");

	static final PropertyDescriptor pName = new PString("Name");

	static final PropertyDescriptor pNumOfNeurons = new PInt("Number of Neurons");

	static final PropertyDescriptor pStorageName = new PString("Storage Name");
	/**
	 * Config descriptors
	 */
	static final PropertyDescriptor[] zConfig = { pName, pNumOfNeurons, pDim, pStorageName };

	@Override
	protected Node configureModel(PropertySet prop) {
		try {

			NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();

			String name = (String) prop.getProperty(pName);

			Integer numOfNeurons = (Integer) prop.getProperty(pNumOfNeurons);
			Integer dimensions = (Integer) prop.getProperty(pDim);
			String storageName = (String) prop.getProperty(pStorageName);

			NEFEnsemble ensemble = ef.make(name, numOfNeurons, dimensions, storageName, false);

			return ensemble;
		} catch (StructuralException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public PropertyDescriptor[] getConfigSchema() {
		return zConfig;
	}

	public String getTypeName() {
		return UINEFEnsemble.typeName;
	}
}

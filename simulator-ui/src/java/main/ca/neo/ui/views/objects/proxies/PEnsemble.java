package ca.neo.ui.views.objects.proxies;

import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.nef.NEFEnsembleFactory;
import ca.neo.model.nef.impl.NEFEnsembleFactoryImpl;
import ca.neo.ui.views.objects.properties.PTInt;
import ca.neo.ui.views.objects.properties.PTString;
import ca.neo.ui.views.objects.properties.PropertySchema;

public class PEnsemble extends ProxyNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String name = "Unamed Ensemble";

	String storageName = "Unamed Ensemble";

	static String pName = "Name";

	static String pNumOfNeurons = "Number of Neurons";

	static String pDim = "Dimensions";

	static String pStorageName = "Storage Name";

	static PropertySchema[] metaProperties = {
			new PTString(pName),
			new PTInt(pNumOfNeurons),
			new PTInt(pDim),
			new PTString(pStorageName)

	};

	@Override
	protected Node createProxy() {
		// TODO Auto-generated method stub
		try {
			NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();

			String name = (String) getProperty(pName);
			Integer numOfNeurons = (Integer) getProperty(pNumOfNeurons);
			Integer dimensions = (Integer) getProperty(pDim);
			String storageName = (String) getProperty(pStorageName);

			return ef.make(name, numOfNeurons, dimensions, storageName, false);
		} catch (StructuralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}



//	@Override
//	public void initProxy0() {
//		// TODO Auto-generated method stub
//		super.initProxy0();
//
//		proxy.getOrigins();
//
//	}

	@Override
	public PropertySchema[] getMetaProperties() {
		// TODO Auto-generated method stub
		return metaProperties;
	}

}

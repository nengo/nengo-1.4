package ca.neo.ui.views.objects;

import ca.neo.model.Ensemble;
import ca.neo.model.StructuralException;
import ca.neo.model.nef.NEFEnsembleFactory;
import ca.neo.model.nef.impl.NEFEnsembleFactoryImpl;

public class EnsembleProxy extends ProxyObject<Ensemble> {

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

	static MetaProperty[] metaProperties = {
			new MetaProperty(pName, String.class),
			new MetaProperty(pNumOfNeurons, Integer.class),
			new MetaProperty(pDim, Integer.class),
			new MetaProperty(pStorageName, String.class)

	};

	@Override
	protected Ensemble createProxy() {
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



	@Override
	public void initProxy0() {
		// TODO Auto-generated method stub
		super.initProxy0();

		proxy.getOrigins();

	}

	@Override
	protected MetaProperty[] getMetaProperties() {
		// TODO Auto-generated method stub
		return metaProperties;
	}

}

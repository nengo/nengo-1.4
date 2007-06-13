package ca.neo.ui.views.objects.proxies;

import ca.neo.model.Node;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.ui.views.objects.properties.PropertySchema;

public class PNetwork extends ProxyNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	static MetaProperty[] metaProperties = {
//			new MetaProperty("Name", String.class),
//			new MetaProperty("Number of Neurons", Integer.class),
//			new MetaProperty("Dimensions", Integer.class),
//			new MetaProperty("Storage Name", Integer.class)
//
//	};

	public int i = 4;

	@Override
	protected Node createProxy() {
		// TODO Auto-generated method stub
		return new NetworkImpl();
	}

	@Override
	public PropertySchema[] getMetaProperties() {
		return null;
	}


	
	



}

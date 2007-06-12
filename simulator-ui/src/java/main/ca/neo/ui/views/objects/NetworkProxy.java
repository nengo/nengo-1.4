package ca.neo.ui.views.objects;

import ca.neo.model.Network;
import ca.neo.model.impl.NetworkImpl;

public class NetworkProxy extends ProxyObject<Network> {

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
	protected Network createProxy() {
		// TODO Auto-generated method stub
		return new NetworkImpl();
	}

	@Override
	protected MetaProperty[] getMetaProperties() {
		return null;
	}

	
	



}

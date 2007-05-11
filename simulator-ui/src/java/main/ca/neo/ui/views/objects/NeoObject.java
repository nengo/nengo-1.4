package ca.neo.ui.views.objects;

import ca.sw.graphics.nodes.WorldObject;

public abstract class NeoObject<E> extends WorldObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected E proxy;
	
	
	public NeoObject() {
		super();
		
		proxy = createProxy();
	}


	public E getProxy() {
		return proxy;
	}
	

	public abstract E createProxy();
	
	
}

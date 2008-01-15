package ca.shu.ui.lib.world.piccolo.objects;

import ca.shu.ui.lib.world.IWorldObject;
import ca.shu.ui.lib.world.piccolo.WorldObjectImpl;

/**
 * A World Object which does nothing but wrap another world object to add a
 * layer of indirection.
 * 
 * @author Shu Wu
 */
public class Wrapper extends WorldObjectImpl {
	private IWorldObject myPackage;

	public Wrapper(IWorldObject obj) {
		super();
		setPickable(false);
		setSelectable(false);
		setPackage(obj);
	}

	public IWorldObject getPackage() {
		return myPackage;
	}

	public void setPackage(IWorldObject obj) {
		if (getPackage() != null) {
			removeChild(getPackage());
		}

		addChild(obj);
		this.myPackage = obj;
	}
}

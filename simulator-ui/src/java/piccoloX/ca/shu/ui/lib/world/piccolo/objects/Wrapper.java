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

	public final void setPackage(IWorldObject obj) {
		if (myPackage != null) {
			myPackage.removeFromParent();
		}
		IWorldObject oldPackage = myPackage;

		myPackage = obj;

		if (obj != null) {
			addChild(obj);
			packageChanged(oldPackage);
		}
	}

	public void destroyPackage() {
		if (myPackage != null) {
			myPackage.destroy();
			myPackage = null;
		}
	}

	protected void packageChanged(IWorldObject oldPackage) {

	}

}

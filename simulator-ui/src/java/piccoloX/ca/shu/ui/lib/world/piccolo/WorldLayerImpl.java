package ca.shu.ui.lib.world.piccolo;

import java.util.Collection;
import java.util.LinkedList;

import ca.shu.ui.lib.world.WorldLayer;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.piccolo.objects.Window;
import ca.shu.ui.lib.world.piccolo.primitives.PiccoloNodeInWorld;

public abstract class WorldLayerImpl extends WorldObjectImpl implements WorldLayer {

	/**
	 * World this layer belongs to
	 */
	protected WorldImpl world;

	/**
	 * Create a new ground layer
	 * 
	 * @param world
	 *            World this layer belongs to
	 */
	public WorldLayerImpl(String name, PiccoloNodeInWorld node) {
		super(name, node);
	}

	public Collection<Window> getWindows() {
		LinkedList<Window> windows = new LinkedList<Window>();
		for (WorldObject wo : getChildren()) {
			if (wo instanceof Window) {
				windows.add((Window) wo);
			}
		}
		return windows;
	}

	/**
	 * Removes and destroys children
	 */
	public void clearLayer() {
		for (WorldObject wo : getChildren()) {
			wo.destroy();
		}
	}

	@Override
	public WorldImpl getWorld() {
		return world;
	}

	public void setWorld(WorldImpl world) {
		this.world = world;
	}

}

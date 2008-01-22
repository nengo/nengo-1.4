package ca.shu.ui.lib.world.piccolo.primitives;

import ca.shu.ui.lib.world.WorldObject;

public interface PiccoloNodeInWorld {
	public WorldObject getWorldObject();

	public boolean isAnimating();

	public void setWorldObject(WorldObject worldObjectParent);
}

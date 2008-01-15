package ca.shu.ui.lib.world.piccolo.primitives;

import ca.shu.ui.lib.world.IWorldObject;

public interface PiccoloNodeInWorld {
	public IWorldObject getWorldObjectParent();

	public boolean isAnimating();

	public void setWorldObjectParent(IWorldObject worldObjectParent);
}

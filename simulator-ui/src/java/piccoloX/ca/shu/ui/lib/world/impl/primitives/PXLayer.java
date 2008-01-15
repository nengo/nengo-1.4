package ca.shu.ui.lib.world.piccolo.primitives;

import ca.shu.ui.lib.world.IWorldObject;
import edu.umd.cs.piccolo.PLayer;

public class PXLayer extends PLayer implements PiccoloNodeInWorld {

	private static final long serialVersionUID = 1L;

	private IWorldObject wo;

	public IWorldObject getWorldObjectParent() {
		return wo;
	}

	public boolean isAnimating() {
		return false;
	}

	public void setWorldObjectParent(IWorldObject worldObjectParent) {
		wo = worldObjectParent;

	}

}

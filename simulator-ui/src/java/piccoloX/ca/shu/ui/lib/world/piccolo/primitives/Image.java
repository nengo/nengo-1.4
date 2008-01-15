package ca.shu.ui.lib.world.piccolo.primitives;

import java.net.URL;

import ca.shu.ui.lib.world.piccolo.WorldObjectImpl;

public class Image extends WorldObjectImpl {
	private PXImage imageNode;

	public Image(URL url) {
		super(new PXImage(url));
		init();
	}

	public Image(String fileName) {
		super(new PXImage(fileName));
		init();
	}

	public boolean isLoadedSuccessfully() {

		if (imageNode.getImage() != null) {
			return true;
		} else {
			return false;
		}

	}

	public void init() {
		imageNode = (PXImage) getPiccolo();
		setPickable(false);
		setSelectable(false);
	}

}

package ca.shu.ui.lib.world.piccolo.primitives;

import java.net.URL;

import ca.shu.ui.lib.world.piccolo.WorldObjectImpl;

public class Image extends WorldObjectImpl {

	public Image(URL url) {
		super(new PXImage(url));
		setPickable(false);
		setSelectable(false);
	}

	public Image(String fileName) {
		super(new PXImage(fileName));
	}

}

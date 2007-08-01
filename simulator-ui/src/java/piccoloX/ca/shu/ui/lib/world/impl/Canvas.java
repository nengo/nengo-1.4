package ca.shu.ui.lib.world.impl;

import ca.shu.ui.lib.world.World;
import edu.umd.cs.piccolo.PCanvas;

public class Canvas extends PCanvas {

	private static final long serialVersionUID = 1L;

	static final double CLICK_ZOOM_PADDING = 100;

	WorldImpl world;

	public Canvas(GFrame frame) {
		super();

		setZoomEventHandler(null);
		setPanEventHandler(null);

		world = new WorldImpl("Top Layer", getRoot());
		
		getLayer().addChild(world);
		world.setBounds(0,0,500,500);
//		getLayer().addChild((MiniWorld) world);
	}

	public WorldImpl getWorld() {
		return world;
	}

	@Override
	public void setBounds(int x, int y, int w, int h) {
		world.setBounds(world.getX(), world.getY(), w, h);
		
		// TODO Auto-generated method stub
		super.setBounds(x, y, w, h);
		
		
		
		
		
	}

}

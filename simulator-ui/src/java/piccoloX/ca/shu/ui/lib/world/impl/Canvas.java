package ca.shu.ui.lib.world.impl;

import ca.shu.ui.lib.world.IWorld;
import edu.umd.cs.piccolo.PCanvas;

public class Canvas extends PCanvas {

	private static final long serialVersionUID = 1L;

	static final double CLICK_ZOOM_PADDING = 100;

	World world;

	public Canvas(Frame frame) {
		super();

		setZoomEventHandler(null);
		setPanEventHandler(null);

		world = new World("Top Layer", getRoot());
		
		getLayer().addChild(world);
		world.setBounds(0,0,500,500);
//		getLayer().addChild((MiniWorld) world);
	}

	public World getWorld() {
		return world;
	}

	@Override
	public void setBounds(int x, int y, int w, int h) {
		world.setBounds(world.getX(), world.getY(), w, h);
		
		// TODO Auto-generated method stub
		super.setBounds(x, y, w, h);
		
		
		
		
		
	}

}

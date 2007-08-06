package ca.shu.ui.lib.world.impl;

import java.util.List;
import java.util.Stack;

import ca.shu.ui.lib.objects.Window;
import ca.shu.ui.lib.world.World;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;

/**
 * Holder of worlds
 * 
 * @author Shu Wu
 * 
 */
public class GCanvas extends PCanvas {

	private static final long serialVersionUID = 1L;

	static final double CLICK_ZOOM_PADDING = 100;

	WorldImpl world;

	Stack<World> miniWorlds = new Stack<World>();

	public GCanvas(GFrame frame) {
		super();

		setZoomEventHandler(null);
		setPanEventHandler(null);

		// world.setBounds(0, 0, 500, 500);
		// getLayer().addChild((MiniWorld) world);
	}

	public void createWorld() {
		world = new WorldImpl("Top Layer");
		getLayer().addChild((WorldImpl) world);
	}

	public WorldImpl getWorld() {
		return world;
	}

	public void addWindow(Window wo) {

		getLayer().addChild(wo);
	}

	@Override
	public void setBounds(int x, int y, int w, int h) {

		PLayer layer = getLayer();
		layer.setBounds(layer.getX(), layer.getY(), w, h);
		world.setBounds(world.getX(), world.getY(), w, h);
		// TODO Auto-generated method stub
		super.setBounds(x, y, w, h);

	}

}

package ca.shu.ui.lib.world;

import java.util.Stack;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;

/**
 * Holder of worlds
 * 
 * @author Shu Wu
 * 
 */
public class GCanvas extends PCanvas {

	private static final long serialVersionUID = 1L;

	static final double CLICK_ZOOM_PADDING = 100;

	World world;

	Stack<World> miniWorlds = new Stack<World>();

	public GCanvas(GFrame frame) {
		super();

		setZoomEventHandler(null);
		setPanEventHandler(null);

		// world.setBounds(0, 0, 500, 500);
		// getLayer().addChild((MiniWorld) world);
	}

	public void createWorld() {
		world = new World("Top Layer");
		getLayer().addChild((World) world);
	}

	public World getWorld() {
		return world;
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

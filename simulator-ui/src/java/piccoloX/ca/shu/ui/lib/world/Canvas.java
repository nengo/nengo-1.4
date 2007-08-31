package ca.shu.ui.lib.world;

import java.util.Collection;
import java.util.Vector;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;

/**
 * Holder of worlds
 * 
 * @author Shu Wu
 */
public class Canvas extends PCanvas {

	private static final long serialVersionUID = 1L;

	public static final String SELECTION_MODE_NOTIFICATION = "canvasSelectionMode";

	static final double CLICK_ZOOM_PADDING = 100;

	private World topWorld;

	private Collection<World> worlds;

	public Canvas() {
		super();

		setZoomEventHandler(null);
		setPanEventHandler(null);

		worlds = new Vector<World>(5);

	}

	protected Collection<World> getWorlds() {
		return worlds;
	}

	public void addWorld(World world) {
		worlds.add(world);
	}

	// GText interactionModeLabel;

	public void createWorld() {
		topWorld = new World("Top Layer");
		getLayer().addChild(topWorld);

	}

	public World getWorld() {
		return topWorld;
	}

	public void removeWorld(World world) {
		worlds.remove(world);
	}

	@Override
	public void setBounds(int x, int y, int w, int h) {

		PLayer layer = getLayer();
		layer.setBounds(layer.getX(), layer.getY(), w, h);
		topWorld.setBounds(topWorld.getX(), topWorld.getY(), w, h);

		super.setBounds(x, y, w, h);

	}

}

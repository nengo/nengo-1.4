package ca.shu.ui.lib.world.piccolo.primitives;

import java.util.Collection;
import java.util.Vector;

import ca.shu.ui.lib.world.elastic.ElasticWorld;
import ca.shu.ui.lib.world.piccolo.WorldImpl;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;

/**
 * Holder of worlds
 * 
 * @author Shu Wu
 */
public class Universe extends PCanvas {

	private static final long serialVersionUID = 1L;

	public static final String SELECTION_MODE_NOTIFICATION = "canvasSelectionMode";

	static final double CLICK_ZOOM_PADDING = 100;

	private ElasticWorld topWorld;

	private Collection<WorldImpl> worlds;

	public Universe() {
		super();

		setZoomEventHandler(null);
		setPanEventHandler(null);

		worlds = new Vector<WorldImpl>(5);
	}

	/**
	 * @param world
	 *            World to be the background for it all
	 */
	public void setBackgroundWorld(ElasticWorld world) {
		topWorld = world;
		getLayer().addChild(topWorld.getPiccolo());
	}

	public void addWorld(WorldImpl world) {
		worlds.add(world);
	}

	public ElasticWorld getWorld() {
		return topWorld;
	}

	public Collection<WorldImpl> getWorlds() {
		return worlds;
	}

	public void removeWorld(WorldImpl world) {
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

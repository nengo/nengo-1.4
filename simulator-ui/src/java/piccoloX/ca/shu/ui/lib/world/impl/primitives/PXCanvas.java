package ca.shu.ui.lib.world.piccolo.primitives;

import java.util.Collection;
import java.util.Vector;

import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.world.IWorldObject;
import ca.shu.ui.lib.world.elastic.ElasticGround;
import ca.shu.ui.lib.world.elastic.ElasticWorld;
import ca.shu.ui.lib.world.piccolo.WorldImpl;
import ca.shu.ui.lib.world.piccolo.WorldGroundImpl;
import ca.shu.ui.lib.world.piccolo.WorldSkyImpl;
import ca.shu.ui.lib.world.piccolo.objects.Window;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;

/**
 * Holder of worlds
 * 
 * @author Shu Wu
 */
public class PXCanvas extends PCanvas {

	private static final long serialVersionUID = 1L;

	public static final String SELECTION_MODE_NOTIFICATION = "canvasSelectionMode";

	static final double CLICK_ZOOM_PADDING = 100;

	private ElasticWorld topWorld;

	private Collection<WorldImpl> worlds;
	private ElasticGround worldGround;

	public PXCanvas(ElasticGround worldGround) {
		super();

		setZoomEventHandler(null);
		setPanEventHandler(null);
		this.worldGround = worldGround;
		worlds = new Vector<WorldImpl>(5);

	}

	public void createWorld() {
		topWorld = new ElasticWorld("Top World", new CanvasSky(), worldGround);
		getLayer().addChild(topWorld.getPiccolo());
	}

	public void addWindow(Window window) {
		getWorld().getSky().addChild(window);
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

class CanvasSky extends WorldSkyImpl {

	private static final long serialVersionUID = 1L;

	public CanvasSky() {
		super();
	}

	@Override
	public void addChild(IWorldObject wo, int index) {
		super.addChild(wo, index);
		if (wo instanceof Window) {
			setActiveWindow((Window) wo);
		}
	}

	private static void setActiveWindow(Window window) {
		UIEnvironment.getInstance().setTitle(
				window.getName() + " - "
						+ UIEnvironment.getInstance().getAppWindowTitle());
	}

	@Override
	public void removeChild(IWorldObject wo) {
		super.removeChild(wo);

		/*
		 * Updates the window title with the next Window
		 */
		for (IWorldObject child : getChildren()) {
			if (child instanceof Window) {
				setActiveWindow((Window) child);
			}
		}

		UIEnvironment.getInstance().setTitle(
				UIEnvironment.getInstance().getAppWindowTitle());

	}

}

class CanvasWorld extends ElasticWorld {
	private static final long serialVersionUID = 1L;

	WorldGroundImpl worldGround;

	public CanvasWorld(ElasticGround worldGround) {
		super("World Canvas", new CanvasSky(), worldGround);
		this.worldGround = worldGround;
	}
}
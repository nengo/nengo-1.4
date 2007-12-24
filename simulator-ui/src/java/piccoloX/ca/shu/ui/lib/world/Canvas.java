package ca.shu.ui.lib.world;

import java.util.Collection;
import java.util.Vector;

import ca.shu.ui.lib.objects.Window;
import ca.shu.ui.lib.util.UIEnvironment;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;

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
	public void addWindow(Window window) {
		getWorld().getSky().addChild(window);
	}

	public void addWorld(World world) {
		worlds.add(world);
	}

	public void createWorld() {
		topWorld = new CanvasWorld("Top Layer");
		getLayer().addChild(topWorld);
	}

	public World getWorld() {
		return topWorld;
	}

	public Collection<World> getWorlds() {
		return worlds;
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

class CanvasSky extends WorldSky {

	private static final long serialVersionUID = 1L;

	public CanvasSky(World world) {
		super(world);
	}

	@Override
	public void addChild(int index, PNode child) {
		super.addChild(index, child);
		if (child instanceof Window) {
			UIEnvironment.getInstance().setTitle(
					((Window) child).getName() + " - "
							+ UIEnvironment.getInstance().getAppWindowTitle());
		}
	}

	@Override
	public PNode removeChild(int index) {
		PNode rtnValue = super.removeChild(index);
		for (int i = getChildrenCount() - 1; i >= 0; i--) {
			if (getChild(i) instanceof Window) {
				UIEnvironment.getInstance().setTitle(
						((Window) getChild(i)).getName()
								+ " - "
								+ UIEnvironment.getInstance()
										.getAppWindowTitle());
				return rtnValue;
			}
		}

		UIEnvironment.getInstance().setTitle(
				UIEnvironment.getInstance().getAppWindowTitle());

		return rtnValue;
	}

}

class CanvasWorld extends World {
	private static final long serialVersionUID = 1L;

	public CanvasWorld(String name) {
		super(name);
	}

	@Override
	protected CanvasSky createSky() {
		return new CanvasSky(this);
	}

}
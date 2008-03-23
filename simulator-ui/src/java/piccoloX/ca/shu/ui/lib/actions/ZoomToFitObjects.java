package ca.shu.ui.lib.actions;

import java.awt.geom.Rectangle2D;
import java.util.Collection;

import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.piccolo.WorldImpl;

public class ZoomToFitObjects extends StandardAction {

	private static final long serialVersionUID = 1L;
	private WorldImpl world;
	private Collection<WorldObject> objects;

	public ZoomToFitObjects(String actionName, WorldImpl world, Collection<WorldObject> objects) {
		super("Zoom to fit objects", actionName);
		this.world = world;
		this.objects = objects;
	}

	@Override
	protected void action() throws ActionException {
		Rectangle2D bounds = WorldImpl.getObjectBounds(objects);
		world.zoomToBounds(bounds);

	}
}

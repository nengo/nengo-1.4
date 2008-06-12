package ca.shu.ui.lib.actions;

import ca.shu.ui.lib.world.piccolo.WorldImpl;

/**
 * Action to zoom to fit
 * 
 * @author Shu Wu
 */
public class ZoomToFitAction extends StandardAction {

	private static final long serialVersionUID = 1L;

	WorldImpl world;

	public ZoomToFitAction(String actionName, WorldImpl world) {
		super("Zoom to fit", actionName);
		this.world = world;
	}

	@Override
	protected void action() throws ActionException {
		world.zoomToFit();
	}

}
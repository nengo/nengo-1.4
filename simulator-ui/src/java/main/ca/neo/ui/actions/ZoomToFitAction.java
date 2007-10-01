package ca.neo.ui.actions;

import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.world.World;

/**
 * Action to zoom to fit
 * 
 * @author Shu Wu
 */
public class ZoomToFitAction extends StandardAction {

	private static final long serialVersionUID = 1L;

	World world;

	public ZoomToFitAction(String actionName, World world) {
		super("Zoom to fit", actionName);
		this.world = world;
	}

	@Override
	protected void action() throws ActionException {
		world.zoomToFit();
	}

}
package ca.shu.ui.lib.handlers;

import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.world.World;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Handles events which change the Application status bar
 * 
 * @author Shu Wu
 */
public abstract class AbstractStatusHandler extends PBasicInputEventHandler {
	private World world;

	/**
	 * @param world
	 *            World this handler belongs to
	 */
	public AbstractStatusHandler(World world) {
		super();
		this.world = world;
	}

	/**
	 * @return World this handler belongs to
	 */
	protected World getWorld() {
		return world;
	}

	/**
	 * @param event
	 *            Input event
	 * @return Message to show on the status bar
	 */
	protected abstract String getStatusMessage(PInputEvent event);

	@Override
	public void mouseMoved(PInputEvent event) {
		super.mouseMoved(event);

		UIEnvironment.getInstance().setStatusMessage(getStatusMessage(event));

	}
}

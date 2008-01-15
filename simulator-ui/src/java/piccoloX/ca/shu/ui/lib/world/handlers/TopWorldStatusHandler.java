package ca.shu.ui.lib.world.handlers;

import java.text.NumberFormat;

import ca.shu.ui.lib.world.piccolo.WorldImpl;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Shows the mouse coordinates using the status bar
 * 
 * @author Shu Wu
 */
public class TopWorldStatusHandler extends AbstractStatusHandler {

	public TopWorldStatusHandler(WorldImpl world) {
		super(world);
	}

	/**
	 * @param event
	 *            Input event
	 * @return String related to that event
	 */
	@Override
	protected String getStatusMessage(PInputEvent event) {
		NumberFormat formatter = NumberFormat.getNumberInstance();
		formatter.setMaximumFractionDigits(2);
		return "Top Window - Mouse X: "
				+ formatter.format(event.getPosition().getX()) + " Y: "
				+ formatter.format(event.getPosition().getY());
	}

}

package ca.shu.ui.lib.handlers;

import java.text.NumberFormat;

import ca.shu.ui.lib.world.World;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Shows the mouse coordinates using the status bar
 * 
 * @author Shu Wu
 */
public class MouseStatusHandler extends AbstractStatusHandler {

	public MouseStatusHandler(World world) {
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
		return "Mouse X: " + formatter.format(event.getPosition().getX())
				+ " Y: " + formatter.format(event.getPosition().getY());
	}

}

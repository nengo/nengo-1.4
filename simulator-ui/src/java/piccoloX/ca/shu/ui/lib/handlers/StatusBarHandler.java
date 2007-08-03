package ca.shu.ui.lib.handlers;

import java.text.NumberFormat;

import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.world.World;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

public class StatusBarHandler extends PBasicInputEventHandler {
	World world;

	public StatusBarHandler(World world) {
		super();
		this.world = world;
	}

	@Override
	public void mouseMoved(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mouseMoved(event);

		UIEnvironment.getInstance().setStatusStr(getStatusStr(event));

	}

	public String getStatusStr(PInputEvent event) {
//		Point2D position = event.getPositionRelativeTo(world.getSky());
		NumberFormat formatter = NumberFormat.getNumberInstance();
		formatter.setMaximumFractionDigits(2);
		
		return "Mouse X: " + formatter.format(event.getPosition().getX()) + " Y: "
				+ formatter.format(event.getPosition().getY());
	}

	public World getWorld() {
		return world;
	}
}

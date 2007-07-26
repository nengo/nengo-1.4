package ca.shu.ui.lib.handlers;

import java.text.NumberFormat;

import ca.shu.ui.lib.world.IWorld;
import ca.shu.ui.lib.world.impl.Frame;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

public class StatusBarHandler extends PBasicInputEventHandler {
	IWorld world;

	public StatusBarHandler(IWorld world) {
		super();
		this.world = world;
	}

	@Override
	public void mouseMoved(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mouseMoved(event);

		Frame.getInstance().setStatusStr(getStatusStr(event));

	}

	public String getStatusStr(PInputEvent event) {
//		Point2D position = event.getPositionRelativeTo(world.getSky());
		NumberFormat formatter = NumberFormat.getNumberInstance();
		formatter.setMaximumFractionDigits(2);
		
		return "Mouse X: " + formatter.format(event.getPosition().getX()) + " Y: "
				+ formatter.format(event.getPosition().getY());
	}

	public IWorld getWorld() {
		return world;
	}
}

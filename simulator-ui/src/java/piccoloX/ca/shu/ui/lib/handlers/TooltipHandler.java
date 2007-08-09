package ca.shu.ui.lib.handlers;

import java.awt.event.InputEvent;

import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

public class TooltipHandler extends NodePickerHandler {
	private WorldObject controls;

	public TooltipHandler(World parent) {
		super(parent);
	}

	protected int getPickDelay() {
		return 500;
	}

	protected int getKeepPickDelay() {
		return 1500;
	}

	@Override
	protected void nodePicked() {
		WorldObject node = getPickedNode();
		node.pushState(WorldObject.State.SELECTED);
		controls = node.getTooltip();

		getWorld().showTooltip(controls, node);
	}

	@Override
	protected void nodeUnPicked() {
		WorldObject node = getPickedNode();

		node.popState(WorldObject.State.SELECTED);
		getWorld().hideTooltip();

	}

	@Override
	public void eventUpdated(PInputEvent event) {

		if (World.isTooltipsVisible()
				|| ((event.getModifiers() & InputEvent.CTRL_MASK) != 0)) {

			PNode node = event.getPickedNode();
			while (node != null) {

				/*
				 * Do nothing if the mouse is over the controls
				 */
				if (node == controls) {
					setKeepPickAlive(true);
					return;
				}
				if (node instanceof WorldObject) {
					WorldObject wo = (WorldObject) node;

					if (wo.getTooltip() != null) {

						setSelectedNode(wo);

						return;
					}
				}

				node = node.getParent();
			}

		}
		setKeepPickAlive(false);
		setSelectedNode(null);

	}

}

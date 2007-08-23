package ca.shu.ui.lib.handlers;

import java.awt.event.InputEvent;

import ca.shu.ui.lib.objects.widgets.TooltipWrapper;
import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

public class TooltipHandler extends MousePickHandler {
	private WorldObject controls;

	private WorldObject keyboardFocusObject;

	private TooltipWrapper keyboardTooltip;

	private TooltipWrapper mouseOverTooltip;

	public TooltipHandler(World parent) {
		super(parent);
	}

	@Override
	public void keyPressed(PInputEvent event) {
		super.keyPressed(event);
		processKeyboardEvent(event);
	}

	@Override
	public void keyReleased(PInputEvent event) {
		super.keyReleased(event);
		processKeyboardEvent(event);
	}

	private void processKeyboardEvent(PInputEvent event) {
		if ((event.getModifiers() & InputEvent.CTRL_MASK) != 0) {

			WorldObject wo = getTooltipNode(event);
			if (wo != null) {
				setKeyboardTooltipFocus(wo);

			}
		} else {
			setKeyboardTooltipFocus(null);
		}

	}

	@Override
	protected int getKeepPickDelay() {
		return 1500;
	}

	@Override
	protected int getPickDelay() {
		return 500;
	}

	protected WorldObject getTooltipNode(PInputEvent event) {

		PNode node = event.getPickedNode();
		while (node != null) {

			/*
			 * Do nothing if the mouse is over the controls
			 */
			if (node == controls) {
				setKeepPickAlive(true);
				return null;
			} else if (node instanceof WorldObject) {
				WorldObject wo = (WorldObject) node;

				if (wo.getTooltip() != null) {

					return wo;
				}
			}

			node = node.getParent();
		}
		setKeepPickAlive(false);
		return null;

	}

	@Override
	protected void nodePicked() {
		WorldObject node = getPickedNode();
		node.pushState(WorldObject.State.SELECTED);

		mouseOverTooltip = getWorld().showTooltip(node);
	}

	@Override
	protected void nodeUnPicked() {
		WorldObject node = getPickedNode();

		node.popState(WorldObject.State.SELECTED);
		mouseOverTooltip.fadeAndDestroy();
		mouseOverTooltip = null;

	}

	@Override
	protected void processMouseEvent(PInputEvent event) {
		WorldObject node = null;

		processKeyboardEvent(event);
		if (World.isTooltipsVisible()) {
			node = getTooltipNode(event);
		} else {
			setKeepPickAlive(false);
		}
		setSelectedNode(node);
		if (node == null) {

			setSelectedNode(null);
		}

	}

	protected void setKeyboardTooltipFocus(WorldObject wo) {
		if (wo != keyboardFocusObject) {
			keyboardFocusObject = wo;

			if (keyboardTooltip != null) {
				keyboardTooltip.fadeAndDestroy();
			}

			if (keyboardFocusObject != null) {
				keyboardTooltip = getWorld().showTooltip(wo);
			}

		}
	}

}

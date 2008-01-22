package ca.shu.ui.lib.world.handlers;

import java.awt.event.InputEvent;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.world.WorldLayer;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.piccolo.WorldImpl;
import ca.shu.ui.lib.world.piccolo.objects.SelectionBorder;
import ca.shu.ui.lib.world.piccolo.objects.TooltipWrapper;
import ca.shu.ui.lib.world.piccolo.objects.Window;
import ca.shu.ui.lib.world.piccolo.primitives.PiccoloNodeInWorld;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Picks objects in which to show tooltips for. Handles both both and keyboard
 * events.
 * 
 * @author Shu Wu
 */
public class TooltipPickHandler extends AbstractPickHandler {
	public static final String TOOLTIP_BORDER_ATTR = "tooltipBdr";

	private WorldObject controls;

	private WorldObject keyboardFocusObject;

	private TooltipWrapper keyboardTooltip;
	private TooltipWrapper mouseOverTooltip;

	private SelectionBorder tooltipFrame;

	private int myPickDelay, myKeepPickDelay;

	public TooltipPickHandler(WorldImpl world, int pickDelay, int keepPickDelay) {
		super(world);
		myPickDelay = pickDelay;
		myKeepPickDelay = keepPickDelay;
		tooltipFrame = new SelectionBorder(world);
		tooltipFrame.setFrameColor(Style.COLOR_TOOLTIP_BORDER);

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
		return myKeepPickDelay;
	}

	@Override
	protected int getPickDelay() {
		return myPickDelay;
	}

	protected WorldObject getTooltipNode(PInputEvent event) {

		PNode node = event.getPickedNode();
		while (node != null) {

			if (node instanceof PiccoloNodeInWorld) {
				WorldObject wo = ((PiccoloNodeInWorld) node).getWorldObject();

				/*
				 * Do nothing if the mouse is over the controls
				 */
				if (node == controls) {
					setKeepPickAlive(true);
					return null;
				} else if (wo instanceof WorldLayer || wo instanceof Window) {
					break;
				} else if (wo.getTooltip() != null) {
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
		tooltipFrame.setSelected(node);

		mouseOverTooltip = getWorld().showTooltip(node);
	}

	@Override
	protected void nodeUnPicked() {

		tooltipFrame.setSelected(null);

		mouseOverTooltip.fadeAndDestroy();
		mouseOverTooltip = null;

	}

	@Override
	protected void processMouseEvent(PInputEvent event) {
		WorldObject node = null;

		processKeyboardEvent(event);
		if (WorldImpl.isTooltipsVisible()) {
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
				keyboardTooltip = null;
			}

			if (keyboardFocusObject != null) {
				keyboardTooltip = getWorld().showTooltip(wo);
			}

		}
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

}

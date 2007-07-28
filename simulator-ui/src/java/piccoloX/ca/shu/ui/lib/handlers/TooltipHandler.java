package ca.shu.ui.lib.handlers;

import ca.shu.ui.lib.world.IWorld;
import ca.shu.ui.lib.world.IWorldObject;
import ca.shu.ui.lib.world.impl.Canvas;
import ca.shu.ui.lib.world.impl.World;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

public class TooltipHandler extends PBasicInputEventHandler {

	WorldObjectImpl controls;

	Thread controlTimer;

	boolean isInsideControl = false;

	Object mouseEventLock = new Object();

	IWorld world;

	WorldObjectImpl pickedNode;

	WorldObjectImpl selectedNode;

	public TooltipHandler(IWorld parent) {
		super();
		this.world = parent;
		controlTimer = new ControlTimer();
		controlTimer.start();
	}

	@Override
	public void mouseDragged(PInputEvent event) {
		updateMouse(event);
	}

	@Override
	public void mouseMoved(PInputEvent event) {
		updateMouse(event);
	}

	public void updateMouse(PInputEvent event) {
		PNode node = event.getPickedNode();

		if (!World.isContexualTipsVisible()) {
			selectedNode = null;
			synchronized (mouseEventLock) {
				mouseEventLock.notifyAll();
			}
			return;
		}

		while (node != null) {
			if (node instanceof WorldObjectImpl) {

				if (node == controls) {
					isInsideControl = true;
					return;
				}

				WorldObjectImpl wo = (WorldObjectImpl) node;

				if (wo.getTooltipObject() != null) {

					if (wo != selectedNode) {
						selectedNode = wo;
						synchronized (mouseEventLock) {
							mouseEventLock.notifyAll();
						}
					}
					return;
				}
			}

			node = node.getParent();
		}

		isInsideControl = false;

		if (selectedNode != null) {

			selectedNode = null;
			synchronized (mouseEventLock) {
				mouseEventLock.notifyAll();
			}
		}

	}

	class ControlTimer extends Thread {

		private ControlTimer() {
			super("Control Timer");
		}

		@Override
		public void run() {
			try {
				while (true) {
					if (selectedNode != null) {
						WorldObjectImpl currNode = selectedNode;
						Thread.sleep(selectedNode.getControlDelay());

						if ((currNode == selectedNode)
								&& (selectedNode != null)) {
							currNode.pushState(IWorldObject.State.SELECTED);
							controls = selectedNode.getTooltipObject();

							world.showTooltip(controls, selectedNode);

							while (true) {
								Thread.sleep(1000);

								if ((currNode == selectedNode)
										|| ((selectedNode == null) && isInsideControl)) {
									synchronized (mouseEventLock) {
										mouseEventLock.wait(1000);
									}
								} else {
									break;
								}
							}

							currNode.popState(IWorldObject.State.SELECTED);
							world.hideControls();
						}
					} else {
						synchronized (mouseEventLock) {
							mouseEventLock.wait();
						}
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

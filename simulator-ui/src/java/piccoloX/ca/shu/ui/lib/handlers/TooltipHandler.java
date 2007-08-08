package ca.shu.ui.lib.handlers;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.impl.WorldImpl;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

public class TooltipHandler extends PBasicInputEventHandler {

	WorldObjectImpl controls;

	Thread controlTimer;

	boolean isInsideControl = false;

	Object mouseEventLock = new Object();

	World world;

	WorldObjectImpl pickedNode;

	WorldObjectImpl selectedNode;

	public TooltipHandler(World parent) {
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

		if (!WorldImpl.isContexualTipsVisible()) {
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
							currNode.pushState(WorldObject.State.SELECTED);
							controls = selectedNode.getTooltipObject();

							SwingUtilities.invokeAndWait(new Runnable() {
								public void run() {
									world.showTooltip(controls, selectedNode);
								}
							});

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

							currNode.popState(WorldObject.State.SELECTED);
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
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
}

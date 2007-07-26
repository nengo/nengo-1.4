package ca.shu.ui.lib.handlers;

import ca.shu.ui.lib.world.IWorld;
import ca.shu.ui.lib.world.IWorldObject;
import ca.shu.ui.lib.world.impl.Canvas;
import ca.shu.ui.lib.world.impl.World;
import ca.shu.ui.lib.world.impl.WorldObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

public class TooltipHandler extends PBasicInputEventHandler {

	WorldObject controls;

	Thread controlTimer;

	boolean isInsideControl = false;

	Object mouseEventLock = new Object();

	IWorld world;

	WorldObject pickedNode;

	WorldObject selectedNode;

	public TooltipHandler(IWorld parent) {
		super();
		this.world = parent;
		controlTimer = new ControlTimer();
		controlTimer.start();
	}

	@Override
	public void mouseClicked(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mouseClicked(event);

		if (event.getClickCount() == 2) {
			PNode node = event.getPickedNode();
			
			while (node != null) {
				if (node instanceof WorldObject) {

					WorldObject framedNode = (WorldObject) node;

					if (world.containsNode(node)) // only
						world.zoomToNode(framedNode);

					break;
				}
				node = node.getParent();
			}

		}

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
		// PNode node = event.getPickedNode();
		PNode node = event.getPickedNode();
		// System.out.println("Picked: " + event.getPickedNode().getClass() +
		// "Over: " + node.getClass());
		
		if (!World.isContexualTipsVisible()) {
			selectedNode = null;
			synchronized (mouseEventLock) {
				mouseEventLock.notifyAll();
			}
			return;
		}

		while (node != null) {
			if (node instanceof WorldObject) {

				if (node == controls) {

					// System.out.println("mouse in control: " +
					// node.hashCode());
					isInsideControl = true;
					return;
				}

				WorldObject wo = (WorldObject) node;

				if (wo.getTooltipObject() != null) {

					if (wo != selectedNode) {

						// System.out.println("mouse in detected: "
						// + node.hashCode());

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
		// System.out.println(node.getClass() + " " + .getClass());

	}

	class ControlTimer extends Thread {

		private ControlTimer() {
			super("Control Timer");
			// TODO Auto-generated constructor stub
		}

		@Override
		public void run() {
			try {
				while (true) {
					if (selectedNode != null) {
						WorldObject currNode = selectedNode;
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
										// System.out.println("waiting");
									}
								} else {
									// System.out.println("mouse out");
									break;
								}
								// if (selectedNode == null) {
								// System.out.println("selected node null");

								// }
							}
							// Thread.sleep(100000);
							// if (selectedNode == null) {
							
							currNode.popState(IWorldObject.State.SELECTED);
							world.hideControls();
							// }
						}
					} else {
						synchronized (mouseEventLock) {
							mouseEventLock.wait();
						}
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

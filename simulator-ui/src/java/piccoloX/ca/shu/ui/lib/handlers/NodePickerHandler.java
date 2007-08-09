package ca.shu.ui.lib.handlers;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

public abstract class NodePickerHandler extends PBasicInputEventHandler {

	private Thread controlTimer;

	private Object pickSetLock = new Object();
	private Object pickChangeLock = new Object();

	private World world;

	private WorldObject transientNode;

	public NodePickerHandler(World parent) {
		super();
		this.world = parent;
		controlTimer = new Timer();
		controlTimer.start();
	}

	@Override
	public void mouseDragged(PInputEvent event) {
		eventUpdated(event);
	}

	@Override
	public void mouseMoved(PInputEvent event) {
		eventUpdated(event);
	}

	public abstract void eventUpdated(PInputEvent event);

	protected abstract void nodePicked();

	protected abstract void nodeUnPicked();

	protected abstract int getPickDelay();

	protected abstract int getKeepPickDelay();

	WorldObject pickedNode;
	private boolean keepPickAlive = false;

	class Timer extends Thread {

		private Timer() {
			super("Node Picker Timer");
		}

		@Override
		public void run() {
			try {
				while (!world.isDestroyed()) {
					if (transientNode != null) {
						pickedNode = transientNode;

						if (getPickDelay() > 0) {
							synchronized (pickChangeLock) {
								pickChangeLock.wait(getPickDelay());
							}
						}
						// check that the transient node hasn't changed since
						// the Show Delay
						if (transientNode == pickedNode) {

							SwingUtilities.invokeAndWait(new Runnable() {
								public void run() {
									nodePicked();
								}
							});

							/**
							 * Keep waiting while the transient node is the same
							 */
							while (!world.isDestroyed()) {
								if (getKeepPickDelay() > 0) {
									synchronized (pickChangeLock) {
										pickChangeLock.wait(getKeepPickDelay());
									}
								}
								if (pickedNode == transientNode
										|| keepPickAlive) {
									synchronized (pickSetLock) {
										pickSetLock.wait(1000);
									}
								} else {
									break;
								}
							}

							SwingUtilities.invokeAndWait(new Runnable() {
								public void run() {
									nodeUnPicked();
									pickedNode = null;
								}
							});

						}
					} else {
						synchronized (pickChangeLock) {
							pickChangeLock.wait(1000);
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

	protected WorldObject getPickedNode() {
		return pickedNode;
	}

	protected void setSelectedNode(WorldObject selectedNode) {
		WorldObject oldNode = transientNode;
		transientNode = selectedNode;

		if (selectedNode != null && selectedNode != oldNode) {
			synchronized (pickChangeLock) {
				pickChangeLock.notifyAll();
			}
		}

		synchronized (pickSetLock) {
			pickSetLock.notifyAll();
		}

	}

	protected World getWorld() {
		return world;
	}

	protected void setKeepPickAlive(boolean keepPickAlive) {
		this.keepPickAlive = keepPickAlive;
	}

	@Override
	public void keyPressed(PInputEvent event) {
		eventUpdated(event);
	}

	@Override
	public void processEvent(PInputEvent event, int type) {
		super.processEvent(event, type);
	}

	public boolean isKeepPickAlive() {
		return keepPickAlive;
	}
}

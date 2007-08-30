package ca.shu.ui.lib.handlers;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Abstract handler which picks and unpicks nodes with a delay
 * 
 * @author Shu Wu
 */
public abstract class AbstractPickHandler extends PBasicInputEventHandler {
	private final Thread controlTimer;

	private boolean keepPickAlive = false;

	private final Object pickChangeLock = new Object();

	private final Object pickSetLock = new Object();

	private WorldObject transientNode;

	private final World world;

	private WorldObject pickedNode;

	public AbstractPickHandler(World parent) {
		super();
		this.world = parent;
		controlTimer = new Timer();
		controlTimer.start();
	}

	public boolean isKeepPickAlive() {
		return keepPickAlive;
	}

	@Override
	public void mouseDragged(PInputEvent event) {
		processMouseEvent(event);
	}

	@Override
	public void mouseMoved(PInputEvent event) {
		processMouseEvent(event);
	}

	@Override
	public void processEvent(PInputEvent event, int type) {
		super.processEvent(event, type);
	}

	protected abstract int getKeepPickDelay();

	protected abstract int getPickDelay();

	protected WorldObject getPickedNode() {
		return pickedNode;
	}

	protected World getWorld() {
		return world;
	}

	protected abstract void nodePicked();

	protected abstract void nodeUnPicked();

	protected abstract void processMouseEvent(PInputEvent event);

	protected void setKeepPickAlive(boolean keepPickAlive) {
		this.keepPickAlive = keepPickAlive;
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
						pickedNode = null;
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
}

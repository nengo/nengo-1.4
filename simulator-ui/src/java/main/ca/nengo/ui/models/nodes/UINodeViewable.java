package ca.nengo.ui.models.nodes;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import javax.swing.SwingUtilities;

import ca.nengo.model.Node;
import ca.nengo.ui.brainView.BrainViewer;
import ca.nengo.ui.models.UINeoNode;
import ca.nengo.ui.models.tooltips.TooltipBuilder;
import ca.nengo.ui.models.viewers.NodeViewer;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.menus.AbstractMenuBuilder;
import ca.shu.ui.lib.world.piccolo.objects.Window;
import ca.shu.ui.lib.world.piccolo.objects.Window.WindowState;

/**
 * UI Wrapper for Node Containers such as Ensembles and Networks.
 * 
 * @author Shu
 */
public abstract class UINodeViewable extends UINeoNode {

	private static final long serialVersionUID = 1L;

	/**
	 * Weak reference to the viewer window
	 */
	private WeakReference<Window> viewerWindowRef;

	public UINodeViewable(Node model) {
		super(model);
	}

	@Override
	protected void constructTooltips(TooltipBuilder tooltips) {
		super.constructTooltips(tooltips);
		tooltips.addProperty("# Nodes", "" + getNodesCount());
	}

	@Override
	protected void constructViewMenu(AbstractMenuBuilder menu) {
		super.constructViewMenu(menu);

		if (viewerWindowRef.get() == null || viewerWindowRef.get().isDestroyed()
				|| (viewerWindowRef.get().getWindowState() == Window.WindowState.MINIMIZED)) {

			menu.addAction(new StandardAction("Open viewer") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void action() throws ActionException {
					openViewer();
				}
			});

		} else {
			menu.addAction(new StandardAction("Close viewer") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void action() throws ActionException {
					closeViewer();
				}
			});

		}

		menu.addAction(new StandardAction("Brain View (under construction)") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void action() throws ActionException {
				createBrainViewer();
			}
		});
	}

	/**
	 * Creates a new Viewer
	 * 
	 * @return Viewer created
	 */
	protected abstract NodeViewer createViewerInstance();

	/**
	 * @return Viewer Window
	 */
	protected Window getViewerWindow() {

		if (viewerWindowRef.get() == null || viewerWindowRef.get().isDestroyed()) {

			NodeViewer nodeViewer = createViewerInstance();
			Window viewerWindow = new Window(this, nodeViewer);
			nodeViewer.applyDefaultLayout();

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (getWorld() != null) {
						if (viewerWindowRef.get() != null && !viewerWindowRef.get().isDestroyed()) {
							getWorld().zoomToObject(viewerWindowRef.get());
						}
					}
				}
			});

			viewerWindowRef = new WeakReference<Window>(viewerWindow);
		}

		return viewerWindowRef.get();

	}

	@Override
	protected void initialize() {
		viewerWindowRef = new WeakReference<Window>(null);

		super.initialize();
	}

	@Override
	protected void prepareForDestroy() {
		closeViewer();
		super.prepareForDestroy();
	}

	/**
	 * Closes the viewer Window
	 */
	public void closeViewer() {
		if (viewerWindowRef.get() != null)
			viewerWindowRef.get().destroy();

	}

	/**
	 * Opens a new instance of Brain View
	 */
	public void createBrainViewer() {
		BrainViewer brainViewer = new BrainViewer();

		new Window(this, brainViewer);
		// window.setOffset(0, -brainViewer.getHeight());
		// addChild(brainViewer);
	}

	@Override
	public void detachViewFromModel() {
		closeViewer();
		super.detachViewFromModel();
	}

	@Override
	public void doubleClicked() {
		openViewer();
	}

	/**
	 * @return Number of nodes contained by the Model
	 */
	public abstract int getNodesCount();

	/**
	 * @return Container Viewer
	 */
	public NodeViewer getViewer() {
		if (viewerWindowRef.get() != null) {
			return (NodeViewer) viewerWindowRef.get().getContents();
		}
		return null;
	}

	/**
	 * Opens the Container Viewer
	 * 
	 * @return the Container viewer
	 */
	public NodeViewer openViewer() {
		Window viewerWindow = getViewerWindow();
		if (viewerWindow.getWindowState() == WindowState.MINIMIZED) {
			viewerWindow.restoreSavedWindow();

		}
		return (NodeViewer) viewerWindow.getContents();

	}

	/**
	 * Saves the configuration of this node container
	 */
	public abstract void saveContainerConfig();

	@Override
	public void saveModel(File file) throws IOException {
		saveContainerConfig();
		super.saveModel(file);
	}

}

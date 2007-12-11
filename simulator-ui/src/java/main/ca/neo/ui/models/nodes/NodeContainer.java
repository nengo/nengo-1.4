package ca.neo.ui.models.nodes;

import java.lang.ref.WeakReference;

import ca.neo.model.Node;
import ca.neo.ui.actions.SaveNodeContainerAction;
import ca.neo.ui.brainView.BrainViewer;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.tooltips.PropertyPart;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.neo.ui.models.viewers.NodeViewer;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.Window;
import ca.shu.ui.lib.objects.Window.WindowState;
import ca.shu.ui.lib.util.menus.AbstractMenuBuilder;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;

/**
 * UI Wrapper for Node Containers such as Ensembles and Networks.
 * 
 */
/**
 * @author Shu
 */
public abstract class NodeContainer extends UINeoNode {

	private static final long serialVersionUID = 1L;

	/**
	 * Weak reference to the viewer window
	 */
	private WeakReference<Window> viewerWindowRef = new WeakReference<Window>(
			null);

	public NodeContainer() {
		super();
	}

	public NodeContainer(Node model) {
		super(model);
	}

	@Override
	protected void constructViewMenu(AbstractMenuBuilder menu) {
		super.constructViewMenu(menu);

		if (viewerWindowRef.get() == null
				|| viewerWindowRef.get().isDestroyed()
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

	@Override
	protected void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);

		menu.addSection("File");
		menu.addAction(new SaveNodeContainerAction(
				"Save " + this.getTypeName(), this));

	}

	@Override
	protected void constructTooltips(TooltipBuilder tooltips) {
		tooltips.addPart(new PropertyPart("# Nodes", "" + getNodesCount()));
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

		if (viewerWindowRef.get() == null
				|| viewerWindowRef.get().isDestroyed()) {

			NodeViewer nodeViewer = createViewerInstance();
			Window viewerWindow = new Window(this, nodeViewer);
			nodeViewer.applyDefaultLayout();

			viewerWindowRef = new WeakReference<Window>(viewerWindow);

		}

		return viewerWindowRef.get();

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

	@Override
	public void doubleClicked() {
		openViewer();
	}

	/**
	 * @return The file name for saving this node container
	 */
	public abstract String getFileName();

	/**
	 * @return Number of nodes contained by the Model
	 */
	public abstract int getNodesCount();

	/**
	 * @return Container Viewer
	 */
	public NodeViewer getViewer() {
		if (viewerWindowRef.get() != null) {
			return (NodeViewer) viewerWindowRef.get().getWindowContent();
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
			viewerWindow.restoreWindow();
		}
		return (NodeViewer) viewerWindow.getWindowContent();

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

	/**
	 * Saves the configuration of this node container
	 */
	public abstract void saveContainerConfig();

	/**
	 * @param fileName
	 *            New file Name
	 */
	public abstract void setFileName(String fileName);

}

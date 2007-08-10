package ca.neo.ui.models.nodes;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import ca.neo.io.FileManager;
import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.TooltipBuilder;
import ca.neo.ui.models.TooltipPart;
import ca.neo.ui.models.actions.SaveNodeContainerAction;
import ca.neo.ui.models.viewers.NodeViewer;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.Window;
import ca.shu.ui.lib.objects.Window.WindowState;
import ca.shu.ui.lib.util.PopupMenuBuilder;

/**
 * For models such as Ensembles and Networks which can contain nodes
 */
public abstract class PNodeContainer extends PNeoNode {

	private static final long serialVersionUID = 1L;

	// private Window networkWindow;

	WeakReference<Window> viewerWindowRef = new WeakReference<Window>(null);

	// Window viewerWindow;

	public PNodeContainer() {
		super();
	}

	public PNodeContainer(Node model) {
		super(model);
	}

	/**
	 * Closes the Network viewer
	 */
	public void closeViewer() {
		if (viewerWindowRef.get() != null)
			viewerWindowRef.get().destroy();
		
	}

	@Override
	public void doubleClicked() {
		openViewer();
	}

	public NodeViewer getAndConstructViewer() {

		if (viewerWindowRef.get() == null
				|| viewerWindowRef.get().isDestroyed()) {
			Window viewerWindow = new Window(this, createNodeViewerInstance());
			viewerWindow.translate(0, this.getHeight() + 20);
			viewerWindowRef = new WeakReference<Window>(viewerWindow);

		}

		return (NodeViewer) viewerWindowRef.get().getWindowContent();

	}

	/**
	 * @return Number of nodes contained by the Model
	 */
	public abstract int getNodesCount();

	public NodeViewer getViewer() {
		if (viewerWindowRef.get() != null) {
			return (NodeViewer) viewerWindowRef.get().getWindowContent();
		}
		return null;
	}

	/**
	 * 
	 * @return opens the network viewer which contains the nodes of the Network
	 *         model
	 */
	public NodeViewer openViewer() {
		NodeViewer nodeViewer = getAndConstructViewer();

		viewerWindowRef.get().setWindowState(WindowState.MAXIMIZED);
		viewerWindowRef = new WeakReference<Window>(null);
		
		return nodeViewer;

	}

	/**
	 * Saves the model representing the Node Container
	 * 
	 * @param file
	 *            File to save the model to
	 * @throws IOException
	 */

	public void saveModel(File file) throws IOException {
		FileManager fm = new FileManager();

		fm.save((Network) getModel(), file);
	}

	@Override
	protected void afterModelCreated() {
		super.afterModelCreated();

	}

	protected PopupMenuBuilder constructMenu() {
		PopupMenuBuilder menu = super.constructMenu();

		menu.addSection("File");
		menu.addAction(new SaveNodeContainerAction(
				"Save " + this.getTypeName(), this));

		menu.addSection("Container");
		if (viewerWindowRef.get() == null
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
		
		
		return menu;

	}

	@Override
	protected TooltipBuilder constructTooltips() {
		TooltipBuilder tooltips = super.constructTooltips();

		tooltips.addPart(new TooltipPart("# Nodes", "" + getNodesCount()));
		return tooltips;
	}

	// @Override
	// protected void moveWidgetsToFront() {
	// /**
	// * Only move widgets to the front if there is no Window open Otherwise,
	// * they might block the Window
	// */
	// if (viewerWindow == null
	// || viewerWindow.isDestroyed()
	// || viewerWindow.getWindowState() == Window.WindowState.MINIMIZED)
	// super.moveWidgetsToFront();
	//
	// }

	/**
	 * 
	 * @return NodeViewer An instance of the Node Viewer to peek into the nodes
	 *         contained
	 */
	protected abstract NodeViewer createNodeViewerInstance();

	@Override
	protected void prepareForDestroy() {
		closeViewer();
		super.prepareForDestroy();
	}

}

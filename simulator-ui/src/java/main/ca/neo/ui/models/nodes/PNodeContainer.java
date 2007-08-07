package ca.neo.ui.models.nodes;

import ca.neo.model.Node;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.viewers.NetworkViewer;
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

	Window viewerWindow;

	public PNodeContainer() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PNodeContainer(Node model) {
		super(model);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @return opens the network viewer which contains the nodes of the Network
	 *         model
	 */
	public NodeViewer openViewer() {
		NodeViewer nodeViewer = getViewer();

		viewerWindow.setWindowState(WindowState.MAXIMIZED);

		return nodeViewer;

	}

	/**
	 * Minimizes the Network Viewer GUI
	 */
	public void closeViewer() {
		if (networkWindow != null)
			networkWindow.destroy();

	}

	Window networkWindow;

	public PopupMenuBuilder constructMenu() {
		PopupMenuBuilder menu = super.constructMenu();
		menu.addSection("Container");
		if (networkWindow == null
				|| (networkWindow.getWindowState() == Window.WindowState.MINIMIZED)) {

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

	private void constructViewer() {

		viewerWindow = new Window(this, createNodeViewerInstance());
		viewerWindow.translate(0, this.getHeight() + 20);
	}

	@Override
	public void doubleClicked() {
		openViewer();
	}

	/**
	 * 
	 * @return NodeViewer An instance of the Node Viewer to peek into the nodes
	 *         contained
	 */
	protected abstract NodeViewer createNodeViewerInstance();

	public NodeViewer getViewer() {
		if (viewerWindow == null || viewerWindow.isDestroyed()) {
			constructViewer();
		}

		return (NodeViewer) viewerWindow.getWindowContent();
	}

	@Override
	protected void moveWidgetsToFront() {
		/**
		 * Only move widgets to the front if there is no Window open Otherwise,
		 * they might block the Window
		 */
		if (viewerWindow == null
				|| viewerWindow.getWindowState() == Window.WindowState.MINIMIZED)
			super.moveWidgetsToFront();

	}

}

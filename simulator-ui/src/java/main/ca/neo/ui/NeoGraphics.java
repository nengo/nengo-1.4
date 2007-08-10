package ca.neo.ui;

import javax.swing.JFileChooser;
import javax.swing.JMenuBar;

import ca.neo.ui.actions.CreateModelAction;
import ca.neo.ui.actions.LoadEnsembleAction;
import ca.neo.ui.actions.LoadNetworkAction;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.nodes.PEnsemble;
import ca.neo.ui.models.nodes.PNEFEnsemble;
import ca.neo.ui.models.nodes.PNetwork;
import ca.neo.ui.widgets.Toolbox;
import ca.neo.util.Environment;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.world.GFrame;
import ca.shu.ui.lib.world.WorldObject;

public class NeoGraphics extends GFrame implements INodeContainer {

	public static final JFileChooser FileChooser = new JFileChooser();
	private static final long serialVersionUID = 1L;
	public static final String USER_FILE_DIR = "UIFiles";

	public static void main(String[] args) {
		new NeoGraphics("Default");

	}

	Toolbox canvasView;

	public NeoGraphics(String title) {
		super(title + " - NEO Workspace");

		Environment.setUserInterface(true);
		// PDebug.debugThreads = true;
		// PDebug.debugPrintUsedMemory = true;

	}

	@Override
	public String getUserFileDirectory() {
		return USER_FILE_DIR;
	}

	/**
	 * 
	 * @param object
	 *            Object to be added to the top-level world
	 * @return the object being added
	 */
	public WorldObject addWorldObject(WorldObject object) {
		getWorld().getGround().catchObject(object);
		return object;
	}

	@Override
	public void constructMenu(JMenuBar menuBar) {
		MenuBuilder menu = new MenuBuilder("File");
		menuBar.add(menu.getJMenu());

		MenuBuilder newMenu = menu.createSubMenu("New");
		newMenu
				.addAction(new CreateModelAction("Network", this,
						PNetwork.class));
		newMenu.addAction(new CreateModelAction("NEFEnsemble", this,
				PNEFEnsemble.class));

		newMenu.addAction(new CreateModelAction("Ensemble", this,
				PEnsemble.class));

		menu.addAction(new LoadNetworkAction("Open Network") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void gotNetwork(PNetwork network) {
				getWorld().getGround().catchObject(network);
				network.openViewer();
			}

		});
		menu.addAction(new LoadEnsembleAction("Open (NEF)Ensemble") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void gotEnsemble(PEnsemble ensemble) {
				getWorld().getGround().catchObject(ensemble);
				ensemble.openViewer();
			}

		});

	}

	public Toolbox getCanvasView() {
		return canvasView;
	}

	public void showCanvas() {
		if (canvasView == null) {
			System.out.println("Creating canvas");
			canvasView = new Toolbox();
			getWorld().getSky().addChild(canvasView);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.models.nodes.INodeContainer#addNeoNode(ca.neo.ui.models.PNeoNode)
	 */
	public void addNeoNode(PNeoNode node) {
		getWorld().getGround().catchObject(node);

	}

}

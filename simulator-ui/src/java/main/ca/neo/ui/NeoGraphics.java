package ca.neo.ui;

import javax.swing.JFileChooser;
import javax.swing.JMenuBar;

import ca.neo.model.Ensemble;
import ca.neo.model.Network;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.ui.actions.LoadObjectAction;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.nodes.PEnsemble;
import ca.neo.ui.models.nodes.PNEFEnsemble;
import ca.neo.ui.models.nodes.PNetwork;
import ca.neo.ui.widgets.Toolbox;
import ca.neo.util.Environment;
import ca.shu.ui.lib.actions.CreateModelAction;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.impl.GFrame;

public class NeoGraphics extends GFrame implements INodeContainer {

	public static final JFileChooser FileChooser = new JFileChooser();
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new NeoGraphics("Default");
	}

	Toolbox canvasView;

	public NeoGraphics(String title) {
		super(title + " - NEO Workspace");

		Environment.setUserInterface(true);

		// PDebug.debugPaintCalls = true;

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
	public void constructMenuBar(JMenuBar menuBar) {
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

		menu.addAction(new LoadNetworkAction("Open Network"));
		menu.addAction(new LoadEnsembleAction("Open (NEF)Ensemble"));

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

	class LoadEnsembleAction extends LoadObjectAction {

		private static final long serialVersionUID = 1L;

		public LoadEnsembleAction(String actionName) {
			super("Load Ensemble from file", actionName);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void processObject(Object objLoaded) {

			PEnsemble ensembleUI = null;
			if (objLoaded instanceof Ensemble) {
				ensembleUI = new PEnsemble((Ensemble) objLoaded);
			} else if (objLoaded instanceof NEFEnsemble) {
				ensembleUI = new PNEFEnsemble((NEFEnsemble) objLoaded);
			} else {
				Util.Error("Could not load Ensemble file");
			}
			if (ensembleUI != null)
				getWorld().getGround().catchObject(ensembleUI);

		}

	}

	class LoadNetworkAction extends LoadObjectAction {

		private static final long serialVersionUID = 1L;

		public LoadNetworkAction(String actionName) {
			super("Load network from file", actionName);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void processObject(Object objLoaded) {
			if (objLoaded instanceof Network) {
				PNetwork networkUI = new PNetwork((Network) objLoaded);
				getWorld().getGround().catchObject(networkUI);
			} else {
				Util.Error("Could not load Network file");
			}

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

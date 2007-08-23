package ca.neo.ui;

import java.util.Iterator;

import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import ca.neo.ui.actions.CreateModelAction;
import ca.neo.ui.actions.OpenNeoFileAction;
import ca.neo.ui.actions.SaveNodeContainerAction;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.nodes.PEnsemble;
import ca.neo.ui.models.nodes.PNEFEnsemble;
import ca.neo.ui.models.nodes.PNetwork;
import ca.neo.ui.models.nodes.PNodeContainer;
import ca.neo.ui.util.NeoFileChooser;
import ca.neo.util.Environment;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.world.GFrame;
import ca.shu.ui.lib.world.WorldObject;

public class NeoGraphics extends GFrame implements INodeContainer {

	@Override
	protected void windowClosing() {
		promptToSaveModels();
		super.windowClosing();
	}

	@SuppressWarnings("unchecked")
	protected void promptToSaveModels() {
		if (getWorld().getGround().getChildrenCount() == 0)
			return;

		if (JOptionPane.showConfirmDialog(this, "Save models before closing?",
				"Confirmation", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}

		Iterator<Object> it = getWorld().getGround().getChildrenIterator();

		while (it.hasNext()) {
			Object obj = it.next();

			if (obj instanceof PNodeContainer) {
				PNodeContainer node = (PNodeContainer) obj;
				if (SaveNodeContainerAction.canSave(node.getModel())) {

					SaveNodeContainerAction saveAction = new SaveNodeContainerAction(
							"Save model", node);
					saveAction.doAction();

				}
			}

		}
	}

	public static final String ENSEMBLE_FILE_EXTENSION = "ens";
	public static final NeoFileChooser FileChooser = new NeoFileChooser();
	public static final String NETWORK_FILE_EXTENSION = "net";

	public static final String USER_FILE_DIR = "UIFiles";
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new NeoGraphics("Default");

	}

	public NeoGraphics(String title) {
		super(title + " - NEO Workspace");

		Environment.setUserInterface(true);
		// PDebug.debugThreads = true;
		// PDebug.debugPrintUsedMemory = true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.models.nodes.INodeContainer#addNeoNode(ca.neo.ui.models.PNeoNode)
	 */
	public void addNeoNode(PNeoNode node) {
		getWorld().getGround().catchObject(node);
		if (node instanceof PNodeContainer) {
			((PNodeContainer) (node)).openViewer();
		}

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

		menu.addAction(new OpenNeoFileAction("Open from file", this));

	}

	@Override
	public String getUserFileDirectory() {
		return USER_FILE_DIR;
	}

}

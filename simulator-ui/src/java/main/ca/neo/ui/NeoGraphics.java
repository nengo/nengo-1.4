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
import ca.shu.ui.lib.world.AppFrame;
import ca.shu.ui.lib.world.WorldObject;

public class NeoGraphics extends AppFrame implements INodeContainer {
	public static final String ABOUT = "NeoGraphics<BR><BR>"
			+ "(c) Copyright Center for Theoretical Neuroscience  007.  All rights reserved<BR>"
			+ "Visit http://ctn.uwaterloo.ca/<BR>"
			+ "<BR> User Inteface by Shu Wu";

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
	public String getAboutString() {
		return ABOUT;
	}

	@Override
	public String getAppName() {
		return "NeoGraphics";
	}

	@Override
	public String getUserFileDirectory() {
		return USER_FILE_DIR;
	}

	@Override
	public void initApplicationMenu(JMenuBar menuBar) {
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

	@SuppressWarnings("unchecked")
	protected void promptToSaveModels() {

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

	@Override
	protected void windowClosing() {
		if (getWorld().getGround().getChildrenCount() > 0
				&& JOptionPane.showConfirmDialog(this,
						"Save models before closing?", "Exiting "
								+ getAppName(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			promptToSaveModels();
		}

		super.windowClosing();
	}

}

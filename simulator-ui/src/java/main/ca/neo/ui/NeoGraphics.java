package ca.neo.ui;

import java.util.Iterator;

import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import ca.neo.ui.actions.CreateModelAction;
import ca.neo.ui.actions.OpenNeoFileAction;
import ca.neo.ui.actions.SaveNodeContainerAction;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.nodes.UIEnsemble;
import ca.neo.ui.models.nodes.UINEFEnsemble;
import ca.neo.ui.models.nodes.UINetwork;
import ca.neo.ui.models.nodes.UINodeContainer;
import ca.neo.ui.util.NeoFileChooser;
import ca.neo.util.Environment;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.world.AppFrame;

/**
 * Top level instance of the NeoGraphics application
 * 
 * @author Shu Wu
 */
public class NeoGraphics extends AppFrame implements INodeContainer {
	private static final long serialVersionUID = 1L;

	/**
	 * Description of NeoGraphics to be shown in the "About" Dialog box
	 */
	public static final String ABOUT = "NeoGraphics<BR><BR>"
			+ "(c) Copyright Center for Theoretical Neuroscience  007.  All rights reserved<BR>"
			+ "Visit http://ctn.uwaterloo.ca/<BR>"
			+ "<BR> User Inteface by Shu Wu";

	/**
	 * File extension for Ensembles
	 */
	public static final String ENSEMBLE_FILE_EXTENSION = "ens";

	/**
	 * UI delegate object used to show the FileChooser
	 */
	public static final NeoFileChooser FileChooser = new NeoFileChooser();

	/**
	 * File extension for Networks
	 */
	public static final String NETWORK_FILE_EXTENSION = "net";

	/**
	 * Name of the directory where UI Files are stored
	 */
	public static final String USER_FILE_DIR = "UIFiles";

	/**
	 * Runs NeoGraphics with a default name
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new NeoGraphics("Default");

	}

	/**
	 * @param title
	 *            Text to be shown in the Title Bar
	 */
	public NeoGraphics(String title) {
		super(title + " - NEO Workspace");

		/*
		 * Set up Environment variables
		 */
		Environment.setUserInterface(true);
		// PDebug.debugThreads = true;
		// PDebug.debugPrintUsedMemory = true;

	}

	/**
	 * Prompt user to save models in NeoGraphics This is most likely called
	 * right before the application is exiting
	 */
	@SuppressWarnings("unchecked")
	protected void promptToSaveModels() {

		Iterator<Object> it = getWorld().getGround().getChildrenIterator();

		while (it.hasNext()) {
			Object obj = it.next();

			if (obj instanceof UINodeContainer) {
				UINodeContainer node = (UINodeContainer) obj;
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
		if (getWorld().getGround().getChildrenCount() > 0) {
			int response = JOptionPane.showConfirmDialog(this,
					"Save models before closing?", "Exiting " + getAppName(),
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (response == JOptionPane.YES_OPTION) {

				promptToSaveModels();
			} else if (response == JOptionPane.CANCEL_OPTION) {
				/*
				 * Cancel exit
				 */
				return;
			}
		}

		super.windowClosing();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.models.INodeContainer#addNeoNode(ca.neo.ui.models.UINeoNode)
	 */
	public void addNeoNode(UINeoNode node) {
		getWorld().getGround().catchObject(node);
		if (node instanceof UINodeContainer) {
			((UINodeContainer) (node)).openViewer();
		}
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
		newMenu.addAction(new CreateModelAction("Network", this,
				UINetwork.class));
		newMenu.addAction(new CreateModelAction("NEFEnsemble", this,
				UINEFEnsemble.class));

		newMenu.addAction(new CreateModelAction("Ensemble", this,
				UIEnsemble.class));

		menu.addAction(new OpenNeoFileAction("Open from file", this));

	}

}

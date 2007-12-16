package ca.neo.ui;

import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ca.neo.model.Network;
import ca.neo.ui.actions.CreateModelAction;
import ca.neo.ui.actions.OpenNeoFileAction;
import ca.neo.ui.actions.SaveNodeAction;
import ca.neo.ui.dataList.DataListView;
import ca.neo.ui.dataList.SimulatorDataModel;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.nodes.NodeContainer;
import ca.neo.ui.models.nodes.UIEnsemble;
import ca.neo.ui.models.nodes.UINEFEnsemble;
import ca.neo.ui.models.nodes.UINetwork;
import ca.neo.ui.util.NeoFileChooser;
import ca.neo.util.Environment;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.menus.MenuBuilder;
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
	public static final String ABOUT = "NEO Graphics<BR><BR>"
			+ "(c) Copyright Center for Theoretical Neuroscience 2007.  All rights reserved<BR>"
			+ "Visit http://ctn.uwaterloo.ca/<BR>"
			+ "<BR> User Interface by Shu Wu (shuwu83@gmail.com)";

	/**
	 * File extension for Neo Nodes
	 */
	public static final String NEONODE_FILE_EXTENSION = "neonode";

	/**
	 * UI delegate object used to show the FileChooser
	 */
	public static NeoFileChooser FileChooser;

	/**
	 * Runs NeoGraphics with a default name
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		new NeoGraphics("Default");

	}

	private JDialog dataViewerDialog = null;

	private SimulatorDataModel simulationData;

	/**
	 * @param title
	 *            Text to be shown in the Title Bar
	 */
	public NeoGraphics(String title) {
		super(title + " - NEO Workspace");

		UIEnvironment.setDebugEnabled(true);

		simulationData = new SimulatorDataModel();

		if (FileChooser == null) {
			FileChooser = new NeoFileChooser();
		}

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

			if (obj instanceof UINeoNode) {

				SaveNodeAction saveAction = new SaveNodeAction((UINeoNode) obj);
				saveAction.doAction();

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
		if (node instanceof NodeContainer) {
			((NodeContainer) (node)).openViewer();
		}
		getWorld().getGround().addObject(node);

	}

	public void captureInDataViewer(Network network) {
		simulationData.captureData(network);
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
	public void initFileMenu(JMenuBar menuBar) {
		MenuBuilder fileMenu = new MenuBuilder("File");
		fileMenu.getJMenu().setMnemonic(KeyEvent.VK_F);

		menuBar.add(fileMenu.getJMenu());

		MenuBuilder newMenu = fileMenu.createSubMenu("New");
		newMenu.getJMenu().setMnemonic(KeyEvent.VK_N);
		newMenu.addAction(new CreateModelAction("Network", this,
				UINetwork.class), KeyEvent.VK_N);
		newMenu.addAction(new CreateModelAction("NEFEnsemble", this,
				UINEFEnsemble.class), KeyEvent.VK_F);

		newMenu.addAction(new CreateModelAction("Ensemble", this,
				UIEnsemble.class), KeyEvent.VK_E);

		fileMenu.addAction(new OpenNeoFileAction(this), KeyEvent.VK_O);

	}

	@Override
	public void initViewMenu(JMenuBar menuBar) {
		MenuBuilder viewMenu = new MenuBuilder("View");
		viewMenu.getJMenu().setMnemonic(KeyEvent.VK_V);
		menuBar.add(viewMenu.getJMenu());

		viewMenu.addAction(new OpenDataViewerAction(), KeyEvent.VK_N);
	}

	public void openDataViewer() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (dataViewerDialog == null) {
					dataViewerDialog = DataListView.createViewer(UIEnvironment
							.getInstance(), simulationData);
				}
				dataViewerDialog.setVisible(true);
			}
		});

	}

	public class OpenDataViewerAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public OpenDataViewerAction() {
			super("Open data viewer");
		}

		@Override
		protected void action() throws ActionException {
			openDataViewer();
		}

	}

}

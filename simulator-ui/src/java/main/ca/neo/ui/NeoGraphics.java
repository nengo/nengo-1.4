package ca.neo.ui;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ca.neo.config.ClassRegistry;
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
import ca.shu.ui.lib.AppFrame;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.util.menus.MenuBuilder;
import ca.shu.ui.lib.world.WorldObject;

/**
 * Top level instance of the NeoGraphics application
 * 
 * @author Shu Wu
 */
/**
 * @author User
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
	 * UI delegate object used to show the FileChooser
	 */
	public static NeoFileChooser FileChooser;

	/**
	 * File extension for Neo Nodes
	 */
	public static final String NEONODE_FILE_EXTENSION = "neonode";

	/**
	 * Runs NeoGraphics with a default name
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new NeoGraphics();
	}

	/**
	 * Data viewer dialog
	 */
	private JDialog dataViewerDialog = null;

	/**
	 * Data viewer data
	 */
	private SimulatorDataModel simulationData;

	/**
	 * @param title
	 *            Text to be shown in the Title Bar
	 */
	public NeoGraphics() {
		super();

		UIEnvironment.setDebugEnabled(true);

		simulationData = new SimulatorDataModel();

		if (FileChooser == null) {
			FileChooser = new NeoFileChooser();
		}

		/*
		 * Register plugin classes
		 */
		registerPlugins();

		/*
		 * Set up Environment variables
		 */
		Environment.setUserInterface(true);
	}

	static final String PLUGIN_DIRECTORY = "plugins";

	/**
	 * Register plugins
	 */
	private void registerPlugins() {
		try {
			LinkedList<URL> pluginUrls = new LinkedList<URL>();
			LinkedList<JarFile> pluginJars = new LinkedList<JarFile>();

			File pluginDir = new File(PLUGIN_DIRECTORY);
			pluginUrls.add(pluginDir.toURI().toURL());

			File[] pluginJarFiles = pluginDir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					if (name.endsWith("jar")) {
						return true;
					} else {
						return false;
					}
				}
			});

			for (File jarFile : pluginJarFiles) {
				pluginUrls.add(jarFile.toURI().toURL());
			}

			URL[] pluginUrlsArray = pluginUrls.toArray(new URL[] {});

			URLClassLoader urlClassLoader = new URLClassLoader(pluginUrlsArray);

			/*
			 * Loads all classes in each plugin jar
			 */
			for (File jarFile : pluginJarFiles) {
				try {
					JarFile jar = new JarFile(jarFile);
					pluginJars.add(jar);
					Enumeration<JarEntry> entries = jar.entries();
					while (entries.hasMoreElements()) {
						JarEntry entry = entries.nextElement();
						String fileName = entry.getName();
						if (fileName.endsWith(".class")) {
							try {
								String className = fileName.substring(0, fileName.lastIndexOf('.'))
										.replace('/', '.');// .replace('$',
								// '.');
								Class<?> newClass = urlClassLoader.loadClass(className);

								Util.debugMsg("Registering class: " + newClass.getName());
								ClassRegistry.getInstance().register(newClass);

							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
						}
					}

					pluginUrls.add(jarFile.toURI().toURL());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prompt user to save models in NeoGraphics This is most likely called
	 * right before the application is exiting
	 */
	@SuppressWarnings("unchecked")
	protected void promptToSaveModels() {

		for (WorldObject wo : getWorld().getGround().getChildren()) {
			if (wo instanceof UINeoNode) {
				SaveNodeAction saveAction = new SaveNodeAction((UINeoNode) wo);
				saveAction.doAction();

			}
		}
	}

	@Override
	public void exitAppFrame() {
		if (getWorld().getGround().getChildrenCount() > 0) {
			int response = JOptionPane.showConfirmDialog(this, "Save models before closing?",
					"Exiting " + getAppName(), JOptionPane.YES_NO_CANCEL_OPTION);
			if (response == JOptionPane.YES_OPTION) {

				promptToSaveModels();
			} else if (response == JOptionPane.CANCEL_OPTION) {
				/*
				 * Cancel exit
				 */
				return;
			}
		}

		super.exitAppFrame();
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
		return "NEO Graphics";
	}

	public String getAppWindowTitle() {
		return "NEO Workspace";
	}

	@Override
	public void initFileMenu(MenuBuilder fileMenu) {

		MenuBuilder newMenu = fileMenu.addSubMenu("New");
		newMenu.getJMenu().setMnemonic(KeyEvent.VK_N);
		newMenu.addAction(new CreateModelAction("Network", this, UINetwork.class), KeyEvent.VK_N);
		newMenu.addAction(new CreateModelAction("NEFEnsemble", this, UINEFEnsemble.class),
				KeyEvent.VK_F);

		newMenu.addAction(new CreateModelAction("Ensemble", this, UIEnsemble.class), KeyEvent.VK_E);

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
					dataViewerDialog = DataListView.createViewer(UIEnvironment.getInstance(),
							simulationData);
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

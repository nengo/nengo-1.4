package ca.neo.ui;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import org.python.util.PythonInterpreter;

import ca.neo.config.ClassRegistry;
import ca.neo.config.ConfigUtil;
import ca.neo.config.JavaSourceParser;
import ca.neo.model.Network;
import ca.neo.model.Node;
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
import ca.neo.ui.script.ScriptConsole;
import ca.neo.ui.script.ScriptEditor;
import ca.neo.ui.util.NeoFileChooser;
import ca.neo.util.Environment;
import ca.shu.ui.lib.AppFrame;
import ca.shu.ui.lib.AuxillarySplitPane;
import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.SetSplitPaneVisibleAction;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.models.ModelObject;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.util.menus.MenuBuilder;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.WorldObject.Property;
import ca.shu.ui.lib.world.handlers.SelectionHandler;
import ca.shu.ui.lib.world.piccolo.primitives.Universe;

/**
 * Top level instance of the NeoGraphics application
 * 
 * @author Shu Wu
 */
/**
 * @author User
 */
public class NeoGraphics extends AppFrame implements INodeContainer {
	private static final String APP_NAME = "NEO Graphics V1 Beta";

	private static final long serialVersionUID = 1L;

	/**
	 * Description of NeoGraphics to be shown in the "About" Dialog box
	 */
	public static final String ABOUT = APP_NAME + "<BR><BR>"
			+ "(c) Copyright Center for Theoretical Neuroscience 2007.  All rights reserved<BR>"
			+ "Visit http://ctn.uwaterloo.ca/<BR>"
			+ "<BR> User Interface by Shu Wu (shuwu83@gmail.com)";

	public static final String CONFIG_FILE = "NeoGraphics.config";

	public static final boolean CONFIGURE_PLANE_ENABLED = false;

	/**
	 * UI delegate object used to show the FileChooser
	 */
	public static NeoFileChooser FileChooser;

	/**
	 * File extension for Neo Nodes
	 */
	public static final String NEONODE_FILE_EXTENSION = "neonode";

	static final String PLUGIN_DIRECTORY = "plugins";

	public static NeoGraphics getInstance() {
		Util.Assert(UIEnvironment.getInstance() instanceof NeoGraphics);
		return (NeoGraphics) UIEnvironment.getInstance();
	}

	/**
	 * Runs NeoGraphics with a default name
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new NeoGraphics();
	}

	private ConfigurationPane configPane;

	private AuxillarySplitPane dataViewerPane;

	private ScriptConsole scriptConsole;

	private AuxillarySplitPane scriptConsolePane;

	/**
	 * Data viewer data
	 */
	private SimulatorDataModel simulationData;

	private ArrayList<AuxillarySplitPane> splitPanes;

	/**
	 * @param auxTitle
	 *            Text to be shown in the Title Bar
	 */
	public NeoGraphics() {
		super();
	}

	private void loadConfig() {
		String simulatorSource = "../simulator/src/java/main";

		try {
			FileInputStream fis = new FileInputStream(CONFIG_FILE);
			Properties props = new Properties();
			props.load(fis);

			simulatorSource = props.getProperty("simulator_source");
		} catch (IOException e) {
			Util.debugMsg("Problem loading config file");
		}
		File simulatorSourceFile = new File(simulatorSource);
		if (!simulatorSourceFile.exists()) {
			Util.debugMsg("Could not find simulator source files");
		}

		JavaSourceParser.addSource(simulatorSourceFile);
	}

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

								// Util.debugMsg("Registering class: " +
								// newClass.getName());
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

	@Override
	protected void initialize() {
		super.initialize();

		UIEnvironment.setDebugEnabled(true);

		loadConfig();

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

		/*
		 * Attach listeners for Script Console
		 */
		initScriptConsoleListeners();

	}

	private void initScriptConsoleListeners() {
		SelectionHandler.addSelectionListener(new SelectionHandler.SelectionListener() {

			public void singleObjectSelected(WorldObject obj) {
				while (obj != null && !(obj instanceof ModelObject)) {
					obj = obj.getParent();
				}

				if (obj != null) {
					Object model = ((ModelObject)obj).getModel();
					scriptConsole.setCurrentObject(model);
				}
			}
		});

		getWorld().getGround().addChildListener(new WorldObject.ChildListener() {

			public void childAdded(WorldObject wo) {
				if (wo instanceof UINetwork) {
					final UINetwork networkUI = ((UINetwork) wo);
					final Network network = networkUI.getModel();

					try {
						scriptConsole.addVariable(network.getName(), network);

						networkUI.addPropertyChangeListener(Property.REMOVED_FROM_WORLD,
								new WorldObject.Listener() {
									public void propertyChanged(Property event) {
										scriptConsole.removeVariable(network.getName());
										networkUI.removePropertyChangeListener(
												Property.REMOVED_FROM_WORLD, this);
									}
								});

					} catch (Exception e) {
						UserMessages.showError("Error adding network: " + e.getMessage());
					}
				}
			}

			public void childRemoved(WorldObject wo) {
				/*
				 * Do nothing here. We don't remove the variable here directly
				 * because the network has already been destroyed and no longer
				 * has a reference to it's model.
				 */
			}

		});

	}

	@Override
	protected void initLayout(Universe canvas) {
		splitPanes = new ArrayList<AuxillarySplitPane>();
		simulationData = new SimulatorDataModel();

		scriptConsole = new ScriptConsole(new PythonInterpreter());
		Style.applyStyle(scriptConsole);

		/*
		 * Create nested split panes
		 */
		configPane = new ConfigurationPane(canvas);
		scriptConsolePane = new AuxillarySplitPane(configPane.toJComponent(), scriptConsole,
				"Script Console", AuxillarySplitPane.Orientation.Bottom);

		dataViewerPane = new AuxillarySplitPane(scriptConsolePane,
				new DataListView(simulationData), "Data Viewer",
				AuxillarySplitPane.Orientation.Left);

		splitPanes.add(scriptConsolePane);
		splitPanes.add(dataViewerPane);

		if (CONFIGURE_PLANE_ENABLED) {
			splitPanes.add(configPane.toJComponent());
		}

		getContentPane().add(dataViewerPane);

		canvas.requestFocus();

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.models.INodeContainer#addNeoNode(ca.neo.ui.models.UINeoNode)
	 */
	public void addNeoNode(UINeoNode node) {
		getWorld().getGround().addObject(node);
		if (node instanceof NodeContainer) {
			((NodeContainer) (node)).openViewer();
		}
	}

	public void captureInDataViewer(Network network) {
		simulationData.captureData(network);
	}

	/**
	 * @param obj
	 *            Object to configure
	 */
	public void configureObject(Object obj) {
		if (CONFIGURE_PLANE_ENABLED) {
			configPane.configureObj(obj);
		} else {
			ConfigUtil.configure(UIEnvironment.getInstance(), obj);
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

	@Override
	public String getAboutString() {
		return ABOUT;
	}

	@Override
	public String getAppName() {
		return APP_NAME;
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

		viewMenu.addAction(new OpenScriptEditor("Open Script Editor"), KeyEvent.VK_E);

		for (AuxillarySplitPane splitPane : splitPanes) {

			viewMenu.addAction(new SetSplitPaneVisibleAction("Show " + splitPane.getAuxTitle(),
					splitPane, true));

		}
	}

	public void setDataViewerVisible(boolean isVisible) {
		dataViewerPane.setAuxVisible(isVisible);
	}

	class OpenScriptEditor extends StandardAction {

		private static final long serialVersionUID = 1L;

		public OpenScriptEditor(String description) {
			super(description);
		}

		@Override
		protected void action() throws ActionException {
			ScriptEditor.openEditor();
		}

	}

}

class ConfigurationPane {
	private static final long serialVersionUID = 1L;

	AuxillarySplitPane auxSplitPane;

	public ConfigurationPane(Container mainPanel) {
		super();
		auxSplitPane = new AuxillarySplitPane(mainPanel, null, "Configuration",
				AuxillarySplitPane.Orientation.Right);

	}

	public void configureObj(Object obj) {

		ConfigUtil.ConfigurationPane configurationPane = ConfigUtil.createConfigurationPane(obj);
		// Style.applyStyle(configurationPane.getTree());
		// Style.applyStyle(configurationPane.getCellRenderer());

		String name;
		if (obj instanceof Node) {
			name = ((Node) obj).getName();
		} else {
			name = "Configuration";
		}
		auxSplitPane.setAuxPane(configurationPane, name + " (" + obj.getClass().getSimpleName()
				+ ")");

	}

	public AuxillarySplitPane toJComponent() {
		return auxSplitPane;
	}
}
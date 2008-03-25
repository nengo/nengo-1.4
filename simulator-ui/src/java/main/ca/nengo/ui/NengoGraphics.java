package ca.nengo.ui;

import java.awt.Container;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
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

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.python.util.PythonInterpreter;

import ca.nengo.config.ClassRegistry;
import ca.nengo.config.ConfigUtil;
import ca.nengo.config.JavaSourceParser;
import ca.nengo.model.Network;
import ca.nengo.model.Node;
import ca.nengo.ui.actions.CopyAction;
import ca.nengo.ui.actions.CreateModelAction;
import ca.nengo.ui.actions.CutAction;
import ca.nengo.ui.actions.OpenNeoFileAction;
import ca.nengo.ui.actions.PasteAction;
import ca.nengo.ui.actions.RunSimulatorAction;
import ca.nengo.ui.actions.SaveNodeAction;
import ca.nengo.ui.dataList.DataListView;
import ca.nengo.ui.dataList.SimulatorDataModel;
import ca.nengo.ui.models.NodeContainer;
import ca.nengo.ui.models.UINeoNode;
import ca.nengo.ui.models.constructors.ConstructableNode;
import ca.nengo.ui.models.constructors.ModelFactory;
import ca.nengo.ui.models.nodes.UINetwork;
import ca.nengo.ui.script.ScriptConsole;
import ca.nengo.ui.script.ScriptEditor;
import ca.nengo.ui.util.NengoClipboard;
import ca.nengo.ui.util.NeoFileChooser;
import ca.nengo.ui.util.ScriptWorldWrapper;
import ca.nengo.util.Environment;
import ca.shu.ui.lib.AppFrame;
import ca.shu.ui.lib.AuxillarySplitPane;
import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.DisabledAction;
import ca.shu.ui.lib.actions.DragAction;
import ca.shu.ui.lib.actions.SetSplitPaneVisibleAction;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.misc.ShortcutKey;
import ca.shu.ui.lib.objects.models.ModelObject;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.util.menus.MenuBuilder;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.WorldObject.Property;
import ca.shu.ui.lib.world.handlers.SelectionHandler;
import ca.shu.ui.lib.world.piccolo.objects.SelectionBorder;
import ca.shu.ui.lib.world.piccolo.objects.Window;
import ca.shu.ui.lib.world.piccolo.primitives.Universe;

/**
 * Top level instance of the NeoGraphics application
 * 
 * @author Shu Wu
 */
/**
 * @author User
 */
public class NengoGraphics extends AppFrame implements NodeContainer {
	private static final long serialVersionUID = 1L;
	public static final double VERSION = 1.0;

	public static final String APP_NAME = "Nengo V" + VERSION;
	/**
	 * Description of NeoGraphics to be shown in the "About" Dialog box
	 */
	public static final String ABOUT = "<H3>" + APP_NAME + "</H3>"
			+ "<p>(c) Copyright Bryan Tripp & Centre for Theoretical Neuroscience (ctn.uwaterloo.ca) 2006-2008.  All rights reserved.<BR><BR>"
			+ "User Interface by Shu Wu (shuwu83@gmail.com) <BR><BR>"
			+ "This product contains several open-source libraries (copyright their respective authors). "
			+ "For more information, consult lib/library-licenses.txt in the installation directory.<BR> "
			+ "This product includes software developed by The Apache Software Foundation (http://www.apache.org/).</p>";

	public static final String CONFIG_FILE = "NengoGraphics.config";

	public static final boolean CONFIGURE_PLANE_ENABLED = false;

	/**
	 * UI delegate object used to show the FileChooser
	 */
	public static NeoFileChooser FileChooser;

	/**
	 * File extension for Neo Nodes
	 */
	public static final String NEONODE_FILE_EXTENSION = "nef";

	static final String PLUGIN_DIRECTORY = "plugins";

	public static NengoGraphics getInstance() {
		Util.Assert(UIEnvironment.getInstance() instanceof NengoGraphics);
		return (NengoGraphics) UIEnvironment.getInstance();
	}

	/**
	 * Runs NeoGraphics with a default name
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new NengoGraphics();

		try {
			Image image = ImageIO.read(frame.getClass().getClassLoader().getResource(
					"ca/nengo/ui/spikepattern-black.png"));
			frame.setIconImage(image);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private NengoClipboard clipboard;

	private ConfigurationPane configPane;

	private AuxillarySplitPane dataViewerPane;
	private SelectionBorder objectSelectedBorder;

	private PythonInterpreter pythonInterpreter;
	private ScriptConsole scriptConsole;

	private AuxillarySplitPane scriptConsolePane;

	private WorldObject selectedObj;

	/**
	 * Data viewer data
	 */
	private SimulatorDataModel simulationData;

	private ArrayList<AuxillarySplitPane> splitPanes;

	/**
	 * @param auxTitle
	 *            Text to be shown in the Title Bar
	 */
	public NengoGraphics() {
		super();
	}

	/**
	 * @returns Top Node Container available in the Application Window. Null, if
	 *          the Top Window is not a Node Container
	 */
	private NodeContainer getTopNodeContainer() {
		Window window = getTopWindow();
		NodeContainer nodeContainer = null;

		if (window != null) {
			WorldObject wo = window.getContents();
			if (wo instanceof NodeContainer) {
				nodeContainer = (NodeContainer) wo;
			}
		} else {
			nodeContainer = this;
		}

		return nodeContainer;
	}

	private void initScriptConsole() {
		scriptConsole.addVariable("world", new ScriptWorldWrapper(this));

		/*
		 * Add listeners
		 */
		SelectionHandler.addSelectionListener(new SelectionHandler.SelectionListener() {

			public void objectFocused(WorldObject obj) {
				objectSelected(obj);
			}
		});

		getWorld().getGround().addChildrenListener(new WorldObject.ChildListener() {

			public void childAdded(WorldObject wo) {
				if (wo instanceof ModelObject) {
					final ModelObject modelObject = ((ModelObject) wo);
					final Object model = modelObject.getModel();
					final String modelName = modelObject.getName();

					try {
						scriptConsole.addVariable(modelName, model);

						modelObject.addPropertyChangeListener(Property.REMOVED_FROM_WORLD,
								new WorldObject.Listener() {
									public void propertyChanged(Property event) {
										scriptConsole.removeVariable(modelName);
										modelObject.removePropertyChangeListener(
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
	protected void constructShortcutKeys(LinkedList<ShortcutKey> shortcuts) {
		super.constructShortcutKeys(shortcuts);
		shortcuts.add(new ShortcutKey(MENU_SHORTCUT_KEY_MASK, KeyEvent.VK_P,
				new SetSplitPaneVisibleAction("Focus on script console", scriptConsolePane, true)));

	}

	private void loadConfig() {
		String simulatorSource = "../simulator/src/java/main";

		try {
			FileInputStream fis = new FileInputStream(CONFIG_FILE);
			Properties props = new Properties();
			props.load(fis);

			simulatorSource = props.getProperty("simulator_source");
		} catch (IOException e) {
			Util.debugMsg("Problem loading config file: " + e.getMessage());
		}
		File simulatorSourceFile = new File(simulatorSource);
		if (!simulatorSourceFile.exists()) {
			Util.debugMsg("Could not find simulator source files at "
					+ simulatorSourceFile.getAbsoluteFile().toString());
		}

		JavaSourceParser.addSource(simulatorSourceFile);
	}

	private void objectSelected(WorldObject obj) {
		while (obj != null && !(obj instanceof ModelObject)) {
			obj = obj.getParent();
		}

		if (obj != null) {

			selectedObj = obj;

			if (objectSelectedBorder != null) {
				objectSelectedBorder.destroy();
			}

			objectSelectedBorder = new SelectionBorder(obj.getWorld(), obj);

			Object model = ((ModelObject) obj).getModel();
			scriptConsole.setCurrentObject(model);
		} else {
			selectedObj = null;
		}
		updateEditMenu();
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
		clipboard = new NengoClipboard();
		clipboard.addClipboardListener(new NengoClipboard.ClipboardListener() {

			public void clipboardChanged() {
				updateEditMenu();
			}

		});

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
		initScriptConsole();

	}

	@Override
	protected void initLayout(Universe canvas) {
		splitPanes = new ArrayList<AuxillarySplitPane>();
		simulationData = new SimulatorDataModel();

		pythonInterpreter = new PythonInterpreter();
		scriptConsole = new ScriptConsole(pythonInterpreter);
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

	@Override
	protected void updateEditMenu() {
		super.updateEditMenu();

		StandardAction copyAction = null;
		StandardAction cutAction = null;
		StandardAction pasteAction = null;

		if (selectedObj != null && selectedObj instanceof UINeoNode) {
			cutAction = new CutAction("Cut", (UINeoNode) selectedObj);
			copyAction = new CopyAction("Copy", (UINeoNode) selectedObj);
		} else {
			cutAction = new DisabledAction("Cut", "No object selected");
			copyAction = new DisabledAction("Copy", "No object selected");
		}

		Node node = getClipboard().getContents();
		if (node != null) {
			pasteAction = new PasteAction("Paste", this);
		} else {
			pasteAction = new DisabledAction("Paste", "No object is in the clipboard");
		}

		editMenu.addAction(copyAction, KeyEvent.VK_C, KeyStroke.getKeyStroke(KeyEvent.VK_C,
				MENU_SHORTCUT_KEY_MASK));
		editMenu.addAction(cutAction, KeyEvent.VK_X, KeyStroke.getKeyStroke(KeyEvent.VK_X,
				MENU_SHORTCUT_KEY_MASK));
		editMenu.addAction(pasteAction, KeyEvent.VK_V, KeyStroke.getKeyStroke(KeyEvent.VK_V,
				MENU_SHORTCUT_KEY_MASK));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.nengo.ui.models.INodeContainer#addNeoNode(ca.nengo.ui.models.UINeoNode)
	 */
	public UINeoNode addNodeModel(Node node) throws ContainerException {
		return addNodeModel(node, null, null);
	}

	public UINeoNode addNodeModel(Node node, Double posX, Double posY) throws ContainerException {
		NodeContainer nodeContainer = getTopNodeContainer();
		if (nodeContainer != this && nodeContainer != null) {
			// Delegate to the top Node Container in the Application
			return nodeContainer.addNodeModel(node, posX, posY);
		} else if (nodeContainer == this) {

			UINeoNode nodeUI = UINeoNode.createNodeUI(node);

			if (posX != null && posY != null) {
				nodeUI.setOffset(posX, posY);

				getWorld().getGround().addChild(nodeUI);
			} else {
				getWorld().getGround().addChildFancy(nodeUI);
			}

			DragAction.dropNode(nodeUI);
			return nodeUI;
		} else {
			throw new ContainerException("There are no containers to put this node");
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
		return "Nengo Workspace";
	}

	public NengoClipboard getClipboard() {
		return clipboard;
	}

	public Node getNodeModel(String name) {
		NodeContainer nodeContainer = getTopNodeContainer();
		if (nodeContainer != this && nodeContainer != null) {
			// Delegate to the top Node Container in the Application
			return nodeContainer.getNodeModel(name);
		} else if (nodeContainer == this) {
			for (WorldObject wo : getWorld().getGround().getChildren()) {
				if (wo instanceof UINeoNode) {
					UINeoNode nodeUI = (UINeoNode) wo;

					if (nodeUI.getName().equals(name)) {
						return nodeUI.getModel();
					}
				}
			}
		}
		return null;
	}

	public PythonInterpreter getPythonInterpreter() {
		return pythonInterpreter;
	}

	public WorldObject getSelectedObj() {
		return selectedObj;
	}

	@Override
	public void initFileMenu(MenuBuilder fileMenu) {

		MenuBuilder newMenu = fileMenu.addSubMenu("New");
		newMenu.getJMenu().setMnemonic(KeyEvent.VK_N);

		for (ConstructableNode constructable : ModelFactory.getNodeConstructables(this)) {
			newMenu.addAction(new CreateModelAction(this, constructable));
		}

		fileMenu.addAction(new OpenNeoFileAction(this), KeyEvent.VK_O, KeyStroke.getKeyStroke(
				KeyEvent.VK_O, MENU_SHORTCUT_KEY_MASK));

		fileMenu.addAction(new SaveNetworkAction("Save Network"), KeyEvent.VK_S, KeyStroke
				.getKeyStroke(KeyEvent.VK_S, MENU_SHORTCUT_KEY_MASK));
		fileMenu.addAction(new RunNetworkAction("Run Network"), KeyEvent.VK_R, KeyStroke
				.getKeyStroke(KeyEvent.VK_R, MENU_SHORTCUT_KEY_MASK));
	}

	@Override
	public void initViewMenu(JMenuBar menuBar) {

		MenuBuilder viewMenu = new MenuBuilder("View");
		viewMenu.getJMenu().setMnemonic(KeyEvent.VK_V);
		menuBar.add(viewMenu.getJMenu());

		viewMenu.addAction(new OpenScriptEditor("Open Script Editor"), KeyEvent.VK_E, KeyStroke
				.getKeyStroke(KeyEvent.VK_E, MENU_SHORTCUT_KEY_MASK));

		int count = 1;
		for (AuxillarySplitPane splitPane : splitPanes) {
			byte shortCutChar = splitPane.getAuxTitle().getBytes()[0];

			viewMenu.addAction(
					new ToggleScriptPane("Toggle " + splitPane.getAuxTitle(), splitPane),
					shortCutChar, KeyStroke.getKeyStroke(0x30 + count++, MENU_SHORTCUT_KEY_MASK));

		}
	}

	public Point2D localToView(Point2D localPoint) {
		localPoint = getWorld().getSky().parentToLocal(localPoint);
		localPoint = getWorld().getSky().localToView(localPoint);
		return localPoint;
	}

	public boolean removeNodeModel(Node node) {
		ModelObject modelToDestroy = null;
		for (WorldObject wo : getWorld().getGround().getChildren()) {
			if (wo instanceof ModelObject) {
				ModelObject modelObject = (ModelObject) wo;

				if (modelObject.getModel() == node) {
					modelToDestroy = modelObject;
					break;
				}
			}
		}
		if (modelToDestroy != null) {
			modelToDestroy.destroyModel();
			return true;
		} else {
			return false;
		}
	}

	public void setDataViewerVisible(boolean isVisible) {
		dataViewerPane.setAuxVisible(isVisible);
	}

	public class ToggleScriptPane extends StandardAction {

		private static final long serialVersionUID = 1L;
		private AuxillarySplitPane splitPane;

		public ToggleScriptPane(String description, AuxillarySplitPane spliPane) {
			super(description);
			this.splitPane = spliPane;
		}

		@Override
		protected void action() throws ActionException {
			splitPane.setAuxVisible(!splitPane.isAuxVisible());
		}

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

/**
 * Runs the closest network to the currently selected obj
 * 
 * @author Shu Wu
 */
class RunNetworkAction extends StandardAction {

	private static final long serialVersionUID = 1L;

	public RunNetworkAction(String description) {
		super(description);

	}

	@Override
	protected void action() throws ActionException {
		WorldObject selectedNode = NengoGraphics.getInstance().getSelectedObj();

		UINetwork selectedNetwork = UINetwork.getClosestNetwork(selectedNode);
		if (selectedNetwork != null) {

			RunSimulatorAction runAction = new RunSimulatorAction("run", selectedNetwork);
			runAction.doAction();

		} else {
			throw new ActionException("No parent network to run, please select a node");
		}
	}

}

/**
 * Saves the closest network to the currently selected object
 * 
 * @author Shu Wu
 */
class SaveNetworkAction extends StandardAction {

	private static final long serialVersionUID = 1L;

	public SaveNetworkAction(String description) {
		super(description);
	}

	@Override
	protected void action() throws ActionException {
		WorldObject selectedNode = NengoGraphics.getInstance().getSelectedObj();

		UINetwork selectedNetwork = UINetwork.getClosestNetwork(selectedNode);
		if (selectedNetwork != null) {

			SaveNodeAction saveNodeAction = new SaveNodeAction(selectedNetwork);
			saveNodeAction.doAction();

		} else {
			throw new ActionException("No parent network to save, please select a node");
		}
	}
}

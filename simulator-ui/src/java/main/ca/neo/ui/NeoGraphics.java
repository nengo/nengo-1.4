package ca.neo.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import ca.neo.config.ClassRegistry;
import ca.neo.config.JavaSourceParser;
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
import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.util.menus.MenuBuilder;
import ca.shu.ui.lib.world.WorldObject;
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
	private static final long serialVersionUID = 1L;

	/**
	 * Description of NeoGraphics to be shown in the "About" Dialog box
	 */
	public static final String ABOUT = "NEO Graphics<BR><BR>"
			+ "(c) Copyright Center for Theoretical Neuroscience 2007.  All rights reserved<BR>"
			+ "Visit http://ctn.uwaterloo.ca/<BR>"
			+ "<BR> User Interface by Shu Wu (shuwu83@gmail.com)";

	public static final String CONFIG_FILE = "NeoGraphics.config";

	/**
	 * UI delegate object used to show the FileChooser
	 */
	public static NeoFileChooser FileChooser;

	/**
	 * File extension for Neo Nodes
	 */
	public static final String NEONODE_FILE_EXTENSION = "neonode";

	static final String PLUGIN_DIRECTORY = "plugins";

	/**
	 * Runs NeoGraphics with a default name
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new NeoGraphics();
	}

	private Container dataViewerPane;

	private JSplitPane mainPane;
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
	}

	class HideDataViewerListener implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			setDataViewerVisible(false);
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

	}

	private void initDataViewer(Container dataViewerContainer) {
		dataViewerContainer.setLayout(new BorderLayout());
		simulationData = new SimulatorDataModel();

		/*
		 * Create title bar
		 */
		JPanel titleBar = new JPanel();
		titleBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		Style.applyStyle(titleBar);
		titleBar.setBackground(Style.COLOR_BACKGROUND2);
		titleBar.setOpaque(true);
		titleBar.setLayout(new BorderLayout());

		JLabel dataViewerLabel = new JLabel("Data Viewer");
		titleBar.add(dataViewerLabel, BorderLayout.WEST);
		dataViewerLabel.setFont(Style.FONT_BIG);
		Style.applyStyle(dataViewerLabel);
		JLabel hideButton = new JLabel("<< ");
		titleBar.add(hideButton, BorderLayout.EAST);
		Style.applyStyle(hideButton);
		// hideButton.setForeground(Color.gray);

		hideButton.addMouseListener(new HideDataViewerListener());

		dataViewerContainer.add(titleBar, BorderLayout.NORTH);

		/*
		 * Create data viewer
		 */
		dataViewerContainer.setMinimumSize(new Dimension(200, 200));
		DataListView dataListViewer = new DataListView(simulationData);
		Style.applyStyle(dataListViewer);

		dataViewerContainer.add(dataListViewer, BorderLayout.CENTER);
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
	protected void initLayout(Universe canvas) {

		mainPane = new JSplitPane();
		Style.applyStyle(mainPane);

		dataViewerPane = new JPanel();

		Style.applyStyle(dataViewerPane);
		initDataViewer(dataViewerPane);

		mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, dataViewerPane, canvas);
		mainPane.setOneTouchExpandable(true);
		mainPane.setBorder(null);

		mainPane.setDividerSize(2);

		Container cp = getContentPane();
		// cp.setLayout(new BorderLayout());
		cp.add(mainPane);

		canvas.requestFocus();
		setDataViewerVisible(false);

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
		if (node instanceof NodeContainer) {
			((NodeContainer) (node)).openViewer();
		}
		getWorld().getGround().addObject(node);

	}

	public void captureInDataViewer(Network network) {
		simulationData.captureData(network);
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

		MenuBuilder dataViewerMenu = viewMenu.addSubMenu("Data viewer");
		dataViewerMenu.addAction(new SetDataViewerVisibleAction("Show data viewer", true),
				KeyEvent.VK_N);

		dataViewerMenu.addAction(new SetDataViewerVisibleAction("Hide data viewer", false),
				KeyEvent.VK_N);

	}

	public boolean isDataViewerVisible() {
		if (mainPane.getDividerLocation() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public void setDataViewerVisible(boolean isVisible) {
		if (isVisible) {
			int dividerLocation = mainPane.getDividerLocation();
			if (dividerLocation < dataViewerPane.getMinimumSize().getWidth()) {
				mainPane.setDividerLocation((int) dataViewerPane.getMinimumSize().getWidth());
			}
			mainPane.setDividerSize(2);
			dataViewerPane.setVisible(true);
		} else {
			dataViewerPane.setVisible(false);
			mainPane.setDividerLocation(0);
			mainPane.setDividerSize(0);
		}
	}

	public class SetDataViewerVisibleAction extends StandardAction {

		private static final long serialVersionUID = 1L;
		private boolean visible;

		public SetDataViewerVisibleAction(String actionName, boolean visible) {
			super(actionName);
			this.visible = visible;
		}

		@Override
		protected void action() throws ActionException {
			setDataViewerVisible(visible);
		}

	}

}

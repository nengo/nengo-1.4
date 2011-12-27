/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "NengoGraphics.java". Description:
"@author User"

The Initial Developer of the Original Code is Bryan Tripp & Centre for Theoretical Neuroscience, University of Waterloo. Copyright (C) 2006-2008. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of the GNU
Public License license (the GPL License), in which case the provisions of GPL
License are applicable  instead of those above. If you wish to allow use of your
version of this file only under the terms of the GPL License and not to allow
others to use your version of this file under the MPL, indicate your decision
by deleting the provisions above and replace  them with the notice and other
provisions required by the GPL License.  If you do not delete the provisions above,
a recipient may use your version of this file under either the MPL or the GPL License.
 */

package ca.nengo.ui;

//import java.awt.Color;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import org.python.util.PythonInterpreter;

import ca.nengo.config.ConfigUtil;
import ca.nengo.config.JavaSourceParser;
import ca.nengo.model.Network;
import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.Termination;
import ca.nengo.ui.actions.CopyAction;
import ca.nengo.ui.actions.CreateModelAction;
import ca.nengo.ui.actions.CutAction;
import ca.nengo.ui.actions.OpenNeoFileAction;
import ca.nengo.ui.actions.PasteAction;
import ca.nengo.ui.actions.RemoveModelAction;
import ca.nengo.ui.actions.RunSimulatorAction;
import ca.nengo.ui.actions.SaveNodeAction;
import ca.nengo.ui.dataList.DataListView;
import ca.nengo.ui.dataList.SimulatorDataModel;
import ca.nengo.ui.lib.AppFrame;
import ca.nengo.ui.lib.AuxillarySplitPane;
import ca.nengo.ui.lib.Style.NengoStyle;
import ca.nengo.ui.lib.actions.ActionException;
import ca.nengo.ui.lib.actions.DisabledAction;
import ca.nengo.ui.lib.actions.DragAction;
import ca.nengo.ui.lib.actions.SetSplitPaneVisibleAction;
import ca.nengo.ui.lib.actions.StandardAction;
import ca.nengo.ui.lib.misc.ShortcutKey;
import ca.nengo.ui.lib.objects.models.ModelObject;
import ca.nengo.ui.lib.util.UIEnvironment;
import ca.nengo.ui.lib.util.UserMessages;
import ca.nengo.ui.lib.util.Util;
import ca.nengo.ui.lib.util.menus.MenuBuilder;
import ca.nengo.ui.lib.world.WorldObject;
import ca.nengo.ui.lib.world.WorldObject.Property;
import ca.nengo.ui.lib.world.elastic.ElasticWorld;
import ca.nengo.ui.lib.world.handlers.SelectionHandler;
import ca.nengo.ui.lib.world.piccolo.objects.SelectionBorder;
import ca.nengo.ui.lib.world.piccolo.objects.Window;
import ca.nengo.ui.lib.world.piccolo.primitives.Universe;
import ca.nengo.ui.models.NodeContainer;
import ca.nengo.ui.models.UINeoNode;
import ca.nengo.ui.models.constructors.CNetwork;
import ca.nengo.ui.models.nodes.UINetwork;
import ca.nengo.ui.script.ScriptConsole;
import ca.nengo.ui.script.ScriptEditor;
import ca.nengo.ui.util.NengoClipboard;
import ca.nengo.ui.util.NengoConfigManager;
import ca.nengo.ui.util.NengoConfigManager.UserProperties;
import ca.nengo.ui.util.NeoFileChooser;
import ca.nengo.ui.util.ScriptWorldWrapper;
import ca.nengo.ui.world.NengoWorld;
import ca.nengo.util.Environment;

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

    /**
     * Nengo version number, no real rhyme or reason to it
     */
    public static final double VERSION = 1.3;

    /**
     * String used in the UI to identify Nengo
     */
    public static final String APP_NAME = "Nengo V" + VERSION;

    /**
     * Description of Nengo to be shown in the "About" Dialog box
     */
    public static final String ABOUT = "<H3>"
            + APP_NAME
            + "</H3>"
            + "<p>(c) Copyright Bryan Tripp & Centre for Theoretical Neuroscience (ctn.uwaterloo.ca) 2006-2009.<BR><BR>"
            + "User Interface by Shu Wu (shuwu83@gmail.com) <BR><BR>"
            + "This product contains several open-source libraries (copyright their respective authors). "
            + "For more information, consult lib/library-licenses.txt in the installation directory.<BR> "
            + "This product includes software developed by The Apache Software Foundation (http://www.apache.org/).</p>";

    /**
     * Use the configure panel in the right side? Otherwise it's a pop-up.
     */
    public static final boolean CONFIGURE_PLANE_ENABLED = true;

    /**
     * UI delegate object used to show the FileChooser
     */
    public static NeoFileChooser FileChooser;

    /**
     * File extension for Nengo Nodes
     */
    public static final String NEONODE_FILE_EXTENSION = "nef";

    //	public static final String PLUGIN_DIRECTORY = "plugins";

    /**
     * @return The singleton instance of the NengoGraphics object
     */
    public static NengoGraphics getInstance() {
        Util.Assert(UIEnvironment.getInstance() instanceof NengoGraphics);
        return (NengoGraphics) UIEnvironment.getInstance();
    }

    /**
     * Runs NengoGraphics with a default name
     * 
     * @param args
     */
    public static void main(String[] args) {
        new NengoGraphics();
    }

    private NengoClipboard clipboard;
    private ConfigurationPane configPane;
    private AuxillarySplitPane dataViewerPane;
    private SelectionBorder objectSelectedBorder;
    private PythonInterpreter pythonInterpreter;
    private ScriptConsole scriptConsole;
    private AuxillarySplitPane scriptConsolePane;
    private WorldObject selectedObj;
    private DataListView dataListViewer;
    private ArrayList<AuxillarySplitPane> splitPanes;

    /**
     * Constructor; displays a splash screen
     */
    public NengoGraphics() {
        super();

        // Setup icon
        try {
            Image image = ImageIO.read(getClass().getClassLoader().getResource("ca/nengo/ui/spikepattern-black.png"));
            setIconImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return Top Node Container available in the Application Window. Null, if
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
                                modelObject.removePropertyChangeListener(Property.REMOVED_FROM_WORLD,
                                        this);
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

    private void initializeSimulatorSourceFiles() {

        String savedSourceLocation = NengoConfigManager.getNengoConfig().getProperty("simulator_source");

        String simulatorSource = (savedSourceLocation != null) ? savedSourceLocation
                : "../simulator/src/java/main";

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

            if (configPane.toJComponent().isAuxVisible()) {
                configPane.configureObj(model);
            }

        } else {
            selectedObj = null;
        }


        updateEditMenu();
    }

    //	/**
    //	 * Register plugins
    //	 */
    //	private void registerPlugins() {
    //		try {
    //			LinkedList<URL> pluginUrls = new LinkedList<URL>();
    //			LinkedList<JarFile> pluginJars = new LinkedList<JarFile>();
    //
    //			File pluginDir = new File(PLUGIN_DIRECTORY);
    //			pluginUrls.add(pluginDir.toURI().toURL());
    //
    //			File[] pluginJarFiles = pluginDir.listFiles(new FilenameFilter() {
    //				public boolean accept(File dir, String name) {
    //					if (name.endsWith("jar")) {
    //						return true;
    //					} else {
    //						return false;
    //					}
    //				}
    //			});
    //
    //			for (File jarFile : pluginJarFiles) {
    //				pluginUrls.add(jarFile.toURI().toURL());
    //			}
    //
    //			URL[] pluginUrlsArray = pluginUrls.toArray(new URL[] {});
    //
    //			URLClassLoader urlClassLoader = new URLClassLoader(pluginUrlsArray);
    //
    //			/*
    //			 * Loads all classes in each plugin jar
    //			 */
    //			for (File jarFile : pluginJarFiles) {
    //				try {
    //					JarFile jar = new JarFile(jarFile);
    //					pluginJars.add(jar);
    //					Enumeration<JarEntry> entries = jar.entries();
    //					while (entries.hasMoreElements()) {
    //						JarEntry entry = entries.nextElement();
    //						String fileName = entry.getName();
    //						if (fileName.endsWith(".class")) {
    //							String className = "";
    //							try {
    //								className = fileName.substring(0, fileName.lastIndexOf('.')).replace('/',
    //										'.');// .replace('$',
    //								// '.');
    //								Class<?> newClass = urlClassLoader.loadClass(className);
    //
    //								// Util.debugMsg("Registering class: " +
    //								// newClass.getName());
    //								ClassRegistry.getInstance().register(newClass);
    //
    //							} catch (ClassNotFoundException e) {
    //								e.printStackTrace();
    //							} catch (NoClassDefFoundError e) {
    //								// this only occurs for nested classes (i.e.
    //								// those with dollar signs in class name),
    //								// and perhaps only on the Mac
    //
    //								// System.out.println(className);
    //								// e.printStackTrace();
    //							}
    //						}
    //					}
    //
    //					pluginUrls.add(jarFile.toURI().toURL());
    //				} catch (IOException e) {
    //					e.printStackTrace();
    //				}
    //			}
    //
    //		} catch (MalformedURLException e) {
    //			e.printStackTrace();
    //		}
    //	}

    protected NengoWorld getNengoWorld() {
        return (NengoWorld) getWorld();
    }

    @Override
    protected void constructShortcutKeys(LinkedList<ShortcutKey> shortcuts) {
        super.constructShortcutKeys(shortcuts);
        shortcuts.add(new ShortcutKey(MENU_SHORTCUT_KEY_MASK, KeyEvent.VK_P,
                new SetSplitPaneVisibleAction("Focus on script console", scriptConsolePane, true)));

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

        //UIEnvironment.setDebugEnabled(true);

        initializeSimulatorSourceFiles();

        if (FileChooser == null) {
            FileChooser = new NeoFileChooser();
        }

        /*
         * Set up Environment variables
         */
        Environment.setUserInterface(true);

        /*
         * Attach listeners for Script Console
         */
        initScriptConsole();

        /*
         * Register plugin classes
         */
        //		registerPlugins();

        setExtendedState(NengoConfigManager.getUserInteger(UserProperties.NengoWindowExtendedState,
                JFrame.MAXIMIZED_BOTH));
    }

    @Override
    protected void initLayout(Universe canvas) {
        try {
            //Tell the UIManager to use the platform look and feel
            String laf=UIManager.getSystemLookAndFeelClassName();
            if (laf.equals("com.sun.java.swing.plaf.gtk.GTKLookAndFeel")) {
                laf="javax.swing.plaf.metal.MetalLookAndFeel";
            }
            UIManager.setLookAndFeel(laf);


            //UIManager.put("Slider.paintValue",Boolean.FALSE);
        } catch(Exception e) { /*Do nothing*/ }
        System.setProperty("swing.aatext", "true");

        splitPanes = new ArrayList<AuxillarySplitPane>();

        pythonInterpreter = new PythonInterpreter();
        scriptConsole = new ScriptConsole(pythonInterpreter);
        NengoStyle.applyStyle(scriptConsole);

        /*
         * Create nested split panes
         */
        configPane = new ConfigurationPane(canvas);
        scriptConsolePane = new AuxillarySplitPane(configPane.toJComponent(), scriptConsole,
                "Script Console", AuxillarySplitPane.Orientation.Bottom);

        dataListViewer = new DataListView(new SimulatorDataModel(),scriptConsole);

        dataViewerPane = new AuxillarySplitPane(scriptConsolePane, dataListViewer, "Data Viewer",
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
     * Prompt user to save models in NengoGraphics.
     * This is most likely called right before the application is exiting.
     */
    protected boolean promptToSaveModels() {
        boolean saveSuccessful = true;

        for (WorldObject wo : getWorld().getGround().getChildren()) {
            if (wo instanceof UINeoNode) {
                SaveNodeAction saveAction = new SaveNodeAction((UINeoNode) wo, true);
                saveAction.doAction();
                saveSuccessful = saveSuccessful && saveAction.getSaveSuccessful();
            }
        }

        return saveSuccessful;
    }

    @Override
    protected void updateEditMenu() {
        super.updateEditMenu();

        StandardAction copyAction = null;
        StandardAction cutAction = null;
        StandardAction pasteAction = null;
        StandardAction removeAction = null;

        if (selectedObj != null && selectedObj instanceof UINeoNode) {
            UINeoNode neoNode = (UINeoNode) selectedObj;

            cutAction = new CutAction("Cut", (UINeoNode) selectedObj);
            copyAction = new CopyAction("Copy", (UINeoNode) selectedObj);
            removeAction = new RemoveModelAction("Remove", neoNode);
        } else {
            cutAction = new DisabledAction("Cut", "No object selected");
            copyAction = new DisabledAction("Copy", "No object selected");
            removeAction = new DisabledAction("Copy", "No objects to remove");
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
        editMenu.addAction(removeAction, KeyEvent.VK_R, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,
                0));

    }

    @Override
    protected ElasticWorld createWorld() {
        return new NengoWorld();
    }

    /**
     * @see ca.nengo.ui.models.NodeContainer#addNodeModel(Node)
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
            UINeoNode nodeUI = getNengoWorld().addNodeModel(node, posX, posY);
            DragAction.dropNode(nodeUI);
            return nodeUI;
        } else {
            throw new ContainerException("There are no containers to put this node");
        }
    }

    /**
     * @param network TODO
     */
    public void captureInDataViewer(Network network) {
        dataListViewer.captureSimulationData(network);
    }

    /**
     * @param obj Object to configure
     */
    public void configureObject(Object obj) {
        if (CONFIGURE_PLANE_ENABLED) {
            configPane.toJComponent().setAuxVisible(true);
            configPane.configureObj(obj);
        } else {
            ConfigUtil.configure(UIEnvironment.getInstance(), obj);
        }
    }

    @Override
    public void exitAppFrame() {
        if (getWorld().getGround().getChildrenCount() > 0)
        {
            int response = JOptionPane.showConfirmDialog(this,
                    "Save models?",
                    "Exiting " + getAppName(),
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (response == JOptionPane.YES_OPTION)
            {
                if (!promptToSaveModels()) {
                    return;
                }
            }
            else if (response == JOptionPane.CANCEL_OPTION ||response == JOptionPane.CLOSED_OPTION)
            {
                /*
                 * Cancel exit
                 */
                return;
            }
        }

        saveUserConfig();
        super.exitAppFrame();
    }

    private void saveUserConfig() {
        NengoConfigManager.setUserProperty(UserProperties.NengoWindowExtendedState,
                getExtendedState());

        NengoConfigManager.saveUserConfig();
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

    /**
     * @return TODO
     */
    public NengoClipboard getClipboard() {
        return clipboard;
    }

    public Node getNodeModel(String name) {
        NodeContainer nodeContainer = getTopNodeContainer();
        if (nodeContainer != this && nodeContainer != null) {
            // Delegate to the top Node Container in the Application
            return nodeContainer.getNodeModel(name);
        } else if (nodeContainer == this) {
            return getNengoWorld().getNodeModel(name);
        }
        return null;
    }

    /**
     * @return TODO
     */
    public PythonInterpreter getPythonInterpreter() {
        return pythonInterpreter;
    }

    /**
     * @return TODO
     */
    public ScriptConsole getScriptConsole() {
        return scriptConsole;
    }

    /**
     * @return TODO
     */
    public WorldObject getSelectedObj() {
        // check to make sure the object is still in the world before claiming it's still connected
        if (selectedObj!=null) {
            if (selectedObj.isDestroyed()) {
                selectedObj=null;
            }
        }
        return selectedObj;
    }

    /**
     * @return TODO
     */
    public ConfigurationPane getConfigPane() {
        return configPane;
    }

    /**
     * @return TODO
     */
    public AuxillarySplitPane getScriptConsolePane() {
        return this.scriptConsolePane;
    }

    @Override
    public void initFileMenu(MenuBuilder fileMenu) {

        fileMenu.addAction(new CreateModelAction("New Network", this, new CNetwork()));

        fileMenu.addAction(new OpenNeoFileAction(this),
                KeyEvent.VK_O,
                KeyStroke.getKeyStroke(KeyEvent.VK_O, MENU_SHORTCUT_KEY_MASK));

        fileMenu.addAction(new SaveNetworkAction("Save Network"),
                KeyEvent.VK_S,
                KeyStroke.getKeyStroke(KeyEvent.VK_S, MENU_SHORTCUT_KEY_MASK));
        fileMenu.addAction(new RunNetworkAction("Run Network"),
                KeyEvent.VK_R,
                KeyStroke.getKeyStroke(KeyEvent.VK_R, MENU_SHORTCUT_KEY_MASK));
    }

    @Override
    public void initViewMenu(JMenuBar menuBar) {

        MenuBuilder viewMenu = new MenuBuilder("View");
        viewMenu.getJMenu().setMnemonic(KeyEvent.VK_V);
        menuBar.add(viewMenu.getJMenu());

        viewMenu.addAction(new OpenScriptEditor("Open Script Editor"),
                KeyEvent.VK_E,
                KeyStroke.getKeyStroke(KeyEvent.VK_E, MENU_SHORTCUT_KEY_MASK));

        int count = 1;
        for (AuxillarySplitPane splitPane : splitPanes) {
            byte shortCutChar = splitPane.getAuxTitle().getBytes()[0];

            viewMenu.addAction(new ToggleScriptPane("Toggle " + splitPane.getAuxTitle(), splitPane),
                    shortCutChar,
                    KeyStroke.getKeyStroke(0x30 + count++, MENU_SHORTCUT_KEY_MASK));

        }
    }

    public Point2D localToView(Point2D localPoint) {
        return getNengoWorld().localToView(localPoint);
    }

    /**
     * @param node TODO
     * @return TODO
     */
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

    /**
     * @param visible TODO
     */
    public void setDataViewerPaneVisible(boolean visible) {
        if (dataViewerPane.isAuxVisible() != visible) {
            (new ToggleScriptPane(null, dataViewerPane)).doAction();
        }
    }

    /**
     * @param isVisible TODO
     */
    public void setDataViewerVisible(boolean isVisible) {
        dataViewerPane.setAuxVisible(isVisible);
    }

    /**
     * TODO
     * 
     * @author TODO
     */
    public class ToggleScriptPane extends StandardAction {

        private static final long serialVersionUID = 1L;
        private AuxillarySplitPane splitPane;

        /**
         * @param description TODO
         * @param spliPane TODO
         */
        public ToggleScriptPane(String description, AuxillarySplitPane spliPane) {
            super(description);
            this.splitPane = spliPane;
        }

        @Override
        protected void action() throws ActionException {
            splitPane.setAuxVisible(!splitPane.isAuxVisible());
        }

    }

    class ConfigurationPane {
        AuxillarySplitPane auxSplitPane;
        Object currentObj;

        public ConfigurationPane(Container mainPanel) {
            super();
            auxSplitPane = new AuxillarySplitPane(mainPanel, null, "Configuration",
                    AuxillarySplitPane.Orientation.Right);
            auxSplitPane.getAuxPaneWrapper().setBackground(NengoStyle.COLOR_CONFIGURE_BACKGROUND);
            currentObj=null;
        }

        public Object getCurrentObj() {
            return currentObj;
        }

        public void configureObj(Object obj) {
            if (obj==currentObj) {
                return;
            }
            currentObj=obj;

            int location=auxSplitPane.getDividerLocation();

            if (obj==null) {
                auxSplitPane.setAuxPane(null,"Configuration");
            } else {


                ConfigUtil.ConfigurationPane configurationPane = ConfigUtil.createConfigurationPane(obj);
                configurationPane.getTree().setBackground(NengoStyle.COLOR_CONFIGURE_BACKGROUND);


                // Style.applyStyle(configurationPane.getTree());
                // Style.applyStyle(configurationPane.getCellRenderer());

                String name;
                if (obj instanceof Node) {
                    name = ((Node) obj).getName();
                } else if (obj instanceof Termination) {
                    name = ((Termination) obj).getName();
                } else if (obj instanceof Origin) {
                    name = ((Origin) obj).getName();
                } else {
                    name = "Configuration";
                }
                auxSplitPane.setAuxPane(configurationPane, name + " (" + obj.getClass().getSimpleName()
                        + ")");
            }
            auxSplitPane.setDividerLocation(location);

        }

        public AuxillarySplitPane toJComponent() {
            return auxSplitPane;
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

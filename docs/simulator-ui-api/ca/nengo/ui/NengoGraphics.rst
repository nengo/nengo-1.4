.. java:import:: java.awt BorderLayout

.. java:import:: java.awt Container

.. java:import:: java.awt Dimension

.. java:import:: java.awt Image

.. java:import:: java.awt.event KeyEvent

.. java:import:: java.awt.geom Point2D

.. java:import:: java.awt.image BufferedImage

.. java:import:: java.io File

.. java:import:: java.io IOException

.. java:import:: java.util ArrayList

.. java:import:: java.util Collection

.. java:import:: java.util LinkedList

.. java:import:: javax.imageio ImageIO

.. java:import:: javax.swing JFrame

.. java:import:: javax.swing JMenuBar

.. java:import:: javax.swing JOptionPane

.. java:import:: javax.swing JPanel

.. java:import:: javax.swing JScrollPane

.. java:import:: javax.swing JToolBar

.. java:import:: javax.swing KeyStroke

.. java:import:: javax.swing ScrollPaneConstants

.. java:import:: javax.swing UIManager

.. java:import:: javax.swing UnsupportedLookAndFeelException

.. java:import:: org.python.util PythonInterpreter

.. java:import:: org.simplericity.macify.eawt Application

.. java:import:: ca.nengo.config ConfigUtil

.. java:import:: ca.nengo.config JavaSourceParser

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.ui.actions ClearAllAction

.. java:import:: ca.nengo.ui.actions CopyAction

.. java:import:: ca.nengo.ui.actions CreateModelAction

.. java:import:: ca.nengo.ui.actions CutAction

.. java:import:: ca.nengo.ui.actions GeneratePDFAction

.. java:import:: ca.nengo.ui.actions GeneratePythonScriptAction

.. java:import:: ca.nengo.ui.actions OpenNeoFileAction

.. java:import:: ca.nengo.ui.actions PasteAction

.. java:import:: ca.nengo.ui.actions RemoveModelAction

.. java:import:: ca.nengo.ui.actions RunInteractivePlotsAction

.. java:import:: ca.nengo.ui.actions RunSimulatorAction

.. java:import:: ca.nengo.ui.actions SaveNodeAction

.. java:import:: ca.nengo.ui.dataList DataListView

.. java:import:: ca.nengo.ui.dataList SimulatorDataModel

.. java:import:: ca.nengo.ui.lib AppFrame

.. java:import:: ca.nengo.ui.lib AuxillarySplitPane

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions DisabledAction

.. java:import:: ca.nengo.ui.lib.actions DragAction

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.actions UserCancelledException

.. java:import:: ca.nengo.ui.lib.actions ZoomToFitAction

.. java:import:: ca.nengo.ui.lib.misc ShortcutKey

.. java:import:: ca.nengo.ui.lib.objects.models ModelObject

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.util.menus MenuBuilder

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world WorldObject.Property

.. java:import:: ca.nengo.ui.lib.world.elastic ElasticWorld

.. java:import:: ca.nengo.ui.lib.world.handlers MouseHandler

.. java:import:: ca.nengo.ui.lib.world.handlers SelectionHandler

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects Window

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives Universe

.. java:import:: ca.nengo.ui.models NodeContainer

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.constructors CNetwork

.. java:import:: ca.nengo.ui.models.nodes UINetwork

.. java:import:: ca.nengo.ui.models.nodes.widgets UIProbe

.. java:import:: ca.nengo.ui.models.nodes.widgets UIProjection

.. java:import:: ca.nengo.ui.models.nodes.widgets Widget

.. java:import:: ca.nengo.ui.script ScriptConsole

.. java:import:: ca.nengo.ui.util NengoClipboard

.. java:import:: ca.nengo.ui.util NengoConfigManager

.. java:import:: ca.nengo.ui.util NengoConfigManager.UserProperties

.. java:import:: ca.nengo.ui.util NeoFileChooser

.. java:import:: ca.nengo.ui.util ProgressIndicator

.. java:import:: ca.nengo.ui.util ScriptWorldWrapper

.. java:import:: ca.nengo.ui.world NengoWorld

.. java:import:: ca.nengo.util Environment

NengoGraphics
=============

.. java:package:: ca.nengo.ui
   :noindex:

.. java:type:: public class NengoGraphics extends AppFrame implements NodeContainer

   :author: User

Fields
------
ABOUT
^^^^^

.. java:field:: public static final String ABOUT
   :outertype: NengoGraphics

   Description of Nengo to be shown in the "About" Dialog box

APP_NAME
^^^^^^^^

.. java:field:: public static final String APP_NAME
   :outertype: NengoGraphics

   String used in the UI to identify Nengo

CONFIGURE_PLANE_ENABLED
^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final boolean CONFIGURE_PLANE_ENABLED
   :outertype: NengoGraphics

   Use the configure panel in the right side? Otherwise it's a pop-up.

FileChooser
^^^^^^^^^^^

.. java:field:: public static NeoFileChooser FileChooser
   :outertype: NengoGraphics

   UI delegate object used to show the FileChooser

NEONODE_FILE_EXTENSION
^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final String NEONODE_FILE_EXTENSION
   :outertype: NengoGraphics

   File extension for Nengo Nodes

VERSION
^^^^^^^

.. java:field:: public static final String VERSION
   :outertype: NengoGraphics

   Nengo version number, no real rhyme or reason to it

Constructors
------------
NengoGraphics
^^^^^^^^^^^^^

.. java:constructor:: public NengoGraphics()
   :outertype: NengoGraphics

   Constructor; displays a splash screen

Methods
-------
addNodeModel
^^^^^^^^^^^^

.. java:method:: public UINeoNode addNodeModel(Node node) throws ContainerException
   :outertype: NengoGraphics

   **See also:** :java:ref:`ca.nengo.ui.models.NodeContainer.addNodeModel(Node)`

addNodeModel
^^^^^^^^^^^^

.. java:method:: public UINeoNode addNodeModel(Node node, Double posX, Double posY) throws ContainerException
   :outertype: NengoGraphics

captureInDataViewer
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void captureInDataViewer(Network network)
   :outertype: NengoGraphics

   :param network: TODO

configureObject
^^^^^^^^^^^^^^^

.. java:method:: public void configureObject(Object obj)
   :outertype: NengoGraphics

   :param obj: Object to configure

constructShortcutKeys
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void constructShortcutKeys(LinkedList<ShortcutKey> shortcuts)
   :outertype: NengoGraphics

createWorld
^^^^^^^^^^^

.. java:method:: @Override protected ElasticWorld createWorld()
   :outertype: NengoGraphics

exitAppFrame
^^^^^^^^^^^^

.. java:method:: @Override public void exitAppFrame()
   :outertype: NengoGraphics

getAboutString
^^^^^^^^^^^^^^

.. java:method:: @Override public String getAboutString()
   :outertype: NengoGraphics

getAppName
^^^^^^^^^^

.. java:method:: @Override public String getAppName()
   :outertype: NengoGraphics

getAppWindowTitle
^^^^^^^^^^^^^^^^^

.. java:method:: public String getAppWindowTitle()
   :outertype: NengoGraphics

getClipboard
^^^^^^^^^^^^

.. java:method:: public NengoClipboard getClipboard()
   :outertype: NengoGraphics

   :return: TODO

getConfigPane
^^^^^^^^^^^^^

.. java:method:: public ConfigurationPane getConfigPane()
   :outertype: NengoGraphics

   :return: the configuration (inspector) pane

getInstance
^^^^^^^^^^^

.. java:method:: public static NengoGraphics getInstance()
   :outertype: NengoGraphics

   :return: The singleton instance of the NengoGraphics object

getNengoWorld
^^^^^^^^^^^^^

.. java:method:: protected NengoWorld getNengoWorld()
   :outertype: NengoGraphics

getNodeModel
^^^^^^^^^^^^

.. java:method:: public Node getNodeModel(String name)
   :outertype: NengoGraphics

getProgressIndicator
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public ProgressIndicator getProgressIndicator()
   :outertype: NengoGraphics

getPythonInterpreter
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public PythonInterpreter getPythonInterpreter()
   :outertype: NengoGraphics

   :return: TODO

getScriptConsole
^^^^^^^^^^^^^^^^

.. java:method:: public ScriptConsole getScriptConsole()
   :outertype: NengoGraphics

   :return: the script console

initFileMenu
^^^^^^^^^^^^

.. java:method:: @Override public void initFileMenu(MenuBuilder fileMenu)
   :outertype: NengoGraphics

initLayout
^^^^^^^^^^

.. java:method:: @Override protected void initLayout(Universe canvas)
   :outertype: NengoGraphics

initViewMenu
^^^^^^^^^^^^

.. java:method:: @Override public void initViewMenu(JMenuBar menuBar)
   :outertype: NengoGraphics

initialize
^^^^^^^^^^

.. java:method:: @Override protected void initialize()
   :outertype: NengoGraphics

isScriptConsoleVisible
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isScriptConsoleVisible()
   :outertype: NengoGraphics

   :return: is the script console pane visible

localToView
^^^^^^^^^^^

.. java:method:: public Point2D localToView(Point2D localPoint)
   :outertype: NengoGraphics

promptToSaveModels
^^^^^^^^^^^^^^^^^^

.. java:method:: protected boolean promptToSaveModels()
   :outertype: NengoGraphics

   Prompt user to save models in NengoGraphics. This is most likely called right before the application is exiting.

removeNodeModel
^^^^^^^^^^^^^^^

.. java:method:: public boolean removeNodeModel(Node node)
   :outertype: NengoGraphics

   :param node: TODO
   :return: TODO

setApplication
^^^^^^^^^^^^^^

.. java:method:: public void setApplication(Application application)
   :outertype: NengoGraphics

setDataViewerPaneVisible
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setDataViewerPaneVisible(boolean visible)
   :outertype: NengoGraphics

   :param visible: TODO

setDataViewerVisible
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setDataViewerVisible(boolean isVisible)
   :outertype: NengoGraphics

   :param isVisible: TODO

setTemplatePanel
^^^^^^^^^^^^^^^^

.. java:method:: public void setTemplatePanel(JPanel panel)
   :outertype: NengoGraphics

   template.py calls this function to provide a template bar

setToolbar
^^^^^^^^^^

.. java:method:: public void setToolbar(JToolBar bar)
   :outertype: NengoGraphics

   toolbar.py calls this function to provide a toolbar

toggleConfigPane
^^^^^^^^^^^^^^^^

.. java:method:: public void toggleConfigPane()
   :outertype: NengoGraphics

updateConfigurationPane
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void updateConfigurationPane()
   :outertype: NengoGraphics

updateEditMenu
^^^^^^^^^^^^^^

.. java:method:: @Override protected void updateEditMenu()
   :outertype: NengoGraphics

updateRunMenu
^^^^^^^^^^^^^

.. java:method:: @Override protected void updateRunMenu()
   :outertype: NengoGraphics

updateScriptConsole
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void updateScriptConsole()
   :outertype: NengoGraphics


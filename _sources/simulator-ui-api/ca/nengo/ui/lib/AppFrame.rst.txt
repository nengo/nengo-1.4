.. java:import:: java.awt BorderLayout

.. java:import:: java.awt Container

.. java:import:: java.awt Dimension

.. java:import:: java.awt DisplayMode

.. java:import:: java.awt GraphicsDevice

.. java:import:: java.awt GraphicsEnvironment

.. java:import:: java.awt KeyEventDispatcher

.. java:import:: java.awt Rectangle

.. java:import:: java.awt Toolkit

.. java:import:: java.awt.event KeyAdapter

.. java:import:: java.awt.event KeyEvent

.. java:import:: java.awt.event KeyListener

.. java:import:: java.awt.event WindowEvent

.. java:import:: java.awt.event WindowListener

.. java:import:: java.io ByteArrayOutputStream

.. java:import:: java.io File

.. java:import:: java.io FileInputStream

.. java:import:: java.io FileOutputStream

.. java:import:: java.io IOException

.. java:import:: java.io ObjectInputStream

.. java:import:: java.io ObjectOutputStream

.. java:import:: java.io Serializable

.. java:import:: java.lang.reflect InvocationTargetException

.. java:import:: java.util ArrayList

.. java:import:: java.util Collection

.. java:import:: java.util EventListener

.. java:import:: java.util Iterator

.. java:import:: java.util LinkedList

.. java:import:: javax.swing FocusManager

.. java:import:: javax.swing JEditorPane

.. java:import:: javax.swing JFrame

.. java:import:: javax.swing JLabel

.. java:import:: javax.swing JMenuBar

.. java:import:: javax.swing JOptionPane

.. java:import:: javax.swing KeyStroke

.. java:import:: javax.swing SwingUtilities

.. java:import:: javax.swing.event HyperlinkEvent

.. java:import:: javax.swing.event HyperlinkListener

.. java:import:: org.simplericity.macify.eawt ApplicationEvent

.. java:import:: org.simplericity.macify.eawt ApplicationListener

.. java:import:: ca.nengo.plot Plotter

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions ExitAction

.. java:import:: ca.nengo.ui.lib.actions OpenURLAction

.. java:import:: ca.nengo.ui.lib.actions ReversableActionManager

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.actions ZoomToFitAction

.. java:import:: ca.nengo.ui.lib.misc ShortcutKey

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util.menus MenuBuilder

.. java:import:: ca.nengo.ui.lib.world World

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.elastic ElasticWorld

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects Window

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PXGrid

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives Universe

.. java:import:: edu.umd.cs.piccolo PCamera

.. java:import:: edu.umd.cs.piccolo.activities PActivity

.. java:import:: edu.umd.cs.piccolo.util PDebug

.. java:import:: edu.umd.cs.piccolo.util PPaintContext

.. java:import:: edu.umd.cs.piccolo.util PUtil

AppFrame
========

.. java:package:: ca.nengo.ui.lib
   :noindex:

.. java:type:: public abstract class AppFrame extends JFrame implements ApplicationListener

   This class is based on PFrame by Jesse Grosjean

   :author: Shu Wu

Fields
------
MENU_SHORTCUT_KEY_MASK
^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static int MENU_SHORTCUT_KEY_MASK
   :outertype: AppFrame

   TODO

USER_FILE_DIR
^^^^^^^^^^^^^

.. java:field:: public static final String USER_FILE_DIR
   :outertype: AppFrame

   Name of the directory where UI Files are stored

WORLD_TIPS
^^^^^^^^^^

.. java:field:: public static final String WORLD_TIPS
   :outertype: AppFrame

   A String which briefly describes some commands used in this application

editMenu
^^^^^^^^

.. java:field:: protected MenuBuilder editMenu
   :outertype: AppFrame

runMenu
^^^^^^^

.. java:field:: protected MenuBuilder runMenu
   :outertype: AppFrame

Constructors
------------
AppFrame
^^^^^^^^

.. java:constructor:: public AppFrame()
   :outertype: AppFrame

   TODO

Methods
-------
addActivity
^^^^^^^^^^^

.. java:method:: public boolean addActivity(PActivity activity)
   :outertype: AppFrame

   :param activity: TODO
   :return: TODO

addEscapeFullScreenModeListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void addEscapeFullScreenModeListener()
   :outertype: AppFrame

   This method adds a key listener that will take this PFrame out of full screen mode when the escape key is pressed. This is called for you automatically when the frame enters full screen mode.

addWorldWindow
^^^^^^^^^^^^^^

.. java:method:: public void addWorldWindow(Window window)
   :outertype: AppFrame

   :param window: TODO

chooseBestDisplayMode
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void chooseBestDisplayMode(GraphicsDevice device)
   :outertype: AppFrame

constructShortcutKeys
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void constructShortcutKeys(LinkedList<ShortcutKey> shortcuts)
   :outertype: AppFrame

createDefaultCamera
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected PCamera createDefaultCamera()
   :outertype: AppFrame

createWorld
^^^^^^^^^^^

.. java:method:: protected abstract ElasticWorld createWorld()
   :outertype: AppFrame

exitAppFrame
^^^^^^^^^^^^

.. java:method:: public void exitAppFrame()
   :outertype: AppFrame

   Called when the user closes the Application window

getAboutString
^^^^^^^^^^^^^^

.. java:method:: public abstract String getAboutString()
   :outertype: AppFrame

   :return: String which describes what the application is about

getActionManager
^^^^^^^^^^^^^^^^

.. java:method:: public ReversableActionManager getActionManager()
   :outertype: AppFrame

   :return: Action manager responsible for managing actions. Enables undo, redo functionality.

getAppName
^^^^^^^^^^

.. java:method:: public abstract String getAppName()
   :outertype: AppFrame

   :return: Name of the application

getAppWindowTitle
^^^^^^^^^^^^^^^^^

.. java:method:: public abstract String getAppWindowTitle()
   :outertype: AppFrame

   :return: TODO

getBestDisplayMode
^^^^^^^^^^^^^^^^^^

.. java:method:: protected DisplayMode getBestDisplayMode(GraphicsDevice device)
   :outertype: AppFrame

getHelp
^^^^^^^

.. java:method:: protected String getHelp()
   :outertype: AppFrame

getPreferredDisplayModes
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected Collection<DisplayMode> getPreferredDisplayModes(GraphicsDevice device)
   :outertype: AppFrame

   By default return the current display mode. Subclasses may override this method to return other modes in the collection.

getShortcutKeys
^^^^^^^^^^^^^^^

.. java:method:: protected ShortcutKey[] getShortcutKeys()
   :outertype: AppFrame

getTopWindow
^^^^^^^^^^^^

.. java:method:: public Window getTopWindow()
   :outertype: AppFrame

   :return: TODO

getUniverse
^^^^^^^^^^^

.. java:method:: public Universe getUniverse()
   :outertype: AppFrame

   :return: Canvas which hold the zoomable UI

getWorld
^^^^^^^^

.. java:method:: public ElasticWorld getWorld()
   :outertype: AppFrame

   :return: the top-most World associated with this frame

handleAbout
^^^^^^^^^^^

.. java:method:: public void handleAbout(ApplicationEvent event)
   :outertype: AppFrame

handleOpenApplication
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void handleOpenApplication(ApplicationEvent event)
   :outertype: AppFrame

handleOpenFile
^^^^^^^^^^^^^^

.. java:method:: public void handleOpenFile(ApplicationEvent event)
   :outertype: AppFrame

handlePreferences
^^^^^^^^^^^^^^^^^

.. java:method:: public void handlePreferences(ApplicationEvent event)
   :outertype: AppFrame

handlePrintFile
^^^^^^^^^^^^^^^

.. java:method:: public void handlePrintFile(ApplicationEvent event)
   :outertype: AppFrame

handleQuit
^^^^^^^^^^

.. java:method:: public void handleQuit(ApplicationEvent event)
   :outertype: AppFrame

handleReOpenApplication
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void handleReOpenApplication(ApplicationEvent event)
   :outertype: AppFrame

initFileMenu
^^^^^^^^^^^^

.. java:method:: protected void initFileMenu(MenuBuilder menu)
   :outertype: AppFrame

   Use this function to add menu items to the frame menu bar

   :param menuBar: is attached to the frame

initLayout
^^^^^^^^^^

.. java:method:: protected void initLayout(Universe canvas)
   :outertype: AppFrame

initViewMenu
^^^^^^^^^^^^

.. java:method:: protected void initViewMenu(JMenuBar menuBar)
   :outertype: AppFrame

   Use this function to add menu items to the frame menu bar

   :param menuBar: is attached to the frame

initialize
^^^^^^^^^^

.. java:method:: protected void initialize()
   :outertype: AppFrame

loadPreferences
^^^^^^^^^^^^^^^

.. java:method:: protected void loadPreferences()
   :outertype: AppFrame

   Loads saved preferences related to the application

removeEscapeFullScreenModeListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeEscapeFullScreenModeListener()
   :outertype: AppFrame

   This method removes the escape full screen mode key listener. It will be called for you automatically when full screen mode exits, but the method has been made public for applications that wish to use other methods for exiting full screen mode.

restoreDefaultTitle
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void restoreDefaultTitle()
   :outertype: AppFrame

   TODO

reversableActionsUpdated
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void reversableActionsUpdated()
   :outertype: AppFrame

   Called when reversable actions have changed. Updates the edit menu.

savePreferences
^^^^^^^^^^^^^^^

.. java:method:: protected void savePreferences()
   :outertype: AppFrame

   Save preferences to file

setFullScreenMode
^^^^^^^^^^^^^^^^^

.. java:method:: public void setFullScreenMode(boolean fullScreenMode)
   :outertype: AppFrame

   :param fullScreenMode: sets the screen to fullscreen

setTopWindow
^^^^^^^^^^^^

.. java:method:: public void setTopWindow(Window window)
   :outertype: AppFrame

   :param window: TODO

updateEditMenu
^^^^^^^^^^^^^^

.. java:method:: protected void updateEditMenu()
   :outertype: AppFrame

   Updates the menu 'edit'

updateRunMenu
^^^^^^^^^^^^^

.. java:method:: protected void updateRunMenu()
   :outertype: AppFrame

   Updates the menu 'run'

updateWorldMenu
^^^^^^^^^^^^^^^

.. java:method:: protected void updateWorldMenu()
   :outertype: AppFrame

   Updates the menu 'world'


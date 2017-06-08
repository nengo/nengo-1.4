.. java:import:: java.awt.event MouseEvent

.. java:import:: java.awt.geom Point2D

.. java:import:: java.awt.geom Rectangle2D

.. java:import:: java.lang.ref WeakReference

.. java:import:: javax.swing JPopupMenu

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.world Interactable

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.handlers EventConsumer

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects.icons CloseIcon

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects.icons MaximizeIcon

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects.icons MinimizeIcon

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects.icons RestoreIcon

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives Path

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives Text

.. java:import:: edu.umd.cs.piccolo.event PBasicInputEventHandler

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

.. java:import:: edu.umd.cs.piccolo.event PInputEventListener

.. java:import:: edu.umd.cs.piccolox.nodes PClip

Window
======

.. java:package:: ca.nengo.ui.lib.world.piccolo.objects
   :noindex:

.. java:type:: public class Window extends WorldObjectImpl implements Interactable

   :author: User

Fields
------
WINDOW_STATE_DEFAULT
^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final WindowState WINDOW_STATE_DEFAULT
   :outertype: Window

Constructors
------------
Window
^^^^^^

.. java:constructor:: public Window(WorldObjectImpl source, WorldObjectImpl content)
   :outertype: Window

   :param source: parent Node to attach this Window to
   :param content: Node containing the contents of this Window

Methods
-------
close
^^^^^

.. java:method:: public void close()
   :outertype: Window

   Closes the window

cycleVisibleWindowState
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void cycleVisibleWindowState()
   :outertype: Window

   Increases the size of the window through state transitions

getContents
^^^^^^^^^^^

.. java:method:: public WorldObject getContents()
   :outertype: Window

   :return: Node representing the contents of the Window

getContextMenu
^^^^^^^^^^^^^^

.. java:method:: public JPopupMenu getContextMenu()
   :outertype: Window

getName
^^^^^^^

.. java:method:: @Override public String getName()
   :outertype: Window

getWindowState
^^^^^^^^^^^^^^

.. java:method:: public WindowState getWindowState()
   :outertype: Window

isMaximized
^^^^^^^^^^^

.. java:method:: public boolean isMaximized()
   :outertype: Window

isMinimized
^^^^^^^^^^^

.. java:method:: public boolean isMinimized()
   :outertype: Window

layoutChildren
^^^^^^^^^^^^^^

.. java:method:: @Override public void layoutChildren()
   :outertype: Window

maximizeBounds
^^^^^^^^^^^^^^

.. java:method:: protected void maximizeBounds()
   :outertype: Window

moveToFront
^^^^^^^^^^^

.. java:method:: @Override public void moveToFront()
   :outertype: Window

prepareForDestroy
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void prepareForDestroy()
   :outertype: Window

restoreSavedWindow
^^^^^^^^^^^^^^^^^^

.. java:method:: public void restoreSavedWindow()
   :outertype: Window

setWindowState
^^^^^^^^^^^^^^

.. java:method:: public void setWindowState(WindowState state)
   :outertype: Window

windowStateChanged
^^^^^^^^^^^^^^^^^^

.. java:method:: protected void windowStateChanged()
   :outertype: Window


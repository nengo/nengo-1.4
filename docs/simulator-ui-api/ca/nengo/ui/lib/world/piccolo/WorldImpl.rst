.. java:import:: java.awt.geom Point2D

.. java:import:: java.awt.geom Rectangle2D

.. java:import:: java.security InvalidParameterException

.. java:import:: java.util ArrayList

.. java:import:: java.util Collection

.. java:import:: javax.swing JPopupMenu

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.actions PasteAction

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions RemoveObjectsAction

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.actions ZoomToFitAction

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.ui.lib.world Interactable

.. java:import:: ca.nengo.ui.lib.world World

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.handlers AbstractStatusHandler

.. java:import:: ca.nengo.ui.lib.world.handlers EventConsumer

.. java:import:: ca.nengo.ui.lib.world.handlers KeyboardHandler

.. java:import:: ca.nengo.ui.lib.world.handlers MouseHandler

.. java:import:: ca.nengo.ui.lib.world.handlers PanEventHandler

.. java:import:: ca.nengo.ui.lib.world.handlers SelectionHandler

.. java:import:: ca.nengo.ui.lib.world.handlers TooltipPickHandler

.. java:import:: ca.nengo.ui.lib.world.handlers TopWorldStatusHandler

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects TooltipWrapper

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects Window

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PXGrid

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PXLayer

.. java:import:: ca.nengo.ui.models NodeContainer

.. java:import:: ca.nengo.ui.util NengoClipboard

.. java:import:: edu.umd.cs.piccolo PRoot

.. java:import:: edu.umd.cs.piccolo.event PBasicInputEventHandler

.. java:import:: edu.umd.cs.piccolo.util PBounds

WorldImpl
=========

.. java:package:: ca.nengo.ui.lib.world.piccolo
   :noindex:

.. java:type:: public class WorldImpl extends WorldObjectImpl implements World, Interactable

   Implementation of World. World holds World Objects and has navigation and interaction handlers.

   :author: Shu Wu

Constructors
------------
WorldImpl
^^^^^^^^^

.. java:constructor:: public WorldImpl(String name, WorldGroundImpl ground)
   :outertype: WorldImpl

   :param name:
   :param ground:

WorldImpl
^^^^^^^^^

.. java:constructor:: public WorldImpl(String name, WorldSkyImpl sky, WorldGroundImpl ground)
   :outertype: WorldImpl

   Default constructor

   :param name: Name of this world

Methods
-------
animateToSkyPosition
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void animateToSkyPosition(double x, double y)
   :outertype: WorldImpl

   Sets the view position of the sky, and animates to it.

   :param x: X Position relative to ground
   :param y: Y Position relative to ground

closeAllWindows
^^^^^^^^^^^^^^^

.. java:method:: public void closeAllWindows()
   :outertype: WorldImpl

   Closes all windows which exist in this world

constructMenu
^^^^^^^^^^^^^

.. java:method:: protected void constructMenu(PopupMenuBuilder menu, Double posX, Double posY)
   :outertype: WorldImpl

   Create context menu

   :return: Menu builder

constructSelectionMenu
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void constructSelectionMenu(Collection<WorldObject> selection, PopupMenuBuilder menu)
   :outertype: WorldImpl

getContextMenu
^^^^^^^^^^^^^^

.. java:method:: public JPopupMenu getContextMenu()
   :outertype: WorldImpl

getContextMenu
^^^^^^^^^^^^^^

.. java:method:: public JPopupMenu getContextMenu(double posX, double posY)
   :outertype: WorldImpl

getGround
^^^^^^^^^

.. java:method:: public WorldGroundImpl getGround()
   :outertype: WorldImpl

   :return: ground

getObjectBounds
^^^^^^^^^^^^^^^

.. java:method:: public static Rectangle2D getObjectBounds(Collection<WorldObject> objects)
   :outertype: WorldImpl

getSelection
^^^^^^^^^^^^

.. java:method:: public Collection<WorldObject> getSelection()
   :outertype: WorldImpl

   :return: Selection Currently Selected nodes

getSelectionHandler
^^^^^^^^^^^^^^^^^^^

.. java:method:: public SelectionHandler getSelectionHandler()
   :outertype: WorldImpl

getSelectionMenu
^^^^^^^^^^^^^^^^

.. java:method:: public final JPopupMenu getSelectionMenu(Collection<WorldObject> selection)
   :outertype: WorldImpl

   :return: Context menu for currently selected items, null is none is to be shown

getSky
^^^^^^

.. java:method:: public WorldSkyImpl getSky()
   :outertype: WorldImpl

   :return: sky

getWindows
^^^^^^^^^^

.. java:method:: public Collection<Window> getWindows()
   :outertype: WorldImpl

   :return: A collection of all the windows in this world

isAncestorOf
^^^^^^^^^^^^

.. java:method:: public boolean isAncestorOf(WorldObject wo)
   :outertype: WorldImpl

isSelectionMode
^^^^^^^^^^^^^^^

.. java:method:: public boolean isSelectionMode()
   :outertype: WorldImpl

   :return: if true, selection mode is enabled. if false, navigation mode is enabled instead.

isTooltipsVisible
^^^^^^^^^^^^^^^^^

.. java:method:: public static boolean isTooltipsVisible()
   :outertype: WorldImpl

minimizeAllWindows
^^^^^^^^^^^^^^^^^^

.. java:method:: public void minimizeAllWindows()
   :outertype: WorldImpl

   Minimizes all windows that exist in this world

prepareForDestroy
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void prepareForDestroy()
   :outertype: WorldImpl

setBounds
^^^^^^^^^

.. java:method:: public boolean setBounds(double x, double y, double w, double h)
   :outertype: WorldImpl

setSelectionMode
^^^^^^^^^^^^^^^^

.. java:method:: public void setSelectionMode(boolean enabled)
   :outertype: WorldImpl

   :param enabled: True if selection mode is enabled, False if navigation

setStatusBarHandler
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setStatusBarHandler(AbstractStatusHandler statusHandler)
   :outertype: WorldImpl

   Set the status bar handler, there can be only one.

   :param statusHandler: New Status bar handler

setTooltipsVisible
^^^^^^^^^^^^^^^^^^

.. java:method:: public static void setTooltipsVisible(boolean tooltipsVisible)
   :outertype: WorldImpl

showTooltip
^^^^^^^^^^^

.. java:method:: public TooltipWrapper showTooltip(WorldObject objectSelected)
   :outertype: WorldImpl

   :param objectSelected: Object to show the tooltip for
   :return: Tooltip shown

skyToGround
^^^^^^^^^^^

.. java:method:: public Point2D skyToGround(Point2D position)
   :outertype: WorldImpl

   :param position: Position in sky
   :return: Position on ground

zoomToBounds
^^^^^^^^^^^^

.. java:method:: public void zoomToBounds(Rectangle2D bounds)
   :outertype: WorldImpl

zoomToBounds
^^^^^^^^^^^^

.. java:method:: public void zoomToBounds(Rectangle2D bounds, long time)
   :outertype: WorldImpl

   Animate the sky to look at a portion of the ground at bounds

   :param bounds: Bounds to look at
   :return: Reference to the activity which is animating the zoom and positioning

zoomToFit
^^^^^^^^^

.. java:method:: public void zoomToFit()
   :outertype: WorldImpl

zoomToObject
^^^^^^^^^^^^

.. java:method:: public void zoomToObject(WorldObject object)
   :outertype: WorldImpl

   :param object: Object to zoom to
   :return: reference to animation activity


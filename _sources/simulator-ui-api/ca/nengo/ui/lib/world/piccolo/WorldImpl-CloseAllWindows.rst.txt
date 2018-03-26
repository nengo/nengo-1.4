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

WorldImpl.CloseAllWindows
=========================

.. java:package:: ca.nengo.ui.lib.world.piccolo
   :noindex:

.. java:type::  class CloseAllWindows extends StandardAction
   :outertype: WorldImpl

   Action to close all windows

   :author: Shu Wu

Constructors
------------
CloseAllWindows
^^^^^^^^^^^^^^^

.. java:constructor:: public CloseAllWindows(String actionName)
   :outertype: WorldImpl.CloseAllWindows

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: WorldImpl.CloseAllWindows


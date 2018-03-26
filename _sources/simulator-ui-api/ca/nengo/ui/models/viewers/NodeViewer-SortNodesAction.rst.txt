.. java:import:: java.awt.geom Point2D

.. java:import:: java.util ArrayList

.. java:import:: java.util Collection

.. java:import:: java.util Collections

.. java:import:: java.util Comparator

.. java:import:: java.util Hashtable

.. java:import:: java.util List

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.ui.lib.actions LayoutAction

.. java:import:: ca.nengo.ui.lib.objects.activities TrackedStatusMsg

.. java:import:: ca.nengo.ui.lib.objects.models ModelObject

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.ui.lib.world Interactable

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.elastic ElasticWorld

.. java:import:: ca.nengo.ui.lib.world.handlers AbstractStatusHandler

.. java:import:: ca.nengo.ui.models ModelsContextMenu

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.nodes UINodeViewable

.. java:import:: edu.umd.cs.piccolo.activities PActivity

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

.. java:import:: edu.umd.cs.piccolo.util PBounds

NodeViewer.SortNodesAction
==========================

.. java:package:: ca.nengo.ui.models.viewers
   :noindex:

.. java:type::  class SortNodesAction extends LayoutAction
   :outertype: NodeViewer

   Action to apply a sorting layout

   :author: Shu Wu

Fields
------
sortMode
^^^^^^^^

.. java:field::  SortMode sortMode
   :outertype: NodeViewer.SortNodesAction

Constructors
------------
SortNodesAction
^^^^^^^^^^^^^^^

.. java:constructor:: public SortNodesAction(SortMode sortMode)
   :outertype: NodeViewer.SortNodesAction

Methods
-------
applyLayout
^^^^^^^^^^^

.. java:method:: @Override protected void applyLayout()
   :outertype: NodeViewer.SortNodesAction


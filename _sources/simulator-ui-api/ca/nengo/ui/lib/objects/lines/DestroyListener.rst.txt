.. java:import:: java.util Collection

.. java:import:: javax.swing JPopupMenu

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.ui.lib.world DroppableX

.. java:import:: ca.nengo.ui.lib.world Interactable

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world WorldObject.Listener

.. java:import:: ca.nengo.ui.lib.world WorldObject.Property

.. java:import:: ca.nengo.ui.lib.world.elastic ElasticEdge

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldGroundImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PXEdge

.. java:import:: edu.umd.cs.piccolo.util PPaintContext

DestroyListener
===============

.. java:package:: ca.nengo.ui.lib.objects.lines
   :noindex:

.. java:type::  class DestroyListener implements Listener

   Listens for destroy events from the Well and destroys the connector Note: The connector isn't destroyed automatically by the well's destruct function because it is not a Piccolo child of the well.

   :author: Shu Wu

Constructors
------------
DestroyListener
^^^^^^^^^^^^^^^

.. java:constructor:: public DestroyListener(LineConnector parent)
   :outertype: DestroyListener

Methods
-------
propertyChanged
^^^^^^^^^^^^^^^

.. java:method:: public void propertyChanged(Property event)
   :outertype: DestroyListener


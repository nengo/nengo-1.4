.. java:import:: java.awt.geom Point2D

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.elastic ElasticWorld

.. java:import:: ca.nengo.ui.models NodeContainer

.. java:import:: ca.nengo.ui.models UINeoNode

NengoWorld
==========

.. java:package:: ca.nengo.ui.world
   :noindex:

.. java:type:: public class NengoWorld extends ElasticWorld implements NodeContainer

Constructors
------------
NengoWorld
^^^^^^^^^^

.. java:constructor:: public NengoWorld()
   :outertype: NengoWorld

Methods
-------
addNodeModel
^^^^^^^^^^^^

.. java:method:: public UINeoNode addNodeModel(Node node) throws ContainerException
   :outertype: NengoWorld

addNodeModel
^^^^^^^^^^^^

.. java:method:: public UINeoNode addNodeModel(Node node, Double posX, Double posY) throws ContainerException
   :outertype: NengoWorld

getNodeModel
^^^^^^^^^^^^

.. java:method:: public Node getNodeModel(String name)
   :outertype: NengoWorld

localToView
^^^^^^^^^^^

.. java:method:: public Point2D localToView(Point2D localPoint)
   :outertype: NengoWorld


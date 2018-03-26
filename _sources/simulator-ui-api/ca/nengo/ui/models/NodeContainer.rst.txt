.. java:import:: java.awt.geom Point2D

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.ui.lib.exceptions UIException

NodeContainer
=============

.. java:package:: ca.nengo.ui.models
   :noindex:

.. java:type:: public interface NodeContainer

   A Container of PNeoNode

   :author: Shu Wu

Methods
-------
addNodeModel
^^^^^^^^^^^^

.. java:method:: public UINeoNode addNodeModel(Node node) throws ContainerException
   :outertype: NodeContainer

   Adds a child node to the container

   :param node: Node to be added
   :return: UI Node Wrapper

addNodeModel
^^^^^^^^^^^^

.. java:method:: public UINeoNode addNodeModel(Node node, Double posX, Double posY) throws ContainerException
   :outertype: NodeContainer

   :param node: Node to be added
   :param posX: X Position of node
   :param posY: Y Position of node

getNodeModel
^^^^^^^^^^^^

.. java:method:: public Node getNodeModel(String name)
   :outertype: NodeContainer

localToView
^^^^^^^^^^^

.. java:method:: public Point2D localToView(Point2D localPoint)
   :outertype: NodeContainer


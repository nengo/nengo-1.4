.. java:import:: java.awt.geom Point2D

.. java:import:: java.awt.geom Rectangle2D

.. java:import:: java.io Serializable

.. java:import:: java.util Hashtable

.. java:import:: ca.nengo.ui.models UINeoNode

NodeLayout
==========

.. java:package:: ca.nengo.ui.models.viewers
   :noindex:

.. java:type:: public class NodeLayout implements Serializable

   Layout of nodes which is serializable

   :author: Shu Wu

Constructors
------------
NodeLayout
^^^^^^^^^^

.. java:constructor:: public NodeLayout(String layoutName, NodeViewer world, boolean elasticMode)
   :outertype: NodeLayout

   :param layoutName: Name of the layout
   :param world: Viewer containing nodes

Methods
-------
elasticModeEnabled
^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean elasticModeEnabled()
   :outertype: NodeLayout

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: NodeLayout

   :return: Layout name

getPosition
^^^^^^^^^^^

.. java:method:: public Point2D getPosition(UINeoNode node)
   :outertype: NodeLayout

   :param nodeName: Name of node
   :return: Position of node

getSavedViewBounds
^^^^^^^^^^^^^^^^^^

.. java:method:: public Rectangle2D getSavedViewBounds()
   :outertype: NodeLayout

   :return: Saved view bounds


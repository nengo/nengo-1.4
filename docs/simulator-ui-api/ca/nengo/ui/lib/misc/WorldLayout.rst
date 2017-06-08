.. java:import:: java.awt.geom Point2D

.. java:import:: java.awt.geom Rectangle2D

.. java:import:: java.io Serializable

.. java:import:: java.util Hashtable

.. java:import:: ca.nengo.ui.lib.world World

.. java:import:: ca.nengo.ui.lib.world WorldObject

WorldLayout
===========

.. java:package:: ca.nengo.ui.lib.misc
   :noindex:

.. java:type:: public class WorldLayout implements Serializable

   Layout of nodes which is serializable

   :author: Shu Wu

Constructors
------------
WorldLayout
^^^^^^^^^^^

.. java:constructor:: public WorldLayout(String layoutName, World world, boolean elasticMode)
   :outertype: WorldLayout

   :param layoutName: Name of the layout
   :param world: Viewer containing nodes

Methods
-------
elasticModeEnabled
^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean elasticModeEnabled()
   :outertype: WorldLayout

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: WorldLayout

   :return: Layout name

getPosition
^^^^^^^^^^^

.. java:method:: public Point2D getPosition(WorldObject node)
   :outertype: WorldLayout

   :param nodeName: Name of node
   :return: Position of node

getSavedViewBounds
^^^^^^^^^^^^^^^^^^

.. java:method:: public Rectangle2D getSavedViewBounds()
   :outertype: WorldLayout

   :return: Saved view bounds


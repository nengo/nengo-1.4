.. java:import:: java.awt.geom Point2D

.. java:import:: java.util Collection

.. java:import:: ca.nengo.ui.lib.world.handlers SelectionHandler

World
=====

.. java:package:: ca.nengo.ui.lib.world
   :noindex:

.. java:type:: public interface World extends WorldObject

Methods
-------
getGround
^^^^^^^^^

.. java:method:: public WorldLayer getGround()
   :outertype: World

getSelection
^^^^^^^^^^^^

.. java:method:: public Collection<WorldObject> getSelection()
   :outertype: World

getSelectionHandler
^^^^^^^^^^^^^^^^^^^

.. java:method:: public SelectionHandler getSelectionHandler()
   :outertype: World

getSky
^^^^^^

.. java:method:: public WorldSky getSky()
   :outertype: World

skyToGround
^^^^^^^^^^^

.. java:method:: public Point2D skyToGround(Point2D position)
   :outertype: World

zoomToFit
^^^^^^^^^

.. java:method:: public void zoomToFit()
   :outertype: World

   Animate the sky to view all object on the ground

   :return: reference to animation activity

zoomToObject
^^^^^^^^^^^^

.. java:method:: public void zoomToObject(WorldObject object)
   :outertype: World

   :param object: Object to zoom to
   :return: reference to animation activity


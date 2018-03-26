.. java:import:: java.awt Color

.. java:import:: java.awt.geom Rectangle2D

.. java:import:: ca.nengo.ui.lib.world WorldObject.Listener

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives Path

Border
======

.. java:package:: ca.nengo.ui.lib.world.piccolo.objects
   :noindex:

.. java:type:: public class Border extends WorldObjectImpl implements Listener

   Adds a border around an object.

   :author: Shu Wu

Constructors
------------
Border
^^^^^^

.. java:constructor:: public Border(WorldObjectImpl target, Color color)
   :outertype: Border

   Create a new border

   :param target: Object to create a border around
   :param color: Color of border

Methods
-------
prepareForDestroy
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void prepareForDestroy()
   :outertype: Border

propertyChanged
^^^^^^^^^^^^^^^

.. java:method:: public void propertyChanged(Property event)
   :outertype: Border

updateBorder
^^^^^^^^^^^^

.. java:method:: protected void updateBorder()
   :outertype: Border

   Updates the border when the target bounds changes


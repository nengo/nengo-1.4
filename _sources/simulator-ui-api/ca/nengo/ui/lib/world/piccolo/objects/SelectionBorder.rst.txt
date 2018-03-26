.. java:import:: java.awt Color

.. java:import:: java.awt.geom Rectangle2D

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.world World

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world WorldSky

.. java:import:: ca.nengo.ui.lib.world WorldObject.Listener

.. java:import:: ca.nengo.ui.lib.world WorldObject.Property

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives Path

SelectionBorder
===============

.. java:package:: ca.nengo.ui.lib.world.piccolo.objects
   :noindex:

.. java:type:: public class SelectionBorder implements Listener

   A Border instance that can change its object of focus (As long as the object are within the world that the frame is in). Border is attached to the sky layer so there is no attenuation of the edge width when the ground is viewed at a low scale.

   :author: Shu Wu

Constructors
------------
SelectionBorder
^^^^^^^^^^^^^^^

.. java:constructor:: public SelectionBorder(World world)
   :outertype: SelectionBorder

   :param world: World, whose sky, this border shall be added to.

SelectionBorder
^^^^^^^^^^^^^^^

.. java:constructor:: public SelectionBorder(World world, WorldObject objSelected)
   :outertype: SelectionBorder

   :param world: World, whose sky, this border shall be added to.
   :param objSelected: Object to select initially

Methods
-------
destroy
^^^^^^^

.. java:method:: public void destroy()
   :outertype: SelectionBorder

getFrameColor
^^^^^^^^^^^^^

.. java:method:: public Color getFrameColor()
   :outertype: SelectionBorder

propertyChanged
^^^^^^^^^^^^^^^

.. java:method:: public void propertyChanged(Property event)
   :outertype: SelectionBorder

setFrameColor
^^^^^^^^^^^^^

.. java:method:: public void setFrameColor(Color frameColor)
   :outertype: SelectionBorder

setSelected
^^^^^^^^^^^

.. java:method:: public void setSelected(WorldObject newSelected)
   :outertype: SelectionBorder

updateBounds
^^^^^^^^^^^^

.. java:method:: protected void updateBounds()
   :outertype: SelectionBorder

   Updates the bounds of the border to match those of the selected object


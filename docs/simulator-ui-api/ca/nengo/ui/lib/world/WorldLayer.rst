.. java:import:: java.util Collection

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects Window

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PXEdge

WorldLayer
==========

.. java:package:: ca.nengo.ui.lib.world
   :noindex:

.. java:type:: public interface WorldLayer extends WorldObject

   A Layer of the world

   :author: Shu Wu

Methods
-------
addChild
^^^^^^^^

.. java:method:: public void addChild(WorldObject child)
   :outertype: WorldLayer

   :param child: Child node to add

addEdge
^^^^^^^

.. java:method:: public void addEdge(PXEdge edge)
   :outertype: WorldLayer

clearLayer
^^^^^^^^^^

.. java:method:: public void clearLayer()
   :outertype: WorldLayer

   Clears the layer of all children

getWindows
^^^^^^^^^^

.. java:method:: public Collection<Window> getWindows()
   :outertype: WorldLayer

   :return: A Collection of windows

getWorld
^^^^^^^^

.. java:method:: public WorldImpl getWorld()
   :outertype: WorldLayer

   :return: World which this layer belongs to

setWorld
^^^^^^^^

.. java:method:: public void setWorld(WorldImpl world)
   :outertype: WorldLayer

   :param world: The world


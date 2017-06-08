.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: ca.nengo.ui.lib.world WorldLayer

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects Window

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PiccoloNodeInWorld

WorldLayerImpl
==============

.. java:package:: ca.nengo.ui.lib.world.piccolo
   :noindex:

.. java:type:: public abstract class WorldLayerImpl extends WorldObjectImpl implements WorldLayer

Fields
------
world
^^^^^

.. java:field:: protected WorldImpl world
   :outertype: WorldLayerImpl

   World this layer belongs to

Constructors
------------
WorldLayerImpl
^^^^^^^^^^^^^^

.. java:constructor:: public WorldLayerImpl(String name, PiccoloNodeInWorld node)
   :outertype: WorldLayerImpl

   Create a new ground layer

   :param world: World this layer belongs to

Methods
-------
clearLayer
^^^^^^^^^^

.. java:method:: public void clearLayer()
   :outertype: WorldLayerImpl

   Removes and destroys children

getWindows
^^^^^^^^^^

.. java:method:: public List<Window> getWindows()
   :outertype: WorldLayerImpl

getWorld
^^^^^^^^

.. java:method:: @Override public WorldImpl getWorld()
   :outertype: WorldLayerImpl

setWorld
^^^^^^^^

.. java:method:: public void setWorld(WorldImpl world)
   :outertype: WorldLayerImpl


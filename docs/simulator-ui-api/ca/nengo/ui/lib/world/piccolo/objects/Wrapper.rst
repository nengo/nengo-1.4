.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

Wrapper
=======

.. java:package:: ca.nengo.ui.lib.world.piccolo.objects
   :noindex:

.. java:type:: public class Wrapper extends WorldObjectImpl

   A World Object which does nothing but wrap another world object to add a layer of indirection.

   :author: Shu Wu

Constructors
------------
Wrapper
^^^^^^^

.. java:constructor:: public Wrapper(WorldObject obj)
   :outertype: Wrapper

Methods
-------
destroyPackage
^^^^^^^^^^^^^^

.. java:method:: public void destroyPackage()
   :outertype: Wrapper

getPackage
^^^^^^^^^^

.. java:method:: public WorldObject getPackage()
   :outertype: Wrapper

packageChanged
^^^^^^^^^^^^^^

.. java:method:: protected void packageChanged(WorldObject oldPackage)
   :outertype: Wrapper

setPackage
^^^^^^^^^^

.. java:method:: public final void setPackage(WorldObject obj)
   :outertype: Wrapper


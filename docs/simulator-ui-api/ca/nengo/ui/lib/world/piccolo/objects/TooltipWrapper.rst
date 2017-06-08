.. java:import:: java.awt.geom Point2D

.. java:import:: java.awt.geom Rectangle2D

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world WorldSky

.. java:import:: ca.nengo.ui.lib.world WorldObject.Listener

.. java:import:: ca.nengo.ui.lib.world.activities Fader

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: edu.umd.cs.piccolo.activities PActivity

.. java:import:: edu.umd.cs.piccolo.event PBasicInputEventHandler

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

TooltipWrapper
==============

.. java:package:: ca.nengo.ui.lib.world.piccolo.objects
   :noindex:

.. java:type:: public class TooltipWrapper extends WorldObjectImpl implements Listener

Constructors
------------
TooltipWrapper
^^^^^^^^^^^^^^

.. java:constructor:: public TooltipWrapper(WorldSky parent, WorldObject tooltip, WorldObject target)
   :outertype: TooltipWrapper

   :param parent: Parent which will hold this wrapper
   :param tooltip: Tooltip object
   :param target: Target which this tooltip shall be attached to

Methods
-------
fadeAndDestroy
^^^^^^^^^^^^^^

.. java:method:: public void fadeAndDestroy()
   :outertype: TooltipWrapper

   Fades away in an animated sequence, and then destroy itself

fadeIn
^^^^^^

.. java:method:: public void fadeIn()
   :outertype: TooltipWrapper

   Fades in, in an animated sequence

prepareForDestroy
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void prepareForDestroy()
   :outertype: TooltipWrapper

propertyChanged
^^^^^^^^^^^^^^^

.. java:method:: public void propertyChanged(Property event)
   :outertype: TooltipWrapper


.. java:import:: java.awt.geom Point2D

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.world.activities Fader

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives Text

.. java:import:: edu.umd.cs.piccolo.activities PActivity

TransientMessage
================

.. java:package:: ca.nengo.ui.lib.objects.activities
   :noindex:

.. java:type:: public class TransientMessage extends Text

   A message that appears in the World and disappears smoothly after a duration.

   :author: Shu Wu

Fields
------
ANIMATE_MSG_DURATION
^^^^^^^^^^^^^^^^^^^^

.. java:field:: static final int ANIMATE_MSG_DURATION
   :outertype: TransientMessage

Constructors
------------
TransientMessage
^^^^^^^^^^^^^^^^

.. java:constructor:: public TransientMessage(String text)
   :outertype: TransientMessage

Methods
-------
popup
^^^^^

.. java:method:: public void popup(long delayMS)
   :outertype: TransientMessage


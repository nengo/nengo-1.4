.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: edu.umd.cs.piccolo.activities PInterpolatingActivity

Fader
=====

.. java:package:: ca.nengo.ui.lib.world.activities
   :noindex:

.. java:type:: public class Fader extends PInterpolatingActivity

   Activity which gradually changes the transparency of an node

   :author: Shu Wu

Constructors
------------
Fader
^^^^^

.. java:constructor:: public Fader(WorldObject target, long duration, float finalOpacity)
   :outertype: Fader

   :param target: Node target
   :param duration: Duration of the activity
   :param finalOpacity: Transparency target

Methods
-------
activityStarted
^^^^^^^^^^^^^^^

.. java:method:: @Override protected void activityStarted()
   :outertype: Fader

setRelativeTargetValue
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setRelativeTargetValue(float zeroToOne)
   :outertype: Fader


.. java:import:: ca.nengo.math CurveFitter

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math.impl InterpolatedFunction

LinearCurveFitter
=================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class LinearCurveFitter implements CurveFitter

   Interpolates linearly between example points. Outside the range of examples, the last interval is extrapolated.

   Inputs x must be sorted from lowest to highest.

   TODO: sort inputs

   :author: Bryan Tripp

Methods
-------
clone
^^^^^

.. java:method:: @Override public CurveFitter clone() throws CloneNotSupportedException
   :outertype: LinearCurveFitter

fit
^^^

.. java:method:: public Function fit(float[] x, float[] y)
   :outertype: LinearCurveFitter

   Note that inputs x must be sorted from lowest to highest.

   **See also:** :java:ref:`ca.nengo.math.CurveFitter.fit(float[],float[])`


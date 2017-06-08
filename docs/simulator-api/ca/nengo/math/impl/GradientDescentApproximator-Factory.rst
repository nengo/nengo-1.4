.. java:import:: java.io Serializable

.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.math ApproximatorFactory

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math LinearApproximator

.. java:import:: ca.nengo.util MU

GradientDescentApproximator.Factory
===================================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public static class Factory implements ApproximatorFactory
   :outertype: GradientDescentApproximator

   An ApproximatorFactory that produces GradientDescentApproximators.

   :author: Bryan Tripp

Constructors
------------
Factory
^^^^^^^

.. java:constructor:: public Factory(Constraints constraints, boolean ignoreBias)
   :outertype: GradientDescentApproximator.Factory

   :param constraints: As in GradientDescentApproximator constructor
   :param ignoreBias: As in GradientDescentApproximator constructor

Methods
-------
clone
^^^^^

.. java:method:: @Override public ApproximatorFactory clone() throws CloneNotSupportedException
   :outertype: GradientDescentApproximator.Factory

getApproximator
^^^^^^^^^^^^^^^

.. java:method:: public LinearApproximator getApproximator(float[][] evalPoints, float[][] values)
   :outertype: GradientDescentApproximator.Factory

   **See also:** :java:ref:`ca.nengo.math.ApproximatorFactory.getApproximator(float[][],float[][])`


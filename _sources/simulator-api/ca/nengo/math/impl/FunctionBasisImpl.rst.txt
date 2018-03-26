.. java:import:: java.lang.reflect Method

.. java:import:: ca.nengo.config ConfigUtil

.. java:import:: ca.nengo.config Configuration

.. java:import:: ca.nengo.config.impl ConfigurationImpl

.. java:import:: ca.nengo.config.impl ListPropertyImpl

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math FunctionBasis

FunctionBasisImpl
=================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class FunctionBasisImpl extends AbstractFunction implements FunctionBasis

   Default implementation of FunctionBasis.

   :author: Bryan Tripp

Constructors
------------
FunctionBasisImpl
^^^^^^^^^^^^^^^^^

.. java:constructor:: public FunctionBasisImpl(Function[] functions)
   :outertype: FunctionBasisImpl

   :param functions: Ordered list of functions composing this basis (all must have same dimension)

Methods
-------
clone
^^^^^

.. java:method:: @Override public Function clone() throws CloneNotSupportedException
   :outertype: FunctionBasisImpl

getBasisDimension
^^^^^^^^^^^^^^^^^

.. java:method:: public int getBasisDimension()
   :outertype: FunctionBasisImpl

   **See also:** :java:ref:`ca.nengo.math.FunctionBasis.getBasisDimension()`

getCoefficients
^^^^^^^^^^^^^^^

.. java:method:: public float[] getCoefficients()
   :outertype: FunctionBasisImpl

   :return: Coefficients with which basis functions are combined

getConfiguration
^^^^^^^^^^^^^^^^

.. java:method:: public Configuration getConfiguration()
   :outertype: FunctionBasisImpl

   :return: Custom configuration

getFunction
^^^^^^^^^^^

.. java:method:: public Function getFunction(int dimension)
   :outertype: FunctionBasisImpl

   **See also:** :java:ref:`ca.nengo.math.FunctionBasis.getFunction(int)`

map
^^^

.. java:method:: public float map(float[] from)
   :outertype: FunctionBasisImpl

   **See also:** :java:ref:`ca.nengo.math.Function.map(float[])`

setCoefficients
^^^^^^^^^^^^^^^

.. java:method:: public void setCoefficients(float[] coefficients)
   :outertype: FunctionBasisImpl

   **See also:** :java:ref:`ca.nengo.math.FunctionBasis.setCoefficients(float[])`


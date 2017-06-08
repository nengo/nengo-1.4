.. java:import:: ca.nengo.math Function

IdentityFunction
================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class IdentityFunction implements Function

   Identity function on a particular dimension of input, ie f(x) = x_i, where i is a constant.

   :author: Bryan Tripp

Constructors
------------
IdentityFunction
^^^^^^^^^^^^^^^^

.. java:constructor:: public IdentityFunction(int dimension, int i)
   :outertype: IdentityFunction

   :param dimension: Dimension of input vector
   :param i: Index (from 0) of input vector of which this function is an identity

IdentityFunction
^^^^^^^^^^^^^^^^

.. java:constructor:: public IdentityFunction()
   :outertype: IdentityFunction

   Defaults to one dimension.

Methods
-------
clone
^^^^^

.. java:method:: @Override public Function clone() throws CloneNotSupportedException
   :outertype: IdentityFunction

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: IdentityFunction

   **See also:** :java:ref:`ca.nengo.math.Function.getDimension()`

getIdentityDimension
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public int getIdentityDimension()
   :outertype: IdentityFunction

   :return: Index on input vector of which this funciton is an identity

map
^^^

.. java:method:: public float map(float[] from)
   :outertype: IdentityFunction

   **See also:** :java:ref:`ca.nengo.math.Function.map(float[])`

multiMap
^^^^^^^^

.. java:method:: public float[] multiMap(float[][] from)
   :outertype: IdentityFunction

   **See also:** :java:ref:`ca.nengo.math.Function.multiMap(float[][])`

setDimension
^^^^^^^^^^^^

.. java:method:: public void setDimension(int dimension)
   :outertype: IdentityFunction

   :param dimension: New dimension of expected input vectors

setIdentityDimension
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setIdentityDimension(int i)
   :outertype: IdentityFunction

   :param i: Index (from 0) of input vector of which this function is an identity


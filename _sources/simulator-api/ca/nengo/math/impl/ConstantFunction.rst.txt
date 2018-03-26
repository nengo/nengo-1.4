.. java:import:: ca.nengo.math Function

ConstantFunction
================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class ConstantFunction implements Function

   A Function that maps everything to the same value.

   :author: Bryan Tripp

Constructors
------------
ConstantFunction
^^^^^^^^^^^^^^^^

.. java:constructor:: public ConstantFunction(int dimension, float value)
   :outertype: ConstantFunction

   :param dimension: Input dimension of this Function
   :param value: Constant output value of this Function

Methods
-------
clone
^^^^^

.. java:method:: @Override public Function clone() throws CloneNotSupportedException
   :outertype: ConstantFunction

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: ConstantFunction

   **See also:** :java:ref:`ca.nengo.math.Function.getDimension()`

getValue
^^^^^^^^

.. java:method:: public float getValue()
   :outertype: ConstantFunction

   :return: Value of function

map
^^^

.. java:method:: public float map(float[] from)
   :outertype: ConstantFunction

   :return: The constant value given in the constructor

   **See also:** :java:ref:`ca.nengo.math.Function.map(float[])`

multiMap
^^^^^^^^

.. java:method:: public float[] multiMap(float[][] from)
   :outertype: ConstantFunction

   **See also:** :java:ref:`ca.nengo.math.Function.multiMap(float[][])`

setDimension
^^^^^^^^^^^^

.. java:method:: public void setDimension(int dimension)
   :outertype: ConstantFunction

   :param dimension: New dimension

setValue
^^^^^^^^

.. java:method:: public void setValue(float value)
   :outertype: ConstantFunction

   :param value: The new constant result of the function


.. java:import:: java.io Serializable

Function
========

.. java:package:: ca.nengo.math
   :noindex:

.. java:type:: public interface Function extends Serializable, Cloneable

   A mathematical function from an n-D space to a 1-D space. For simplicity we always map to a 1-D space, and model maps to n-D spaces with n Functions.

   Instances of Function are immutable once they are created (ie their parameters do not change over time).

   :author: Bryan Tripp

Methods
-------
clone
^^^^^

.. java:method:: public Function clone() throws CloneNotSupportedException
   :outertype: Function

   :throws CloneNotSupportedException: if clone can't be made
   :return: Valid clone

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: Function

   :return: Dimension of the space that the Function maps from

map
^^^

.. java:method:: public float map(float[] from)
   :outertype: Function

   :param from: Must have same length as getDimension()
   :return: result of function operation on arg

multiMap
^^^^^^^^

.. java:method:: public float[] multiMap(float[][] from)
   :outertype: Function

   :param from: An array of arguments; each element must have length getDimension().
   :return: Array of results of function operation on each arg


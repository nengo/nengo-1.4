.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.util MU

LinearFunction
==============

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class LinearFunction extends AbstractFunction

   A linear map into one dimension. Optionally, the result can be biased and/or rectified.

   :author: Bryan Tripp

Constructors
------------
LinearFunction
^^^^^^^^^^^^^^

.. java:constructor:: public LinearFunction(float[] map, float bias, boolean rectified)
   :outertype: LinearFunction

   :param map: A 1Xn matrix that defines a map from input onto one dimension (i.e. f(x) = m'x, where m is the map)
   :param bias: Bias to add to result
   :param rectified: If true, result is rectified (set to 0 if less than 0)

Methods
-------
clone
^^^^^

.. java:method:: @Override public Function clone() throws CloneNotSupportedException
   :outertype: LinearFunction

getBias
^^^^^^^

.. java:method:: public float getBias()
   :outertype: LinearFunction

   :return: Bias to add to result

getMap
^^^^^^

.. java:method:: public float[] getMap()
   :outertype: LinearFunction

   :return: map A 1Xn matrix that defines a map from input onto one dimension (i.e. f(x) = m'x, where m is the map)

getRectified
^^^^^^^^^^^^

.. java:method:: public boolean getRectified()
   :outertype: LinearFunction

   :return: If true, result is rectified (set to 0 if less than 0)

map
^^^

.. java:method:: @Override public float map(float[] from)
   :outertype: LinearFunction

setBias
^^^^^^^

.. java:method:: public void setBias(float bias)
   :outertype: LinearFunction

   :param bias: Bias to add to result

setMap
^^^^^^

.. java:method:: public void setMap(float[] map)
   :outertype: LinearFunction

   :param map: map A 1Xn matrix that defines a map from input onto one dimension (i.e. f(x) = m'x, where m is the map)

setRectified
^^^^^^^^^^^^

.. java:method:: public void setRectified(boolean rectified)
   :outertype: LinearFunction

   :param rectified: If true, result is rectified (set to 0 if less than 0)


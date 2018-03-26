.. java:import:: ca.nengo.math Function

AbstractFunction
================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public abstract class AbstractFunction implements Function

   Base class for Function implementations. The default implementation of multiMap() calls map(). This will be a little slower than if both methods were to call a static function, so if multiMap speed is an issue this method could be overridden, or it might be better not to use this abstract class.

   :author: Bryan Tripp

Fields
------
DIMENSION_PROPERTY
^^^^^^^^^^^^^^^^^^

.. java:field:: public static final String DIMENSION_PROPERTY
   :outertype: AbstractFunction

   How should we refer to the dimension?

Constructors
------------
AbstractFunction
^^^^^^^^^^^^^^^^

.. java:constructor:: public AbstractFunction(int dim)
   :outertype: AbstractFunction

   :param dim: Input dimension of the function

Methods
-------
clone
^^^^^

.. java:method:: public Function clone() throws CloneNotSupportedException
   :outertype: AbstractFunction

   :throws CloneNotSupportedException: is super does not support clone

getCode
^^^^^^^

.. java:method:: public String getCode()
   :outertype: AbstractFunction

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: AbstractFunction

   **See also:** :java:ref:`ca.nengo.math.Function.getDimension()`

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: AbstractFunction

map
^^^

.. java:method:: public abstract float map(float[] from)
   :outertype: AbstractFunction

   **See also:** :java:ref:`ca.nengo.math.Function.map(float[])`

multiMap
^^^^^^^^

.. java:method:: public float[] multiMap(float[][] from)
   :outertype: AbstractFunction

   **See also:** :java:ref:`ca.nengo.math.Function.multiMap(float[][])`

setCode
^^^^^^^

.. java:method:: public void setCode(String code)
   :outertype: AbstractFunction

setName
^^^^^^^

.. java:method:: public void setName(String name)
   :outertype: AbstractFunction


FixedSignalFunction
===================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class FixedSignalFunction extends AbstractFunction

   A Function that produces a fixed sequence of outputs, independent of input.

   :author: Daniel Rasmussen

Constructors
------------
FixedSignalFunction
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public FixedSignalFunction(float[][] signal, int dimension, int reportdimension)
   :outertype: FixedSignalFunction

   :param signal: sequence defining output (each element is a (potentially) multidimensional output)
   :param dimension: Dimension of signal on which to base Function output
   :param reportdimension: value this function will report as its input dimension (this is only needed for compatibility with other components, since this function takes no input)

FixedSignalFunction
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public FixedSignalFunction(float[][] signal, int dimension)
   :outertype: FixedSignalFunction

Methods
-------
getSeriesDimension
^^^^^^^^^^^^^^^^^^

.. java:method:: public int getSeriesDimension()
   :outertype: FixedSignalFunction

   :return: Dimension of series on which to base Function output

getSignal
^^^^^^^^^

.. java:method:: public float[][] getSignal()
   :outertype: FixedSignalFunction

map
^^^

.. java:method:: public float map(float[] from)
   :outertype: FixedSignalFunction

   **See also:** :java:ref:`ca.nengo.math.impl.AbstractFunction.map(float[])`

setSeriesDimension
^^^^^^^^^^^^^^^^^^

.. java:method:: public void setSeriesDimension(int dim)
   :outertype: FixedSignalFunction

   :param dim: Dimension of series on which to base Function output

setSignal
^^^^^^^^^

.. java:method:: public void setSignal(float[][] signal)
   :outertype: FixedSignalFunction


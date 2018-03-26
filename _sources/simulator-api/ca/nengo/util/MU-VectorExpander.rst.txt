.. java:import:: java.text NumberFormat

.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.math PDF

MU.VectorExpander
=================

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public static class VectorExpander
   :outertype: MU

   A tool for growing vectors (similar to java.util.List).

   :author: Bryan Tripp

Constructors
------------
VectorExpander
^^^^^^^^^^^^^^

.. java:constructor:: public VectorExpander()
   :outertype: MU.VectorExpander

Methods
-------
add
^^^

.. java:method:: public void add(float value)
   :outertype: MU.VectorExpander

   :param value: New element to append

toArray
^^^^^^^

.. java:method:: public float[] toArray()
   :outertype: MU.VectorExpander

   :return: Array of elements in order appended


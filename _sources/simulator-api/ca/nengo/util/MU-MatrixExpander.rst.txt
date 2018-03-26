.. java:import:: java.text NumberFormat

.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.math PDF

MU.MatrixExpander
=================

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public static class MatrixExpander
   :outertype: MU

   A tool for growing matrices (similar to java.util.List).

   :author: Bryan Tripp

Constructors
------------
MatrixExpander
^^^^^^^^^^^^^^

.. java:constructor:: public MatrixExpander()
   :outertype: MU.MatrixExpander

Methods
-------
add
^^^

.. java:method:: public void add(float[] value)
   :outertype: MU.MatrixExpander

   :param value: New row to append

toArray
^^^^^^^

.. java:method:: public float[][] toArray()
   :outertype: MU.MatrixExpander

   :return: Array of rows in order appended


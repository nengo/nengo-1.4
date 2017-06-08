.. java:import:: java.awt Component

.. java:import:: java.awt FlowLayout

.. java:import:: java.util StringTokenizer

.. java:import:: javax.swing JLabel

.. java:import:: javax.swing JPanel

.. java:import:: javax.swing JTextArea

.. java:import:: ca.nengo.config IconRegistry

.. java:import:: ca.nengo.config.ui ConfigurationChangeListener

.. java:import:: ca.nengo.config.ui MatrixEditor

.. java:import:: ca.nengo.util MU.MatrixExpander

.. java:import:: ca.nengo.util MU.VectorExpander

MatrixHandler
=============

.. java:package:: ca.nengo.config.handlers
   :noindex:

.. java:type:: public class MatrixHandler extends MatrixHandlerBase

   ConfigurationHandler for float[][] values.

   :author: Bryan Tripp

Constructors
------------
MatrixHandler
^^^^^^^^^^^^^

.. java:constructor:: public MatrixHandler()
   :outertype: MatrixHandler

   ConfigurationHandler for float[][] values.

Methods
-------
CreateMatrixEditor
^^^^^^^^^^^^^^^^^^

.. java:method:: public MatrixEditor CreateMatrixEditor(Object o, ConfigurationChangeListener configListener)
   :outertype: MatrixHandler

fromString
^^^^^^^^^^

.. java:method:: @Override public Object fromString(String s)
   :outertype: MatrixHandler

fromString
^^^^^^^^^^

.. java:method:: public static float[][] fromString(String s, char colDelim, String rowDelim)
   :outertype: MatrixHandler

   :param s: A String representation of a matrix, eg from toString(float[][], char, String)
   :param colDelim: The character used to delimit matrix columns in this string
   :param rowDelim: The string (can be >1 chars) used to delimit matrix rows in this string
   :return: The matrix represented by the string

getDefaultValue
^^^^^^^^^^^^^^^

.. java:method:: public Object getDefaultValue(Class<?> c)
   :outertype: MatrixHandler

   **See also:** :java:ref:`ca.nengo.config.ConfigurationHandler.getDefaultValue(java.lang.Class)`

getRenderer
^^^^^^^^^^^

.. java:method:: @Override public Component getRenderer(Object o)
   :outertype: MatrixHandler

toString
^^^^^^^^

.. java:method:: @Override public String toString(Object o)
   :outertype: MatrixHandler

toString
^^^^^^^^

.. java:method:: public static String toString(float[][] matrix, char colDelim, String rowDelim)
   :outertype: MatrixHandler

   :param matrix: A matrix
   :param colDelim: A character to be used to delimit matrix columns
   :param rowDelim: A String to be used to delimit matrix rows
   :return: A String representation of the given matrix using the given delimiters


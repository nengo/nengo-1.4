.. java:import:: java.awt Component

.. java:import:: java.awt Font

.. java:import:: javax.swing JLabel

.. java:import:: javax.swing SwingConstants

.. java:import:: ca.nengo.config IconRegistry

.. java:import:: ca.nengo.config.ui ConfigurationChangeListener

.. java:import:: ca.nengo.config.ui MatrixEditor

VectorHandler
=============

.. java:package:: ca.nengo.config.handlers
   :noindex:

.. java:type:: public class VectorHandler extends MatrixHandlerBase

   ConfigurationHandler for float[] values.

   :author: Bryan Tripp

Constructors
------------
VectorHandler
^^^^^^^^^^^^^

.. java:constructor:: public VectorHandler()
   :outertype: VectorHandler

   ConfigurationHandler for float[] values.

Methods
-------
CreateMatrixEditor
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public MatrixEditor CreateMatrixEditor(Object o, ConfigurationChangeListener configListener)
   :outertype: VectorHandler

fromString
^^^^^^^^^^

.. java:method:: @Override public Object fromString(String s)
   :outertype: VectorHandler

getDefaultValue
^^^^^^^^^^^^^^^

.. java:method:: public Object getDefaultValue(Class<?> c)
   :outertype: VectorHandler

   **See also:** :java:ref:`ca.nengo.config.ConfigurationHandler.getDefaultValue(java.lang.Class)`

getRenderer
^^^^^^^^^^^

.. java:method:: @Override public Component getRenderer(Object o)
   :outertype: VectorHandler

toString
^^^^^^^^

.. java:method:: @Override public String toString(Object o)
   :outertype: VectorHandler


.. java:import:: java.awt Color

.. java:import:: java.awt Component

.. java:import:: java.awt FlowLayout

.. java:import:: javax.swing JButton

.. java:import:: javax.swing JCheckBox

.. java:import:: javax.swing JComponent

.. java:import:: javax.swing JPanel

.. java:import:: ca.nengo.config.ui ConfigurationChangeListener

BooleanHandler
==============

.. java:package:: ca.nengo.config.handlers
   :noindex:

.. java:type:: public class BooleanHandler extends BaseHandler

   ConfigurationHandler for Boolean values.

   :author: Bryan Tripp

Constructors
------------
BooleanHandler
^^^^^^^^^^^^^^

.. java:constructor:: public BooleanHandler()
   :outertype: BooleanHandler

   ConfigurationHandler for Boolean values.

Methods
-------
getDefaultValue
^^^^^^^^^^^^^^^

.. java:method:: public Object getDefaultValue(Class<?> c)
   :outertype: BooleanHandler

   **See also:** :java:ref:`ca.nengo.config.ConfigurationHandler.getDefaultValue(java.lang.Class)`

getEditor
^^^^^^^^^

.. java:method:: @Override public Component getEditor(Object o, ConfigurationChangeListener listener, JComponent parent)
   :outertype: BooleanHandler


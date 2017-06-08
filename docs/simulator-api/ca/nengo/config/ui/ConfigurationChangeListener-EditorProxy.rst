.. java:import:: java.awt.event ActionEvent

.. java:import:: java.awt.event ActionListener

.. java:import:: javax.swing JTree

.. java:import:: javax.swing.tree TreeCellEditor

.. java:import:: javax.swing.tree TreePath

ConfigurationChangeListener.EditorProxy
=======================================

.. java:package:: ca.nengo.config.ui
   :noindex:

.. java:type:: public interface EditorProxy
   :outertype: ConfigurationChangeListener

   An editor component (from ConfigurationHandler.getEditor(...)) must implement EditorProxy in order to allow retrieval of a new value when editing is complete. For example if the component is a JTextField, the implementation could be \ ``getValue() { jtf.getText(); }``\ .

   :author: Bryan Tripp

Methods
-------
getValue
^^^^^^^^

.. java:method:: public Object getValue()
   :outertype: ConfigurationChangeListener.EditorProxy

   :return: Current value of edited object


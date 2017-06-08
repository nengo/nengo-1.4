.. java:import:: java.awt BorderLayout

.. java:import:: java.awt Component

.. java:import:: java.awt Container

.. java:import:: java.awt Dimension

.. java:import:: java.awt FlowLayout

.. java:import:: java.awt.event ActionEvent

.. java:import:: java.awt.event ActionListener

.. java:import:: java.lang.reflect Array

.. java:import:: java.lang.reflect Constructor

.. java:import:: java.lang.reflect InvocationTargetException

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: javax.swing BoxLayout

.. java:import:: javax.swing JButton

.. java:import:: javax.swing JComboBox

.. java:import:: javax.swing JDialog

.. java:import:: javax.swing JLabel

.. java:import:: javax.swing JList

.. java:import:: javax.swing JOptionPane

.. java:import:: javax.swing JPanel

.. java:import:: javax.swing JScrollPane

.. java:import:: javax.swing JTree

.. java:import:: javax.swing.plaf.basic BasicComboBoxRenderer

.. java:import:: ca.nengo.config ClassRegistry

.. java:import:: ca.nengo.config ConfigUtil

.. java:import:: ca.nengo.config Configuration

.. java:import:: ca.nengo.config JavaSourceParser

.. java:import:: ca.nengo.config ListProperty

.. java:import:: ca.nengo.config MainHandler

.. java:import:: ca.nengo.config Property

.. java:import:: ca.nengo.config SingleValuedProperty

.. java:import:: ca.nengo.config.impl AbstractProperty

.. java:import:: ca.nengo.config.impl ConfigurationImpl

.. java:import:: ca.nengo.config.impl TemplateArrayProperty

.. java:import:: ca.nengo.config.impl TemplateProperty

.. java:import:: ca.nengo.config.ui ConfigurationTreeModel.NullValue

.. java:import:: ca.nengo.config.ui ConfigurationTreeModel.Value

.. java:import:: ca.nengo.model StructuralException

NewConfigurableDialog.ConstructionProperties
============================================

.. java:package:: ca.nengo.config.ui
   :noindex:

.. java:type:: public static class ConstructionProperties
   :outertype: NewConfigurableDialog

   Class used to pass configuration properties to created classes.

Methods
-------
getConfiguration
^^^^^^^^^^^^^^^^

.. java:method:: public Configuration getConfiguration()
   :outertype: NewConfigurableDialog.ConstructionProperties

   :return: the Configuration to use


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

NewConfigurableDialog
=====================

.. java:package:: ca.nengo.config.ui
   :noindex:

.. java:type:: public class NewConfigurableDialog extends JDialog implements ActionListener

   A dialog box through which the user can construct a new object.

   :author: Bryan Tripp

Methods
-------
actionPerformed
^^^^^^^^^^^^^^^

.. java:method:: public void actionPerformed(ActionEvent e)
   :outertype: NewConfigurableDialog

   **See also:** :java:ref:`java.awt.event.ActionListener.actionPerformed(java.awt.event.ActionEvent)`

getResult
^^^^^^^^^

.. java:method:: public Object getResult()
   :outertype: NewConfigurableDialog

   :return: Resulting object

showDialog
^^^^^^^^^^

.. java:method:: public static Object showDialog(Component comp, Class<?> type, Class<?> specificType)
   :outertype: NewConfigurableDialog

   Opens a NewConfigurableDialog through which the user can construct a new object, and returns the constructed object.

   :param comp: UI component from which a dialog is to be launched
   :param type: Class of object to be constructed
   :param specificType: An optional more specific type to be initially selected (if there is more than one implementation of the more general type above)
   :return: User-constructed object (or null if construction aborted)


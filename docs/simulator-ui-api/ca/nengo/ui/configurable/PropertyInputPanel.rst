.. java:import:: java.awt Component

.. java:import:: java.awt Container

.. java:import:: java.awt.event ActionEvent

.. java:import:: java.awt.event ActionListener

.. java:import:: javax.swing BorderFactory

.. java:import:: javax.swing BoxLayout

.. java:import:: javax.swing JButton

.. java:import:: javax.swing JDialog

.. java:import:: javax.swing JEditorPane

.. java:import:: javax.swing JLabel

.. java:import:: javax.swing JOptionPane

.. java:import:: javax.swing JPanel

.. java:import:: javax.swing.event HyperlinkEvent

.. java:import:: javax.swing.event HyperlinkListener

.. java:import:: ca.nengo.ui.lib.actions OpenURLAction

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

PropertyInputPanel
==================

.. java:package:: ca.nengo.ui.configurable
   :noindex:

.. java:type:: public abstract class PropertyInputPanel

   Swing Input panel to be used to enter in the value for a ConfigParam

   :author: Shu

Constructors
------------
PropertyInputPanel
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PropertyInputPanel(Property property)
   :outertype: PropertyInputPanel

   :param property: A description of the Configuration parameter to be configured

Methods
-------
add
^^^

.. java:method:: protected void add(Component comp)
   :outertype: PropertyInputPanel

   :param comp: Component to be added to the input panel

getDescriptor
^^^^^^^^^^^^^

.. java:method:: public Property getDescriptor()
   :outertype: PropertyInputPanel

   :return: Descriptor of the configuration parameter

getDialogParent
^^^^^^^^^^^^^^^

.. java:method:: protected JDialog getDialogParent()
   :outertype: PropertyInputPanel

getJPanel
^^^^^^^^^

.. java:method:: public JPanel getJPanel()
   :outertype: PropertyInputPanel

   :return: TODO

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: PropertyInputPanel

   :return: TODO

getValue
^^^^^^^^

.. java:method:: public abstract Object getValue()
   :outertype: PropertyInputPanel

   :return: Value of the parameter

isEnabled
^^^^^^^^^

.. java:method:: public boolean isEnabled()
   :outertype: PropertyInputPanel

   :return: TODO

isValueSet
^^^^^^^^^^

.. java:method:: public abstract boolean isValueSet()
   :outertype: PropertyInputPanel

   :return: True if configuration parameter is set

removeFromPanel
^^^^^^^^^^^^^^^

.. java:method:: protected void removeFromPanel(Component comp)
   :outertype: PropertyInputPanel

   :param comp: Component to be removed from the input panel

setEnabled
^^^^^^^^^^

.. java:method:: public void setEnabled(boolean enabled)
   :outertype: PropertyInputPanel

   :param enabled: TODO

setStatusMsg
^^^^^^^^^^^^

.. java:method:: protected void setStatusMsg(String msg)
   :outertype: PropertyInputPanel

   :param msg:

setValue
^^^^^^^^

.. java:method:: public abstract void setValue(Object value)
   :outertype: PropertyInputPanel

   :param value: Sets the configuration parameter


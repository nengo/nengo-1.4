.. java:import:: java.awt Component

.. java:import:: java.awt Dialog

.. java:import:: java.awt Dimension

.. java:import:: java.awt Frame

.. java:import:: java.awt.event ActionEvent

.. java:import:: java.awt.event ActionListener

.. java:import:: java.awt.event KeyEvent

.. java:import:: java.util Iterator

.. java:import:: java.util List

.. java:import:: java.util Vector

.. java:import:: javax.swing BorderFactory

.. java:import:: javax.swing Box

.. java:import:: javax.swing BoxLayout

.. java:import:: javax.swing JButton

.. java:import:: javax.swing JComponent

.. java:import:: javax.swing JDialog

.. java:import:: javax.swing JOptionPane

.. java:import:: javax.swing JPanel

.. java:import:: javax.swing KeyStroke

.. java:import:: javax.swing.text MutableAttributeSet

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable PropertyInputPanel

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.objects.activities TrackedAction

.. java:import:: ca.nengo.ui.lib.util UserMessages

ConfigDialog
============

.. java:package:: ca.nengo.ui.configurable.managers
   :noindex:

.. java:type:: public class ConfigDialog extends JDialog

   Configuration dialog

   :author: Shu Wu

Fields
------
propertyInputPanels
^^^^^^^^^^^^^^^^^^^

.. java:field:: protected Vector<PropertyInputPanel> propertyInputPanels
   :outertype: ConfigDialog

Constructors
------------
ConfigDialog
^^^^^^^^^^^^

.. java:constructor:: public ConfigDialog(UserConfigurer configManager, Frame owner)
   :outertype: ConfigDialog

   :param configManager: Parent Configuration Manager
   :param owner: Component this dialog shall be added to

ConfigDialog
^^^^^^^^^^^^

.. java:constructor:: public ConfigDialog(UserConfigurer configManager, Dialog owner)
   :outertype: ConfigDialog

   :param configManager: Parent Configuration Manager
   :param owner: Component this dialog shall be added to

Methods
-------
addDescriptors
^^^^^^^^^^^^^^

.. java:method:: protected void addDescriptors(List<Property> propDescriptors)
   :outertype: ConfigDialog

   Adds property descriptors to the panel

applyProperties
^^^^^^^^^^^^^^^

.. java:method:: protected boolean applyProperties()
   :outertype: ConfigDialog

   Gets value entered in the dialog and applies them to the properties set

   :return: Whether operation was successful

checkPropreties
^^^^^^^^^^^^^^^

.. java:method:: protected boolean checkPropreties()
   :outertype: ConfigDialog

completeConfiguration
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void completeConfiguration() throws ConfigException
   :outertype: ConfigDialog

getConfigurer
^^^^^^^^^^^^^

.. java:method:: public UserConfigurer getConfigurer()
   :outertype: ConfigDialog

   :return: TODO

initPanelBottom
^^^^^^^^^^^^^^^

.. java:method:: protected void initPanelBottom(JPanel panel)
   :outertype: ConfigDialog

   Initializes the dialog contents bottom

initPanelTop
^^^^^^^^^^^^

.. java:method:: protected void initPanelTop(JPanel panel)
   :outertype: ConfigDialog

   Initializes the dialog contents top

initialize
^^^^^^^^^^

.. java:method:: protected void initialize(UserConfigurer configManager, Component owner)
   :outertype: ConfigDialog

   Initialization to be called from the constructor

   :param configManager: Configuration manager parent
   :param owner: Component the dialog is to be added to


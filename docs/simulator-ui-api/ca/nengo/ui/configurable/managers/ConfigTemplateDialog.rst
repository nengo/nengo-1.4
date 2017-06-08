.. java:import:: java.awt Dialog

.. java:import:: java.awt Dimension

.. java:import:: java.awt Frame

.. java:import:: java.awt.event ActionEvent

.. java:import:: java.awt.event ActionListener

.. java:import:: java.util Iterator

.. java:import:: javax.swing BoxLayout

.. java:import:: javax.swing JButton

.. java:import:: javax.swing JComboBox

.. java:import:: javax.swing JLabel

.. java:import:: javax.swing JOptionPane

.. java:import:: javax.swing JPanel

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable PropertyInputPanel

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.util Util

ConfigTemplateDialog
====================

.. java:package:: ca.nengo.ui.configurable.managers
   :noindex:

.. java:type:: public class ConfigTemplateDialog extends ConfigDialog

   A Configuration dialog which allows the user to manage templates

   :author: Shu

Constructors
------------
ConfigTemplateDialog
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ConfigTemplateDialog(UserTemplateConfigurer configManager, Dialog owner)
   :outertype: ConfigTemplateDialog

   :param configManager: TODO
   :param owner: TODO

ConfigTemplateDialog
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ConfigTemplateDialog(UserTemplateConfigurer configManager, Frame owner)
   :outertype: ConfigTemplateDialog

   :param configManager: TODO
   :param owner: TODO

Methods
-------
completeConfiguration
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void completeConfiguration() throws ConfigException
   :outertype: ConfigTemplateDialog

getConfigurer
^^^^^^^^^^^^^

.. java:method:: @Override public UserTemplateConfigurer getConfigurer()
   :outertype: ConfigTemplateDialog

initPanelTop
^^^^^^^^^^^^

.. java:method:: @Override protected void initPanelTop(JPanel panel)
   :outertype: ConfigTemplateDialog

updateFromTemplate
^^^^^^^^^^^^^^^^^^

.. java:method:: protected void updateFromTemplate()
   :outertype: ConfigTemplateDialog

   Loads the properties associated with the item selected in the file drop down list


.. java:import:: java.awt.event ActionEvent

.. java:import:: java.awt.event ActionListener

.. java:import:: javax.swing JTree

.. java:import:: javax.swing.tree TreeCellEditor

.. java:import:: javax.swing.tree TreePath

ConfigurationChangeListener
===========================

.. java:package:: ca.nengo.config.ui
   :noindex:

.. java:type:: public class ConfigurationChangeListener implements ActionListener

   A listener for changes to Property values. TODO: is there a better option than EditorProxy? TODO: can we avoid references to this class from ca.nengo.config?

   :author: Bryan Tripp

Constructors
------------
ConfigurationChangeListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ConfigurationChangeListener(JTree tree, TreePath path)
   :outertype: ConfigurationChangeListener

   :param tree: Parent tree object
   :param path: Current path

Methods
-------
actionPerformed
^^^^^^^^^^^^^^^

.. java:method:: public void actionPerformed(ActionEvent e)
   :outertype: ConfigurationChangeListener

   **See also:** :java:ref:`java.awt.event.ActionListener.actionPerformed(java.awt.event.ActionEvent)`

cancelChanges
^^^^^^^^^^^^^

.. java:method:: public void cancelChanges()
   :outertype: ConfigurationChangeListener

commitChanges
^^^^^^^^^^^^^

.. java:method:: public void commitChanges()
   :outertype: ConfigurationChangeListener

   Event for when changes are committed

isChangeCancelled
^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isChangeCancelled()
   :outertype: ConfigurationChangeListener

   :return: Has change been cancelled?

isChangeCommited
^^^^^^^^^^^^^^^^

.. java:method:: public boolean isChangeCommited()
   :outertype: ConfigurationChangeListener

   :return: Already committed?

setProxy
^^^^^^^^

.. java:method:: public void setProxy(EditorProxy proxy)
   :outertype: ConfigurationChangeListener

   Called by a ConfigurationHandler's editor.

   :param proxy: Provides access to an updated property value after it is changed by the user


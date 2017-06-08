.. java:import:: java.awt Container

.. java:import:: java.awt.event ActionEvent

.. java:import:: javax.swing AbstractAction

.. java:import:: javax.swing JButton

.. java:import:: javax.swing JDialog

.. java:import:: javax.swing JLabel

.. java:import:: javax.swing JTextField

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable PropertyInputPanel

.. java:import:: ca.nengo.ui.configurable.descriptors PCouplingMatrix

.. java:import:: ca.nengo.ui.configurable.descriptors PTerminationWeights

.. java:import:: ca.nengo.ui.configurable.managers ConfigManager

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.lib.util Util

TerminationWeightsInputPanel.EditMatrixAction
=============================================

.. java:package:: ca.nengo.ui.configurable.panels
   :noindex:

.. java:type::  class EditMatrixAction extends AbstractAction
   :outertype: TerminationWeightsInputPanel

   User triggered action to edit the termination weights matrix

   :author: Shu Wu

Constructors
------------
EditMatrixAction
^^^^^^^^^^^^^^^^

.. java:constructor:: public EditMatrixAction()
   :outertype: TerminationWeightsInputPanel.EditMatrixAction

Methods
-------
actionPerformed
^^^^^^^^^^^^^^^

.. java:method:: public void actionPerformed(ActionEvent e)
   :outertype: TerminationWeightsInputPanel.EditMatrixAction


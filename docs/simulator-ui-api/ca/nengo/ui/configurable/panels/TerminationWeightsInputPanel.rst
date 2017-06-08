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

TerminationWeightsInputPanel
============================

.. java:package:: ca.nengo.ui.configurable.panels
   :noindex:

.. java:type:: public class TerminationWeightsInputPanel extends PropertyInputPanel

   Input panel for Termination Weights Matrix

   :author: Shu

Constructors
------------
TerminationWeightsInputPanel
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public TerminationWeightsInputPanel(PTerminationWeights property)
   :outertype: TerminationWeightsInputPanel

   :param property: TODO

Methods
-------
editMatrix
^^^^^^^^^^

.. java:method:: protected void editMatrix()
   :outertype: TerminationWeightsInputPanel

   Edits the termination weights matrix

getDescriptor
^^^^^^^^^^^^^

.. java:method:: @Override public PTerminationWeights getDescriptor()
   :outertype: TerminationWeightsInputPanel

getFromSize
^^^^^^^^^^^

.. java:method:: protected int getFromSize()
   :outertype: TerminationWeightsInputPanel

   :return: From size, of the matrix to be created

getToSize
^^^^^^^^^

.. java:method:: protected int getToSize()
   :outertype: TerminationWeightsInputPanel

   :return: To size, of the matrix to be created

getValue
^^^^^^^^

.. java:method:: @Override public float[][] getValue()
   :outertype: TerminationWeightsInputPanel

isValueSet
^^^^^^^^^^

.. java:method:: @Override public boolean isValueSet()
   :outertype: TerminationWeightsInputPanel

setValue
^^^^^^^^

.. java:method:: @Override public void setValue(Object value)
   :outertype: TerminationWeightsInputPanel


.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable PropertyInputPanel

.. java:import:: ca.nengo.ui.configurable.panels CouplingMatrixPanel

PCouplingMatrix
===============

.. java:package:: ca.nengo.ui.configurable.descriptors
   :noindex:

.. java:type:: public class PCouplingMatrix extends Property

   Config Descriptor for a Coupling Matrix

   :author: Shu Wu

Constructors
------------
PCouplingMatrix
^^^^^^^^^^^^^^^

.. java:constructor:: public PCouplingMatrix(float[][] matrixValues)
   :outertype: PCouplingMatrix

   :param matrixValues: TODO

PCouplingMatrix
^^^^^^^^^^^^^^^

.. java:constructor:: public PCouplingMatrix(int fromSize, int toSize)
   :outertype: PCouplingMatrix

   :param fromSize: TODO
   :param toSize: TODO

Methods
-------
createInputPanel
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected PropertyInputPanel createInputPanel()
   :outertype: PCouplingMatrix

getTypeClass
^^^^^^^^^^^^

.. java:method:: @Override public Class<float[][]> getTypeClass()
   :outertype: PCouplingMatrix

getTypeName
^^^^^^^^^^^

.. java:method:: @Override public String getTypeName()
   :outertype: PCouplingMatrix


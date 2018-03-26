.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable PropertyInputPanel

.. java:import:: ca.nengo.ui.configurable.matrixEditor CouplingMatrixImpl

.. java:import:: ca.nengo.ui.configurable.matrixEditor MatrixEditor

.. java:import:: ca.nengo.ui.lib.util Util

CouplingMatrixPanel
===================

.. java:package:: ca.nengo.ui.configurable.panels
   :noindex:

.. java:type:: public class CouplingMatrixPanel extends PropertyInputPanel

   Input panel for a matrix

   :author: Shu Wu

Constructors
------------
CouplingMatrixPanel
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CouplingMatrixPanel(Property property, int fromSize, int toSize)
   :outertype: CouplingMatrixPanel

   :param property: TODO
   :param fromSize: TODO
   :param toSize: TODO

Methods
-------
getValue
^^^^^^^^

.. java:method:: @Override public float[][] getValue()
   :outertype: CouplingMatrixPanel

isValueSet
^^^^^^^^^^

.. java:method:: @Override public boolean isValueSet()
   :outertype: CouplingMatrixPanel

setValue
^^^^^^^^

.. java:method:: @Override public void setValue(Object value)
   :outertype: CouplingMatrixPanel


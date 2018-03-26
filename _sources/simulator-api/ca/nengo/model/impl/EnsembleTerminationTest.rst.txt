.. java:import:: ca.nengo TestUtil

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model.impl EnsembleTermination

.. java:import:: ca.nengo.model.impl LinearExponentialTermination

.. java:import:: junit.framework TestCase

EnsembleTerminationTest
=======================

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class EnsembleTerminationTest extends TestCase

   Unit tests for EnsembleTermination.

   :author: Bryan Tripp

Constructors
------------
EnsembleTerminationTest
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public EnsembleTerminationTest(String arg0)
   :outertype: EnsembleTerminationTest

   :param arg0:

Methods
-------
setUp
^^^^^

.. java:method:: protected void setUp() throws Exception
   :outertype: EnsembleTerminationTest

   **See also:** :java:ref:`junit.framework.TestCase.setUp()`

testSetModulatory
^^^^^^^^^^^^^^^^^

.. java:method:: public void testSetModulatory()
   :outertype: EnsembleTerminationTest

   Test method for \ :java:ref:`ca.nengo.model.impl.EnsembleTermination.setModulatory(boolean)`\ .

testSetTau
^^^^^^^^^^

.. java:method:: public void testSetTau() throws StructuralException
   :outertype: EnsembleTerminationTest

   Test method for \ :java:ref:`ca.nengo.model.impl.EnsembleTermination.setTau(float)`\ .

   :throws StructuralException:


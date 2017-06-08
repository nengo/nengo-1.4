.. java:import:: java.lang.reflect InvocationTargetException

.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math.impl ConstantFunction

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl FunctionInput

.. java:import:: ca.nengo.model.impl NetworkImpl

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef NEFEnsembleFactory

.. java:import:: ca.nengo.model.nef.impl NEFEnsembleFactoryImpl

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.models.nodes UINetwork

.. java:import:: ca.nengo.util Probe

IntegratorExample
=================

.. java:package:: ca.nengo.ui.test
   :noindex:

.. java:type:: public class IntegratorExample

   In this example, an Integrator network is constructed

   :author: Shu Wu

Fields
------
tau
^^^

.. java:field:: public static float tau
   :outertype: IntegratorExample

Constructors
------------
IntegratorExample
^^^^^^^^^^^^^^^^^

.. java:constructor:: public IntegratorExample()
   :outertype: IntegratorExample

Methods
-------
createUINetwork
^^^^^^^^^^^^^^^

.. java:method:: public void createUINetwork(NengoGraphics nengoGraphics) throws StructuralException, SimulationException
   :outertype: IntegratorExample

main
^^^^

.. java:method:: public static void main(String[] args)
   :outertype: IntegratorExample


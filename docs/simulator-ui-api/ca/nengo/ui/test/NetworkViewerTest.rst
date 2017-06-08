.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math.impl ConstantFunction

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl FunctionInput

.. java:import:: ca.nengo.model.impl NetworkImpl

.. java:import:: ca.nengo.ui.actions RunSimulatorAction

.. java:import:: ca.nengo.ui.dev ExampleRunner

.. java:import:: ca.nengo.ui.models.nodes UINetwork

NetworkViewerTest
=================

.. java:package:: ca.nengo.ui.test
   :noindex:

.. java:type:: public class NetworkViewerTest extends ExampleRunner

   Starts Nengo with a network viewer open

   :author: Shu Wu

Constructors
------------
NetworkViewerTest
^^^^^^^^^^^^^^^^^

.. java:constructor:: public NetworkViewerTest() throws StructuralException
   :outertype: NetworkViewerTest

Methods
-------
CreateNetwork
^^^^^^^^^^^^^

.. java:method:: public static Network CreateNetwork() throws StructuralException
   :outertype: NetworkViewerTest

doStuff
^^^^^^^

.. java:method:: @Override protected void doStuff(UINetwork network)
   :outertype: NetworkViewerTest

main
^^^^

.. java:method:: public static void main(String[] args)
   :outertype: NetworkViewerTest


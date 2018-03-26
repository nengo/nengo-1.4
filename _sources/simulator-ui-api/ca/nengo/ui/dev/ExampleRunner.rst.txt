.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.lib.objects.activities TrackedStatusMsg

.. java:import:: ca.nengo.ui.models.nodes UINetwork

ExampleRunner
=============

.. java:package:: ca.nengo.ui.dev
   :noindex:

.. java:type:: public class ExampleRunner

   Used to conveniently create a NeoGraphics instance with an existing Network model

   :author: Shu Wu

Constructors
------------
ExampleRunner
^^^^^^^^^^^^^

.. java:constructor:: public ExampleRunner(Network network)
   :outertype: ExampleRunner

   :param name: Name to be given to this instance
   :param network: Network to be given to NeoGraphics

ExampleRunner
^^^^^^^^^^^^^

.. java:constructor:: public ExampleRunner(UINetwork network)
   :outertype: ExampleRunner

   :param name: Name to be given to this instance
   :param network: Network to be given to NeoGraphics

Methods
-------
doStuff
^^^^^^^

.. java:method:: protected void doStuff(UINetwork network)
   :outertype: ExampleRunner

processNetwork
^^^^^^^^^^^^^^

.. java:method:: protected void processNetwork(UINetwork network)
   :outertype: ExampleRunner


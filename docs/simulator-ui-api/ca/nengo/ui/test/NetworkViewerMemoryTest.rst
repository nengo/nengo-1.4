.. java:import:: java.lang.reflect InvocationTargetException

.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math.impl ConstantFunction

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl FunctionInput

.. java:import:: ca.nengo.model.impl NetworkImpl

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef NEFEnsembleFactory

.. java:import:: ca.nengo.model.nef.impl NEFEnsembleFactoryImpl

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.models.nodes UINetwork

NetworkViewerMemoryTest
=======================

.. java:package:: ca.nengo.ui.test
   :noindex:

.. java:type:: public class NetworkViewerMemoryTest

   Just a quick check for one type of memory leaks in Network Viewer.

   :author: Shu

Fields
------
network
^^^^^^^

.. java:field:: static UINetwork network
   :outertype: NetworkViewerMemoryTest

Methods
-------
createNetwork
^^^^^^^^^^^^^

.. java:method:: public static Network createNetwork() throws StructuralException
   :outertype: NetworkViewerMemoryTest

getApproximateUsedMemory
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static long getApproximateUsedMemory()
   :outertype: NetworkViewerMemoryTest

main
^^^^

.. java:method:: public static void main(String[] args)
   :outertype: NetworkViewerMemoryTest

   :param args:

printMemoryUsed
^^^^^^^^^^^^^^^

.. java:method:: public static void printMemoryUsed(String msg)
   :outertype: NetworkViewerMemoryTest


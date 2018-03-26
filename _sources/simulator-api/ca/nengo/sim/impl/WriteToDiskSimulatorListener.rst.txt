.. java:import:: java.io BufferedWriter

.. java:import:: java.io File

.. java:import:: java.io FileWriter

.. java:import:: java.io IOException

.. java:import:: java.util Calendar

.. java:import:: ca.nengo.util Probe

.. java:import:: ca.nengo.sim SimulatorEvent

.. java:import:: ca.nengo.sim SimulatorListener

WriteToDiskSimulatorListener
============================

.. java:package:: ca.nengo.sim.impl
   :noindex:

.. java:type:: public class WriteToDiskSimulatorListener implements SimulatorListener

   A method of writing to disk values being tracked by a probe. This class is designed to be used in cases where a simulation must run for a long period of time, and it is likely that the amount of data being stored will cause issues with the proper running of Nengo. By attaching a WriteToDiskSimulatorListener to a simulator instance, progress is saved to disk after each recordInterval. Example usage (Python syntax): probe_error = network.getSimulator().addProbe("error",error.X,True) file_error = File("output/error.csv") listener_error = WriteToDiskSimulatorListener(file_error,probe_error,0.005) network.simulator.addSimulatorListener(listener_error)

   :author: Trevor Bekolay

Constructors
------------
WriteToDiskSimulatorListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public WriteToDiskSimulatorListener(File file, Probe targetProbe, float recordInterval)
   :outertype: WriteToDiskSimulatorListener

   :param file: The file that progress will be saved to. If it already exists, it will be overwritten.
   :param targetProbe: The Probe from which data will be collected.
   :param recordInterval: How often data will be written to disk. To record every timestep, use 0.0.

Methods
-------
processEvent
^^^^^^^^^^^^

.. java:method:: public void processEvent(SimulatorEvent event)
   :outertype: WriteToDiskSimulatorListener

   :param event: The SimulatorEvent corresponding to the current state of the simulator.


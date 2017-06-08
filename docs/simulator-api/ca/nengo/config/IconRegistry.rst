.. java:import:: java.awt Color

.. java:import:: java.awt Component

.. java:import:: java.awt Graphics

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: javax.swing Icon

.. java:import:: javax.swing ImageIcon

.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.dynamics DynamicalSystem

.. java:import:: ca.nengo.dynamics Integrator

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Noise

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.model.neuron SpikeGenerator

.. java:import:: ca.nengo.model.neuron SynapticIntegrator

.. java:import:: ca.nengo.util SpikePattern

IconRegistry
============

.. java:package:: ca.nengo.config
   :noindex:

.. java:type:: public class IconRegistry

   A registry of graphical Icons that can be used for displaying Property values.

   :author: Bryan Tripp

Methods
-------
getIcon
^^^^^^^

.. java:method:: public Icon getIcon(Object o)
   :outertype: IconRegistry

   :param o: An object
   :return: An icon to use in displaying the given object

getIcon
^^^^^^^

.. java:method:: public Icon getIcon(Class<?> c)
   :outertype: IconRegistry

   :param c: Class of object
   :return: An icon to use in displaying objects of the given class

getInstance
^^^^^^^^^^^

.. java:method:: public static IconRegistry getInstance()
   :outertype: IconRegistry

   :return: Singleton instance

setIcon
^^^^^^^

.. java:method:: public void setIcon(Class<?> c, Icon icon)
   :outertype: IconRegistry

   :param c: A class
   :param icon: An Icon to use for objects of the given class

setIcon
^^^^^^^

.. java:method:: public void setIcon(Class<?> c, String path)
   :outertype: IconRegistry

   :param c: A class
   :param path: Path to an image file from which to make an Icon for objects of the given class


.. java:import:: java.io Serializable

.. java:import:: ca.nengo.model.neuron SpikeGenerator

SpikeGeneratorFactory
=====================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public interface SpikeGeneratorFactory extends Serializable

   Creates SpikeGenerators. Implementations should have a zero-arg constructor that parameterizes the factory with defaults, and accessor methods for changing these parameters as appropriate.

   :author: Bryan Tripp

Methods
-------
make
^^^^

.. java:method:: public SpikeGenerator make()
   :outertype: SpikeGeneratorFactory

   :return: Sets defaults


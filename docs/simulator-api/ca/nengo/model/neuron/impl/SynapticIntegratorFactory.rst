.. java:import:: java.io Serializable

.. java:import:: ca.nengo.model.neuron SynapticIntegrator

SynapticIntegratorFactory
=========================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public interface SynapticIntegratorFactory extends Serializable

   Creates SynapticIntegrators. Implementations should have a zero-arg constructor that parameterizes the factory with defaults, and accessor methods for changing these parameters as appropriate.

   :author: Bryan Tripp

Methods
-------
make
^^^^

.. java:method:: public SynapticIntegrator make()
   :outertype: SynapticIntegratorFactory

   :return: Synaptic integrator with defaults


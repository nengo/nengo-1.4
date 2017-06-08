.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.math.impl IndicatorPDF

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl NodeFactory

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.model.neuron SpikeGenerator

.. java:import:: ca.nengo.model.neuron SynapticIntegrator

LIFNeuronFactory
================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public class LIFNeuronFactory implements NodeFactory

   A factory for leaky-integrate-and-fire neurons.

   :author: Bryan Tripp

Constructors
------------
LIFNeuronFactory
^^^^^^^^^^^^^^^^

.. java:constructor:: public LIFNeuronFactory(float tauRC, float tauRef, PDF maxRate, PDF intercept)
   :outertype: LIFNeuronFactory

   :param tauRC: Spike generator membrane time constant (s)
   :param tauRef: Spike generator refractory time (s)
   :param maxRate: Maximum firing rate distribution (spikes/s)
   :param intercept: Level of summed input at which spiking begins (arbitrary current units)

LIFNeuronFactory
^^^^^^^^^^^^^^^^

.. java:constructor:: public LIFNeuronFactory()
   :outertype: LIFNeuronFactory

   Uses default parameters.

Methods
-------
getIntercept
^^^^^^^^^^^^

.. java:method:: public PDF getIntercept()
   :outertype: LIFNeuronFactory

   :return: Level of summed input at which spiking begins (arbitrary current units)

getMaxRate
^^^^^^^^^^

.. java:method:: public PDF getMaxRate()
   :outertype: LIFNeuronFactory

   :return: Maximum firing rate distribution (spikes/s)

getTauRC
^^^^^^^^

.. java:method:: public float getTauRC()
   :outertype: LIFNeuronFactory

   :return: Spike generator membrane time constant (s)

getTauRef
^^^^^^^^^

.. java:method:: public float getTauRef()
   :outertype: LIFNeuronFactory

   :return: Spike generator refractory time (s)

getTypeDescription
^^^^^^^^^^^^^^^^^^

.. java:method:: public String getTypeDescription()
   :outertype: LIFNeuronFactory

   **See also:** :java:ref:`ca.nengo.model.impl.NodeFactory.getTypeDescription()`

make
^^^^

.. java:method:: public Neuron make(String name) throws StructuralException
   :outertype: LIFNeuronFactory

   **See also:** :java:ref:`ca.nengo.model.impl.NodeFactory.make(String)`

setIntercept
^^^^^^^^^^^^

.. java:method:: public void setIntercept(PDF intercept)
   :outertype: LIFNeuronFactory

   :param intercept: Level of summed input at which spiking begins (arbitrary current units)

setMaxRate
^^^^^^^^^^

.. java:method:: public void setMaxRate(PDF maxRate)
   :outertype: LIFNeuronFactory

   :param maxRate: Maximum firing rate distribution (spikes/s)

setTauRC
^^^^^^^^

.. java:method:: public void setTauRC(float tauRC)
   :outertype: LIFNeuronFactory

   :param tauRC: Spike generator membrane time constant (s)

setTauRef
^^^^^^^^^

.. java:method:: public void setTauRef(float tauRef)
   :outertype: LIFNeuronFactory

   :param tauRef: Spike generator refractory time (s)


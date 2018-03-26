.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.math.impl IndicatorPDF

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl NodeFactory

.. java:import:: ca.nengo.model.neuron SpikeGenerator

.. java:import:: ca.nengo.model.neuron SynapticIntegrator

ALIFNeuronFactory
=================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public class ALIFNeuronFactory implements NodeFactory

   A factory for adapting leaky integrate-and-fire neurons.

   :author: Bryan Tripp

Constructors
------------
ALIFNeuronFactory
^^^^^^^^^^^^^^^^^

.. java:constructor:: public ALIFNeuronFactory(PDF maxRate, PDF intercept, PDF incN, float tauRef, float tauRC, float tauN)
   :outertype: ALIFNeuronFactory

   :param maxRate: Maximum firing rate distribution (spikes/s)
   :param intercept: Level of summed input at which spiking begins (arbitrary current units)
   :param incN: Increment of adaptation-related ion concentration with each spike (arbitrary units)
   :param tauRef: Spike generator refractory time (s)
   :param tauRC: Spike generator membrane time constant (s)
   :param tauN: Time constant of adaptation-related ion decay (s)

ALIFNeuronFactory
^^^^^^^^^^^^^^^^^

.. java:constructor:: public ALIFNeuronFactory()
   :outertype: ALIFNeuronFactory

   Uses default parameters.

Methods
-------
getIncN
^^^^^^^

.. java:method:: public PDF getIncN()
   :outertype: ALIFNeuronFactory

   :return: Increment of adaptation-related ion concentration with each spike (arbitrary units)

getIntercept
^^^^^^^^^^^^

.. java:method:: public PDF getIntercept()
   :outertype: ALIFNeuronFactory

   :return: Level of summed input at which spiking begins (arbitrary current units)

getMaxRate
^^^^^^^^^^

.. java:method:: public PDF getMaxRate()
   :outertype: ALIFNeuronFactory

   :return: Maximum firing rate distribution (spikes/s)

getTauN
^^^^^^^

.. java:method:: public float getTauN()
   :outertype: ALIFNeuronFactory

   :return: Time constant of adaptation-related ion decay (s)

getTauRC
^^^^^^^^

.. java:method:: public float getTauRC()
   :outertype: ALIFNeuronFactory

   :return: Spike generator membrane time constant (s)

getTauRef
^^^^^^^^^

.. java:method:: public float getTauRef()
   :outertype: ALIFNeuronFactory

   :return: Spike generator refractory time (s)

getTypeDescription
^^^^^^^^^^^^^^^^^^

.. java:method:: public String getTypeDescription()
   :outertype: ALIFNeuronFactory

   **See also:** :java:ref:`ca.nengo.model.impl.NodeFactory.getTypeDescription()`

make
^^^^

.. java:method:: public Node make(String name) throws StructuralException
   :outertype: ALIFNeuronFactory

   **See also:** :java:ref:`ca.nengo.model.impl.NodeFactory.make(java.lang.String)`

setIncN
^^^^^^^

.. java:method:: public void setIncN(PDF incN)
   :outertype: ALIFNeuronFactory

   :param incN: Increment of adaptation-related ion concentration with each spike (arbitrary units)

setIntercept
^^^^^^^^^^^^

.. java:method:: public void setIntercept(PDF intercept)
   :outertype: ALIFNeuronFactory

   :param intercept: Level of summed input at which spiking begins (arbitrary current units)

setMaxRate
^^^^^^^^^^

.. java:method:: public void setMaxRate(PDF maxRate)
   :outertype: ALIFNeuronFactory

   :param maxRate: Maximum firing rate distribution (spikes/s)

setTauN
^^^^^^^

.. java:method:: public void setTauN(float tauN)
   :outertype: ALIFNeuronFactory

   :param tauN: Time constant of adaptation-related ion decay (s)

setTauRC
^^^^^^^^

.. java:method:: public void setTauRC(float tauRC)
   :outertype: ALIFNeuronFactory

   :param tauRC: Spike generator membrane time constant (s)

setTauRef
^^^^^^^^^

.. java:method:: public void setTauRef(float tauRef)
   :outertype: ALIFNeuronFactory

   :param tauRef: Spike generator refractory time (s)


.. java:import:: java.util ArrayList

.. java:import:: java.util HashMap

.. java:import:: java.util List

.. java:import:: java.util Properties

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Noise

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model RealOutput

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model SpikeOutput

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl BasicOrigin

.. java:import:: ca.nengo.model.nef NEFNode

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.model.neuron SpikeGenerator

.. java:import:: ca.nengo.model.neuron SynapticIntegrator

.. java:import:: ca.nengo.util ScriptGenException

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util TimeSeries1D

.. java:import:: ca.nengo.util VisiblyMutable

.. java:import:: ca.nengo.util VisiblyMutableUtils

.. java:import:: ca.nengo.util.impl TimeSeries1DImpl

SpikingNeuron
=============

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public class SpikingNeuron implements Neuron, Probeable, NEFNode

   A neuron model composed of a SynapticIntegrator and a SpikeGenerator.

   :author: Bryan Tripp

Fields
------
CURRENT
^^^^^^^

.. java:field:: public static final String CURRENT
   :outertype: SpikingNeuron

   Name of Origin representing unscaled and unbiased current entering the soma.

Constructors
------------
SpikingNeuron
^^^^^^^^^^^^^

.. java:constructor:: public SpikingNeuron(SynapticIntegrator integrator, SpikeGenerator generator, float scale, float bias, String name)
   :outertype: SpikingNeuron

   Note: current = scale * (weighted sum of inputs at each termination) * (radial input) + bias.

   :param integrator: SynapticIntegrator used to model dendritic/somatic integration of inputs to this Neuron
   :param generator: SpikeGenerator used to model spike generation at the axon hillock of this Neuron
   :param scale: A coefficient that scales summed input
   :param bias: A bias current that models unaccounted-for inputs and/or intrinsic currents
   :param name: A unique name for this neuron in the context of the Network or Ensemble to which it belongs

Methods
-------
addChangeListener
^^^^^^^^^^^^^^^^^

.. java:method:: public void addChangeListener(Listener listener)
   :outertype: SpikingNeuron

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.addChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

clone
^^^^^

.. java:method:: @Override public SpikingNeuron clone() throws CloneNotSupportedException
   :outertype: SpikingNeuron

fireVisibleChangeEvent
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void fireVisibleChangeEvent()
   :outertype: SpikingNeuron

   Called by subclasses when properties have changed in such a way that the display of the ensemble may need updating.

getBias
^^^^^^^

.. java:method:: public float getBias()
   :outertype: SpikingNeuron

   :return: The bias current that models unaccounted-for inputs and/or intrinsic currents

getChildren
^^^^^^^^^^^

.. java:method:: public Node[] getChildren()
   :outertype: SpikingNeuron

getDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public String getDocumentation()
   :outertype: SpikingNeuron

   **See also:** :java:ref:`ca.nengo.model.Node.getDocumentation()`

getGenerator
^^^^^^^^^^^^

.. java:method:: public SpikeGenerator getGenerator()
   :outertype: SpikingNeuron

   :return: The SpikeGenerator used to model spike generation at the axon hillock of this Neuron

getHistory
^^^^^^^^^^

.. java:method:: public TimeSeries getHistory(String stateName) throws SimulationException
   :outertype: SpikingNeuron

   Available states include "I" (net current into SpikeGenerator) and the states of the SpikeGenerator.

   **See also:** :java:ref:`ca.nengo.model.Probeable.getHistory(java.lang.String)`

getIntegrator
^^^^^^^^^^^^^

.. java:method:: public SynapticIntegrator getIntegrator()
   :outertype: SpikingNeuron

   :return: The SynapticIntegrator used to model dendritic/somatic integration of inputs to this Neuron

getMode
^^^^^^^

.. java:method:: public SimulationMode getMode()
   :outertype: SpikingNeuron

   **See also:** :java:ref:`ca.nengo.model.neuron.Neuron.getMode()`

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: SpikingNeuron

   **See also:** :java:ref:`ca.nengo.model.Node.getName()`

getNoise
^^^^^^^^

.. java:method:: public Noise getNoise()
   :outertype: SpikingNeuron

   :return: Noise object applied to this neuron

getOrigin
^^^^^^^^^

.. java:method:: public Origin getOrigin(String name) throws StructuralException
   :outertype: SpikingNeuron

   **See also:** :java:ref:`ca.nengo.model.neuron.Neuron.getOrigin(java.lang.String)`

getOrigins
^^^^^^^^^^

.. java:method:: public Origin[] getOrigins()
   :outertype: SpikingNeuron

   **See also:** :java:ref:`ca.nengo.model.neuron.Neuron.getOrigins()`

getScale
^^^^^^^^

.. java:method:: public float getScale()
   :outertype: SpikingNeuron

   :return: The coefficient that scales summed input

getTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination getTermination(String name) throws StructuralException
   :outertype: SpikingNeuron

   **See also:** :java:ref:`ca.nengo.model.Node.getTermination(java.lang.String)`

getTerminations
^^^^^^^^^^^^^^^

.. java:method:: public Termination[] getTerminations()
   :outertype: SpikingNeuron

   **See also:** :java:ref:`ca.nengo.model.Node.getTerminations()`

listStates
^^^^^^^^^^

.. java:method:: public Properties listStates()
   :outertype: SpikingNeuron

   **See also:** :java:ref:`ca.nengo.model.Probeable.listStates()`

removeChangeListener
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeChangeListener(Listener listener)
   :outertype: SpikingNeuron

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.removeChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: SpikingNeuron

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: public void run(float startTime, float endTime) throws SimulationException
   :outertype: SpikingNeuron

   **See also:** :java:ref:`ca.nengo.model.neuron.Neuron.run(float,float)`

setBias
^^^^^^^

.. java:method:: public void setBias(float bias)
   :outertype: SpikingNeuron

   :param bias: New bias current

setDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public void setDocumentation(String text)
   :outertype: SpikingNeuron

   **See also:** :java:ref:`ca.nengo.model.Node.setDocumentation(java.lang.String)`

setGenerator
^^^^^^^^^^^^

.. java:method:: public void setGenerator(SpikeGenerator generator)
   :outertype: SpikingNeuron

   :param generator: New SpikeGenerator

setIntegrator
^^^^^^^^^^^^^

.. java:method:: public void setIntegrator(SynapticIntegrator integrator)
   :outertype: SpikingNeuron

   :param integrator: New synaptic integrator

setMode
^^^^^^^

.. java:method:: public void setMode(SimulationMode mode)
   :outertype: SpikingNeuron

   **See also:** :java:ref:`ca.nengo.model.neuron.Neuron.setMode(ca.nengo.model.SimulationMode)`

setName
^^^^^^^

.. java:method:: public void setName(String name) throws StructuralException
   :outertype: SpikingNeuron

   :param name: The new name

setNoise
^^^^^^^^

.. java:method:: public void setNoise(Noise noise)
   :outertype: SpikingNeuron

   :param noise: Noise object to apply to this neuron

setRadialInput
^^^^^^^^^^^^^^

.. java:method:: public void setRadialInput(float value)
   :outertype: SpikingNeuron

   **See also:** :java:ref:`ca.nengo.model.nef.NEFNode.setRadialInput(float)`

setScale
^^^^^^^^

.. java:method:: public void setScale(float scale)
   :outertype: SpikingNeuron

   :param scale: New scaling coefficient

toScript
^^^^^^^^

.. java:method:: public String toScript(HashMap<String, Object> scriptData) throws ScriptGenException
   :outertype: SpikingNeuron


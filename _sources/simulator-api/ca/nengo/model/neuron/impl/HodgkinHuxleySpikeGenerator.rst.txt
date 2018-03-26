.. java:import:: ca.nengo.dynamics.impl AbstractDynamicalSystem

.. java:import:: ca.nengo.dynamics.impl RK45Integrator

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl NodeFactory

.. java:import:: ca.nengo.model.neuron SpikeGenerator

HodgkinHuxleySpikeGenerator
===========================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public class HodgkinHuxleySpikeGenerator extends DynamicalSystemSpikeGenerator

   A SpikeGenerator based on the Hodgkin-Huxley model. TODO: unit test

   :author: Bryan Tripp

Constructors
------------
HodgkinHuxleySpikeGenerator
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public HodgkinHuxleySpikeGenerator()
   :outertype: HodgkinHuxleySpikeGenerator

   Makes the dynamic system


.. java:import:: ca.nengo.dynamics.impl AbstractDynamicalSystem

.. java:import:: ca.nengo.dynamics.impl RK45Integrator

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl NodeFactory

.. java:import:: ca.nengo.model.neuron SpikeGenerator

HodgkinHuxleySpikeGenerator.HodgkinHuxleySystem
===============================================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public static class HodgkinHuxleySystem extends AbstractDynamicalSystem
   :outertype: HodgkinHuxleySpikeGenerator

   Hodgkin-Huxley spiking dynamics.

   :author: Bryan Tripp

Constructors
------------
HodgkinHuxleySystem
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public HodgkinHuxleySystem(float[] state)
   :outertype: HodgkinHuxleySpikeGenerator.HodgkinHuxleySystem

   :param state: Initial state

HodgkinHuxleySystem
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public HodgkinHuxleySystem()
   :outertype: HodgkinHuxleySpikeGenerator.HodgkinHuxleySystem

   Set up the dynamical system

Methods
-------
f
^

.. java:method:: public float[] f(float t, float[] u)
   :outertype: HodgkinHuxleySpikeGenerator.HodgkinHuxleySystem

g
^

.. java:method:: public float[] g(float t, float[] u)
   :outertype: HodgkinHuxleySpikeGenerator.HodgkinHuxleySystem

   **See also:** :java:ref:`ca.nengo.dynamics.impl.AbstractDynamicalSystem.g(float,float[])`

getInputDimension
^^^^^^^^^^^^^^^^^

.. java:method:: public int getInputDimension()
   :outertype: HodgkinHuxleySpikeGenerator.HodgkinHuxleySystem

   **See also:** :java:ref:`ca.nengo.dynamics.impl.AbstractDynamicalSystem.getInputDimension()`

getOutputDimension
^^^^^^^^^^^^^^^^^^

.. java:method:: public int getOutputDimension()
   :outertype: HodgkinHuxleySpikeGenerator.HodgkinHuxleySystem

   **See also:** :java:ref:`ca.nengo.dynamics.impl.AbstractDynamicalSystem.getOutputDimension()`


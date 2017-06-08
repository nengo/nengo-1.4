.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.math PDFTools

.. java:import:: ca.nengo.math.impl IndicatorPDF

.. java:import:: ca.nengo.math.impl LinearFunction

.. java:import:: ca.nengo.math.impl PoissonPDF

.. java:import:: ca.nengo.math.impl SigmoidFunction

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl NodeFactory

.. java:import:: ca.nengo.model.impl RealOutputImpl

.. java:import:: ca.nengo.model.impl SpikeOutputImpl

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.model.neuron SpikeGenerator

.. java:import:: ca.nengo.model.neuron SynapticIntegrator

.. java:import:: ca.nengo.util MU

PoissonSpikeGenerator.LinearFactory
===================================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public static class LinearFactory implements SpikeGeneratorFactory
   :outertype: PoissonSpikeGenerator

   Creates PoissonSpikeGenerators with linear response functions.

   :author: Bryan Tripp

Fields
------
myIntercept
^^^^^^^^^^^

.. java:field::  PDF myIntercept
   :outertype: PoissonSpikeGenerator.LinearFactory

myMaxRate
^^^^^^^^^

.. java:field::  PDF myMaxRate
   :outertype: PoissonSpikeGenerator.LinearFactory

myRectified
^^^^^^^^^^^

.. java:field::  boolean myRectified
   :outertype: PoissonSpikeGenerator.LinearFactory

Constructors
------------
LinearFactory
^^^^^^^^^^^^^

.. java:constructor:: public LinearFactory()
   :outertype: PoissonSpikeGenerator.LinearFactory

   Set reasonable defaults

Methods
-------
getIntercept
^^^^^^^^^^^^

.. java:method:: public PDF getIntercept()
   :outertype: PoissonSpikeGenerator.LinearFactory

   :return: Input current at which firing rate is zero

getMaxRate
^^^^^^^^^^

.. java:method:: public PDF getMaxRate()
   :outertype: PoissonSpikeGenerator.LinearFactory

   :return: Firing rate of produced SpikeGenerators when input current is 1

getRectified
^^^^^^^^^^^^

.. java:method:: public boolean getRectified()
   :outertype: PoissonSpikeGenerator.LinearFactory

   :return: If true, response functions will be rectified (firing rates > 0)

make
^^^^

.. java:method:: public SpikeGenerator make()
   :outertype: PoissonSpikeGenerator.LinearFactory

   **See also:** :java:ref:`ca.nengo.model.neuron.impl.SpikeGeneratorFactory.make()`

setIntercept
^^^^^^^^^^^^

.. java:method:: public void setIntercept(PDF intercept)
   :outertype: PoissonSpikeGenerator.LinearFactory

   :param intercept: Input current at which firing rate is zero

setMaxRate
^^^^^^^^^^

.. java:method:: public void setMaxRate(PDF maxRate)
   :outertype: PoissonSpikeGenerator.LinearFactory

   :param maxRate: Firing rate of produced SpikeGenerators when input current is 1

setRectified
^^^^^^^^^^^^

.. java:method:: public void setRectified(boolean rectified)
   :outertype: PoissonSpikeGenerator.LinearFactory

   :param rectified: If true, response functions will be rectified (firing rates > 0)


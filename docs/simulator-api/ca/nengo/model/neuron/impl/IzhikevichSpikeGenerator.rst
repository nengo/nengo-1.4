.. java:import:: java.util Properties

.. java:import:: ca.nengo.math.impl InterpolatedFunction

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl RealOutputImpl

.. java:import:: ca.nengo.model.impl SpikeOutputImpl

.. java:import:: ca.nengo.model.neuron SpikeGenerator

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util TimeSeries1D

.. java:import:: ca.nengo.util.impl TimeSeries1DImpl

IzhikevichSpikeGenerator
========================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public class IzhikevichSpikeGenerator implements SpikeGenerator, Probeable

   From Izhikevich, 2003, the model is: v' = 0.04v*v + 5v + 140 - u + I  u' = a(bv - u)

   If v >= 30 mV, then v := c and u := u + d (reset after spike)

   v represents the membrane potential; u is a membrane recovery variable; a, b, c, and d are modifiable parameters

   :author: Hussein, Bryan

Fields
------
U
^

.. java:field:: public static final String U
   :outertype: IzhikevichSpikeGenerator

   Recovery state variable

V
^

.. java:field:: public static final String V
   :outertype: IzhikevichSpikeGenerator

   Voltage state variable

Constructors
------------
IzhikevichSpikeGenerator
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public IzhikevichSpikeGenerator()
   :outertype: IzhikevichSpikeGenerator

   Constructor using "default" parameters

IzhikevichSpikeGenerator
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public IzhikevichSpikeGenerator(Preset preset)
   :outertype: IzhikevichSpikeGenerator

   :param preset: A set of parameter values corresponding to a predefined cell type

IzhikevichSpikeGenerator
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public IzhikevichSpikeGenerator(float a, float b, float c, float d)
   :outertype: IzhikevichSpikeGenerator

   :param a: time scale of recovery variable
   :param b: sensitivity of recovery variable
   :param c: voltage reset value
   :param d: recovery variable reset change

IzhikevichSpikeGenerator
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public IzhikevichSpikeGenerator(float a, float b, float c, float d, float initialVoltage)
   :outertype: IzhikevichSpikeGenerator

   :param a: time scale of recovery variable
   :param b: sensitivity of recovery variable
   :param c: voltage reset value
   :param d: recovery variable reset change
   :param initialVoltage: initial voltage value (varying across neurons can prevent synchrony at start of simulation)

Methods
-------
clone
^^^^^

.. java:method:: @Override public SpikeGenerator clone() throws CloneNotSupportedException
   :outertype: IzhikevichSpikeGenerator

getA
^^^^

.. java:method:: public float getA()
   :outertype: IzhikevichSpikeGenerator

   :return: time scale of recovery variable

getB
^^^^

.. java:method:: public float getB()
   :outertype: IzhikevichSpikeGenerator

   :return: sensitivity of recovery variable

getC
^^^^

.. java:method:: public float getC()
   :outertype: IzhikevichSpikeGenerator

   :return: voltage reset value

getD
^^^^

.. java:method:: public float getD()
   :outertype: IzhikevichSpikeGenerator

   :return: recovery variable reset change

getHistory
^^^^^^^^^^

.. java:method:: public TimeSeries getHistory(String stateName) throws SimulationException
   :outertype: IzhikevichSpikeGenerator

   **See also:** :java:ref:`Probeable.getHistory(String)`

getMode
^^^^^^^

.. java:method:: public SimulationMode getMode()
   :outertype: IzhikevichSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.SimulationMode.ModeConfigurable.getMode()`

getPreset
^^^^^^^^^

.. java:method:: public Preset getPreset()
   :outertype: IzhikevichSpikeGenerator

   :return: An enumerated parameter value preset

getVoltage
^^^^^^^^^^

.. java:method:: public float getVoltage()
   :outertype: IzhikevichSpikeGenerator

   :return: membrane voltage

listStates
^^^^^^^^^^

.. java:method:: public Properties listStates()
   :outertype: IzhikevichSpikeGenerator

   **See also:** :java:ref:`Probeable.listStates()`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: IzhikevichSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: public InstantaneousOutput run(float[] time, float[] current)
   :outertype: IzhikevichSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.neuron.SpikeGenerator.run(float[],float[])`

setA
^^^^

.. java:method:: public void setA(float a)
   :outertype: IzhikevichSpikeGenerator

   :param a: time scale of recovery variable

setB
^^^^

.. java:method:: public void setB(float b)
   :outertype: IzhikevichSpikeGenerator

   :param b: sensitivity of recovery variable

setC
^^^^

.. java:method:: public void setC(float c)
   :outertype: IzhikevichSpikeGenerator

   :param c: voltage reset value

setD
^^^^

.. java:method:: public void setD(float d)
   :outertype: IzhikevichSpikeGenerator

   :param d: recovery variable reset change

setMode
^^^^^^^

.. java:method:: public void setMode(SimulationMode mode)
   :outertype: IzhikevichSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.SimulationMode.ModeConfigurable.setMode(ca.nengo.model.SimulationMode)`

setPreset
^^^^^^^^^

.. java:method:: public void setPreset(Preset preset)
   :outertype: IzhikevichSpikeGenerator

   :param preset: An enumerated parameter value preset


.. java:import:: ca.nengo.dynamics DynamicalSystem

.. java:import:: ca.nengo.dynamics Integrator

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.math.impl ConstantFunction

.. java:import:: ca.nengo.model Noise

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

NoiseFactory
============

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class NoiseFactory

   Default additive Noise implementations. TODO: unit tests

   :author: Bryan Tripp

Methods
-------
makeExplicitNoise
^^^^^^^^^^^^^^^^^

.. java:method:: public static Noise makeExplicitNoise(Function function)
   :outertype: NoiseFactory

   :param function: A function of time
   :return: Additive Noise where values are given explicit functions of time

makeNullNoise
^^^^^^^^^^^^^

.. java:method:: public static Noise makeNullNoise()
   :outertype: NoiseFactory

   :return: Zero additive Noise

makeRandomNoise
^^^^^^^^^^^^^^^

.. java:method:: public static Noise makeRandomNoise(float frequency, PDF pdf)
   :outertype: NoiseFactory

   :param frequency: Frequency (in simulation time) with which new noise values are drawn from the PDF
   :param pdf: PDF from which new noise values are drawn. The dimension must equal the input dimension of the dynamics.
   :return: a new NoiseImplPDF with the given parameters

makeRandomNoise
^^^^^^^^^^^^^^^

.. java:method:: public static Noise makeRandomNoise(float frequency, PDF pdf, DynamicalSystem dynamics, Integrator integrator)
   :outertype: NoiseFactory

   :param frequency: Frequency (in simulation time) with which new noise values are drawn from the PDF
   :param pdf: PDF from which new noise values are drawn. The dimension must equal the input dimension of the dynamics.
   :param dynamics: Dynamics through which raw noise values pass before they are combined with non-noise. The output dimension must equal the dimension of expected input to getValues().
   :param integrator: Integrator used to solve dynamics
   :return: a new NoiseImplPDF with the given parameters


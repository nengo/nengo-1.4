.. java:import:: ca.nengo.dynamics DynamicalSystem

.. java:import:: ca.nengo.dynamics Integrator

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.math.impl ConstantFunction

.. java:import:: ca.nengo.model Noise

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

NoiseFactory.NoiseImplPDF
=========================

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public static class NoiseImplPDF implements Noise
   :outertype: NoiseFactory

   Note: setters are private, because Origins typically make copies for each output dimension, which would then not be updated with changes to the original. So to change noise properties the Noise object must be replaced.

   :author: Bryan Tripp

Constructors
------------
NoiseImplPDF
^^^^^^^^^^^^

.. java:constructor:: public NoiseImplPDF(float frequency, PDF pdf, DynamicalSystem dynamics, Integrator integrator)
   :outertype: NoiseFactory.NoiseImplPDF

   :param frequency: Frequency (in simulation time) with which new noise values are drawn from the PDF
   :param pdf: PDF from which new noise values are drawn. The dimension of the space over which the PDF is defined must equal the input dimension of the dynamics.
   :param dynamics: Dynamics through which raw noise values pass before they are combined with non-noise. The input dimension must match the PDF and the output dimension must equal one. Can be null in which case the PDF must be one-dimensional.
   :param integrator: Integrator used to solve dynamics. Can be null if dynamics is null.

Methods
-------
clone
^^^^^

.. java:method:: @Override public Noise clone()
   :outertype: NoiseFactory.NoiseImplPDF

getDynamics
^^^^^^^^^^^

.. java:method:: public DynamicalSystem getDynamics()
   :outertype: NoiseFactory.NoiseImplPDF

   :return: Dynamics through which raw noise values pass before they are combined with non-noise. The input dimension must match the PDF and the output dimension must equal one. Can be null in which case the PDF must be one-dimensional.

getFrequency
^^^^^^^^^^^^

.. java:method:: public float getFrequency()
   :outertype: NoiseFactory.NoiseImplPDF

   :return: Frequency (in simulation time) with which new noise values are drawn from the PDF

getIntegrator
^^^^^^^^^^^^^

.. java:method:: public Integrator getIntegrator()
   :outertype: NoiseFactory.NoiseImplPDF

   :return: Integrator used to solve dynamics. Can be null if dynamics is null.

getPDF
^^^^^^

.. java:method:: public PDF getPDF()
   :outertype: NoiseFactory.NoiseImplPDF

   :return: PDF from which new noise values are drawn. The dimension of the space over which the PDF is defined must equal the input dimension of the dynamics.

getValue
^^^^^^^^

.. java:method:: public float getValue(float startTime, float endTime, float input)
   :outertype: NoiseFactory.NoiseImplPDF

   **See also:** :java:ref:`ca.nengo.model.Noise.getValue(float,float,float)`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: NoiseFactory.NoiseImplPDF

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

setFrequency
^^^^^^^^^^^^

.. java:method:: public void setFrequency(float frequency)
   :outertype: NoiseFactory.NoiseImplPDF

   :param frequency: Frequency (in simulation time) with which new noise values are drawn from the PDF


.. java:import:: Jama Matrix

.. java:import:: ca.nengo.math.impl GaussianPDF

.. java:import:: ca.nengo.model Resettable

.. java:import:: ca.nengo.model.nef ExpressModel

.. java:import:: ca.nengo.util MU

AdditiveGaussianExpressModel
============================

.. java:package:: ca.nengo.model.nef.impl
   :noindex:

.. java:type:: public abstract class AdditiveGaussianExpressModel implements ExpressModel, Resettable

   An ExpressModel that adds random noise and interpolated static distortion to DIRECT values as a model of spiking effects. Assumes Gaussian spike-related variability which is independent across decoded values. Autocorrelation over time is assumed to be zero by default but this can be set via setR(...). Note that noise is also filtered by PSC dynamics at the Termination. Autocorrelation is meant to model the unfiltered spectrum.

   :author: Bryan Tripp

Constructors
------------
AdditiveGaussianExpressModel
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public AdditiveGaussianExpressModel(int dim)
   :outertype: AdditiveGaussianExpressModel

   :param dim: Number of outputs of the DecodedOrigin

Methods
-------
getDistortion
^^^^^^^^^^^^^

.. java:method:: public abstract float[] getDistortion(float[] state, float[] directOutput)
   :outertype: AdditiveGaussianExpressModel

   :param state: The value represented by the associated NEFEnsemble
   :param directOutput: DIRECT mode output values of an Origin
   :return: Static distortion error to be added to each DIRECT output value

getNoise
^^^^^^^^

.. java:method:: public float[] getNoise(float[] state, float[] directOutput)
   :outertype: AdditiveGaussianExpressModel

   Note: Override this for alternative additive noise, e.g. correlated across outputs.

   :param state: The value represented by the associated NEFEnsemble
   :param directOutput: DIRECT mode output values of an Origin
   :return: Noise to be added to DIRECT mode values

getNoiseSD
^^^^^^^^^^

.. java:method:: public abstract float[] getNoiseSD(float[] state, float[] directOutput)
   :outertype: AdditiveGaussianExpressModel

   :param state: The value represented by the associated NEFEnsemble
   :param directOutput: DIRECT mode output values of an Origin
   :return: Standard deviation of noise to be added to each DIRECT output value

getOutput
^^^^^^^^^

.. java:method:: public float[] getOutput(float startTime, float[] state, float[] directOutput)
   :outertype: AdditiveGaussianExpressModel

   **See also:** :java:ref:`ca.nengo.model.nef.ExpressModel.getOutput(float,float[],float[])`

getR
^^^^

.. java:method:: public float[][] getR()
   :outertype: AdditiveGaussianExpressModel

   :return: Autocorrelation for each output

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: AdditiveGaussianExpressModel

setR
^^^^

.. java:method:: public void setR(float[][] R)
   :outertype: AdditiveGaussianExpressModel

   :param R: Autocorrelation for each input. The first element of each array is the 1-step autocorrelation; the kth is the k-step autocorrelation

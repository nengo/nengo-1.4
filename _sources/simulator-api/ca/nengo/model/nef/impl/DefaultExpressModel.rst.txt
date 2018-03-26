.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math.impl LinearCurveFitter

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.util MU

DefaultExpressModel
===================

.. java:package:: ca.nengo.model.nef.impl
   :noindex:

.. java:type:: public class DefaultExpressModel extends AdditiveGaussianExpressModel

   An ExpressModel that determines simplified noise and distortion models from simulations. Noise variance & autocorrelation are assumed constant per output and are determined from an example simulation. Distortion is interpolated from example simulations. For 1D ensembles, distortion is interpolated from samples in the encoded domain. For higher-dimensional ensembles, distortion is treated as a function of radial distance from zero, taken from samples in the first dimension.

   :author: Bryan Tripp

Constructors
------------
DefaultExpressModel
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public DefaultExpressModel(DecodedOrigin origin) throws SimulationException
   :outertype: DefaultExpressModel

   :param origin: The DecodedOrigin for which spike effects are to be modelled
   :throws SimulationException:

Methods
-------
getDistortion
^^^^^^^^^^^^^

.. java:method:: public float[] getDistortion(float[] state, float[] directValues)
   :outertype: DefaultExpressModel

   **See also:** :java:ref:`ca.nengo.model.nef.impl.AdditiveGaussianExpressModel.getDistortion(float[])`

getNoiseSD
^^^^^^^^^^

.. java:method:: public float[] getNoiseSD(float[] state, float[] directValues)
   :outertype: DefaultExpressModel

   **See also:** :java:ref:`ca.nengo.model.nef.impl.AdditiveGaussianExpressModel.getNoiseSD(float[])`

update
^^^^^^

.. java:method:: public void update() throws SimulationException
   :outertype: DefaultExpressModel


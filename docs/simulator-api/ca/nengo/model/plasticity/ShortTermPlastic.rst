.. java:import:: ca.nengo.dynamics DynamicalSystem

ShortTermPlastic
================

.. java:package:: ca.nengo.model.plasticity
   :noindex:

.. java:type:: public interface ShortTermPlastic

   Something (like a synapse) that is subject to short-term plasticity. This plasticity is modelled with user-defined dynamics.

   The dynamics are typically single-input-single-output, with firing rate as input and a synaptic weight scale factor (between 0 and 1) as the output. However, other interpretations are allowed, as defined by the implementing class (e.g. multiple inputs). Where possible, the implementing class should support the default interpretation in addition to its own alternative interpretations.

   :author: Bryan Tripp

Methods
-------
getSTPDynamics
^^^^^^^^^^^^^^

.. java:method:: public DynamicalSystem getSTPDynamics()
   :outertype: ShortTermPlastic

   :return: Dynamics of short-term plasticity

setSTPDynamics
^^^^^^^^^^^^^^

.. java:method:: public void setSTPDynamics(DynamicalSystem dynamics) throws IllegalArgumentException
   :outertype: ShortTermPlastic

   :param dynamics: New dynamics of short-term plasticity
   :throws IllegalArgumentException: if the number of inputs or outputs is not as expected by the implementing class (typically dynamics would be SISO)


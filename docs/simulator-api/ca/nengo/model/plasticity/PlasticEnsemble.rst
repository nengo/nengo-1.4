.. java:import:: ca.nengo.model Ensemble

PlasticEnsemble
===============

.. java:package:: ca.nengo.model.plasticity
   :noindex:

.. java:type:: public interface PlasticEnsemble extends Ensemble

   An extension of the default ensemble in which connection weights can be modified by a plasticity rule.

   :author: Trevor Bekolay

Methods
-------
getPlasticityInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public float getPlasticityInterval()
   :outertype: PlasticEnsemble

   :return: Period after which plasticity rules are evaluated (defaults to every time step).

setPlasticityInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setPlasticityInterval(float time)
   :outertype: PlasticEnsemble

   :param time: Period after which plasticity rules are evaluated (defaults to every time step).


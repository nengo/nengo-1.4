.. java:import:: ca.nengo.dynamics DynamicalSystem

.. java:import:: ca.nengo.dynamics.impl LTISystem

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

CriticallyDampedMuscle
======================

.. java:package:: ca.nengo.model.muscle.impl
   :noindex:

.. java:type:: public class CriticallyDampedMuscle extends SkeletalMuscleImpl

   A simple, phenomenological muscle model in which activation-force dynamics are modelled with a linear 2nd-order low-pass filter (see e.g. Winter, 1990, Biomechanics and Motor Control of Human Movement).

   This type of model is most viable in isometric conditions.

   :author: Bryan Tripp

Constructors
------------
CriticallyDampedMuscle
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CriticallyDampedMuscle(String name, float cutoff, float maxForce) throws StructuralException
   :outertype: CriticallyDampedMuscle

   :param name: Name of muscle
   :param cutoff: Cutoff frequency of filter model (Hz)
   :param maxForce: Cutoff force for muscle
   :throws StructuralException: if there's an issue making dynamics


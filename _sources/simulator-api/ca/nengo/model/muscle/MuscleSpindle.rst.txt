.. java:import:: ca.nengo.model Node

MuscleSpindle
=============

.. java:package:: ca.nengo.model.muscle
   :noindex:

.. java:type:: public interface MuscleSpindle extends Node

   A model of a muscle spindle receptor. A muscle spindle is embedded in a skeletal muscle, and has both efferent and afferent innervation. It receives excitatory drive from gamma motor neurons, which parallels the excitation of the surrounding muscle. It has two neural Origins which provide different information about stretch dynamics.

   :author: Bryan Tripp

Fields
------
DYNAMIC_ORIGIN_NAME
^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final String DYNAMIC_ORIGIN_NAME
   :outertype: MuscleSpindle

   Default name for the dynamic origin

STATIC_ORIGIN_NAME
^^^^^^^^^^^^^^^^^^

.. java:field:: public static final String STATIC_ORIGIN_NAME
   :outertype: MuscleSpindle

   Default name for the static origin

Methods
-------
getMuscle
^^^^^^^^^

.. java:method:: public SkeletalMuscle getMuscle()
   :outertype: MuscleSpindle

   :return: SkeletalMuscle parent that this spindle is part of


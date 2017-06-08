.. java:import:: ca.nengo.model Node

GolgiTendonOrgan
================

.. java:package:: ca.nengo.model.muscle
   :noindex:

.. java:type:: public interface GolgiTendonOrgan extends Node

   A model of a golgi tendon organ receptor. A GTO is embedded in the muscle-tendon junction. It has a neural Origin consisting of a Ib afferent neuron, the activity of which reflects muscle tension.

   :author: Bryan Tripp

Fields
------
ORIGIN_NAME
^^^^^^^^^^^

.. java:field:: public static final String ORIGIN_NAME
   :outertype: GolgiTendonOrgan

   Default origin name

Methods
-------
getMuscle
^^^^^^^^^

.. java:method:: public SkeletalMuscle getMuscle()
   :outertype: GolgiTendonOrgan

   :return: SkeletalMuscle object


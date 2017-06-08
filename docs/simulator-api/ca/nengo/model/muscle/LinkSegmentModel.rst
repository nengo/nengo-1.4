.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Probeable

LinkSegmentModel
================

.. java:package:: ca.nengo.model.muscle
   :noindex:

.. java:type:: public interface LinkSegmentModel extends Node, Probeable

   TODO: javadocs

   :author: Bryan Tripp

Methods
-------
getJointNames
^^^^^^^^^^^^^

.. java:method:: public String[] getJointNames()
   :outertype: LinkSegmentModel

   :return: Names of joints

getMuscles
^^^^^^^^^^

.. java:method:: public SkeletalMuscle[] getMuscles()
   :outertype: LinkSegmentModel

   :return: Array of SkeletalMuscles


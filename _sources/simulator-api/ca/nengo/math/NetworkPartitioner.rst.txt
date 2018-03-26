.. java:import:: java.util ArrayList

.. java:import:: java.util Set

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Projection

NetworkPartitioner
==================

.. java:package:: ca.nengo.math
   :noindex:

.. java:type:: public interface NetworkPartitioner

Methods
-------
getPartitions
^^^^^^^^^^^^^

.. java:method::  ArrayList<Set<Node>> getPartitions()
   :outertype: NetworkPartitioner

getPartitionsAsIntArray
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  int[] getPartitionsAsIntArray()
   :outertype: NetworkPartitioner

initialize
^^^^^^^^^^

.. java:method::  void initialize(Node[] nodes, Projection[] projections, int numPartitions)
   :outertype: NetworkPartitioner


.. java:import:: java.util ArrayList

.. java:import:: java.util Collections

.. java:import:: java.util Comparator

.. java:import:: java.util HashMap

.. java:import:: java.util HashSet

.. java:import:: java.util Iterator

.. java:import:: java.util LinkedList

.. java:import:: java.util List

.. java:import:: java.util ListIterator

.. java:import:: java.util Map

.. java:import:: java.util PriorityQueue

.. java:import:: java.util Set

.. java:import:: org.jgrapht Graph

.. java:import:: org.jgrapht WeightedGraph

.. java:import:: org.jgrapht.graph DefaultWeightedEdge

.. java:import:: org.jgrapht.graph SimpleWeightedGraph

.. java:import:: org.jgrapht.graph UndirectedWeightedSubgraph

.. java:import:: ca.nengo.math NetworkPartitioner

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model Projection

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model.impl NetworkImpl

.. java:import:: ca.nengo.model.impl NetworkImpl.OriginWrapper

.. java:import:: ca.nengo.model.impl NetworkImpl.TerminationWrapper

.. java:import:: ca.nengo.model.nef.impl NEFEnsembleImpl

MultiLevelKLNetworkPartitioner
==============================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class MultiLevelKLNetworkPartitioner implements NetworkPartitioner

   Employs the multi-level Kernighan-Lin graph partitioning heuristic to partition a network into a given number of partitions such that the amount of information passed along the projections that cross partitions is minimized, while making sure the number of neurons in each partition is relatively balanced.

   :author: e2crawfo

Methods
-------
getBalanceFactor
^^^^^^^^^^^^^^^^

.. java:method:: public double getBalanceFactor()
   :outertype: MultiLevelKLNetworkPartitioner

getPartitions
^^^^^^^^^^^^^

.. java:method:: public ArrayList<Set<Node>> getPartitions()
   :outertype: MultiLevelKLNetworkPartitioner

getPartitionsAsIntArray
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public int[] getPartitionsAsIntArray()
   :outertype: MultiLevelKLNetworkPartitioner

initialize
^^^^^^^^^^

.. java:method:: public void initialize(Node[] nodes, Projection[] projections, int numPartitions)
   :outertype: MultiLevelKLNetworkPartitioner

setBalanceFactor
^^^^^^^^^^^^^^^^

.. java:method:: public void setBalanceFactor(double myBalanceFactor)
   :outertype: MultiLevelKLNetworkPartitioner


.. java:import:: java.util ArrayList

.. java:import:: java.util Calendar

.. java:import:: java.util Enumeration

.. java:import:: java.util GregorianCalendar

.. java:import:: java.util HashSet

.. java:import:: java.util Hashtable

.. java:import:: javax.swing.tree DefaultMutableTreeNode

.. java:import:: javax.swing.tree DefaultTreeModel

.. java:import:: javax.swing.tree MutableTreeNode

.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model.impl AbstractEnsemble

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.util Probe

.. java:import:: ca.nengo.util SpikePattern

.. java:import:: ca.nengo.util TimeSeries

SimulatorDataModel
==================

.. java:package:: ca.nengo.ui.dataList
   :noindex:

.. java:type:: public class SimulatorDataModel extends DefaultTreeModel

   TODO

   :author: TODO

Constructors
------------
SimulatorDataModel
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public SimulatorDataModel()
   :outertype: SimulatorDataModel

   TODO

Methods
-------
captureData
^^^^^^^^^^^

.. java:method:: public SortableMutableTreeNode captureData(Network network)
   :outertype: SimulatorDataModel

   Captures the current data from a network and copies it to this simulator data tree

   :param network: TODO
   :return: TODO

parseEnsembleName
^^^^^^^^^^^^^^^^^

.. java:method::  ArrayList<String> parseEnsembleName(String name)
   :outertype: SimulatorDataModel


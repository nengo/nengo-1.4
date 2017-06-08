.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions ReversableAction

.. java:import:: ca.nengo.ui.lib.util.menus AbstractMenuBuilder

.. java:import:: ca.nengo.ui.models.icons EnsembleIcon

.. java:import:: ca.nengo.ui.models.nodes.widgets UISpikeProbe

.. java:import:: ca.nengo.ui.models.viewers EnsembleViewer

.. java:import:: ca.nengo.ui.models.viewers NodeViewer

UIEnsemble.StopCollectSpikes
============================

.. java:package:: ca.nengo.ui.models.nodes
   :noindex:

.. java:type::  class StopCollectSpikes extends ReversableAction
   :outertype: UIEnsemble

   Action to Stop Collecting Spikes

   :author: Shu Wu

Constructors
------------
StopCollectSpikes
^^^^^^^^^^^^^^^^^

.. java:constructor:: public StopCollectSpikes()
   :outertype: UIEnsemble.StopCollectSpikes

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: UIEnsemble.StopCollectSpikes

undo
^^^^

.. java:method:: @Override protected void undo() throws ActionException
   :outertype: UIEnsemble.StopCollectSpikes


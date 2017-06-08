.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions ReversableAction

.. java:import:: ca.nengo.ui.lib.util.menus AbstractMenuBuilder

.. java:import:: ca.nengo.ui.models.icons EnsembleIcon

.. java:import:: ca.nengo.ui.models.nodes.widgets UISpikeProbe

.. java:import:: ca.nengo.ui.models.viewers EnsembleViewer

.. java:import:: ca.nengo.ui.models.viewers NodeViewer

UIEnsemble.StartCollectSpikes
=============================

.. java:package:: ca.nengo.ui.models.nodes
   :noindex:

.. java:type::  class StartCollectSpikes extends ReversableAction
   :outertype: UIEnsemble

   Action to enable Spike Collection

   :author: Shu Wu

Constructors
------------
StartCollectSpikes
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public StartCollectSpikes()
   :outertype: UIEnsemble.StartCollectSpikes

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: UIEnsemble.StartCollectSpikes

undo
^^^^

.. java:method:: @Override protected void undo()
   :outertype: UIEnsemble.StartCollectSpikes


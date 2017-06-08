.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.ui.actions PlotSpikePattern

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.ui.models.nodes UIEnsemble

.. java:import:: ca.nengo.ui.models.tooltips TooltipBuilder

UISpikeProbe
============

.. java:package:: ca.nengo.ui.models.nodes.widgets
   :noindex:

.. java:type:: public class UISpikeProbe extends UIProbe

Constructors
------------
UISpikeProbe
^^^^^^^^^^^^

.. java:constructor:: public UISpikeProbe(UIEnsemble nodeAttachedTo)
   :outertype: UISpikeProbe

Methods
-------
constructMenu
^^^^^^^^^^^^^

.. java:method:: @Override protected void constructMenu(PopupMenuBuilder menu)
   :outertype: UISpikeProbe

constructTooltips
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void constructTooltips(TooltipBuilder tooltips)
   :outertype: UISpikeProbe

doubleClicked
^^^^^^^^^^^^^

.. java:method:: @Override public void doubleClicked()
   :outertype: UISpikeProbe

getModel
^^^^^^^^

.. java:method:: @Override public Ensemble getModel()
   :outertype: UISpikeProbe

getProbeParent
^^^^^^^^^^^^^^

.. java:method:: @Override public UIEnsemble getProbeParent()
   :outertype: UISpikeProbe

getTypeName
^^^^^^^^^^^

.. java:method:: @Override public String getTypeName()
   :outertype: UISpikeProbe

prepareToDestroyModel
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void prepareToDestroyModel()
   :outertype: UISpikeProbe


.. java:import:: java.io File

.. java:import:: java.io IOException

.. java:import:: java.util Collection

.. java:import:: javax.swing JOptionPane

.. java:import:: ca.nengo.io MatlabExporter

.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.ui.dataList ProbePlotHelper

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.objects.activities TrackedAction

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.lib.util.menus MenuBuilder

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.nodes UIEnsemble

.. java:import:: ca.nengo.ui.models.tooltips TooltipBuilder

.. java:import:: ca.nengo.ui.models.viewers EnsembleViewer

.. java:import:: ca.nengo.util Probe

UIStateProbe.ExportToMatlabAction
=================================

.. java:package:: ca.nengo.ui.models.nodes.widgets
   :noindex:

.. java:type::  class ExportToMatlabAction extends StandardAction
   :outertype: UIStateProbe

   Action for exporting to MatLab

   :author: Shu Wu

Fields
------
name
^^^^

.. java:field::  String name
   :outertype: UIStateProbe.ExportToMatlabAction

Constructors
------------
ExportToMatlabAction
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ExportToMatlabAction()
   :outertype: UIStateProbe.ExportToMatlabAction

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: UIStateProbe.ExportToMatlabAction


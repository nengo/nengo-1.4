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

UIStateProbe
============

.. java:package:: ca.nengo.ui.models.nodes.widgets
   :noindex:

.. java:type:: public class UIStateProbe extends UIProbe

   UI Wrapper for a Simulator Probe

   :author: Shu Wu

Constructors
------------
UIStateProbe
^^^^^^^^^^^^

.. java:constructor:: public UIStateProbe(UINeoNode nodeAttachedTo, Probe probeModel)
   :outertype: UIStateProbe

UIStateProbe
^^^^^^^^^^^^

.. java:constructor:: public UIStateProbe(UINeoNode nodeAttachedTo, String state) throws SimulationException
   :outertype: UIStateProbe

Methods
-------
constructMenu
^^^^^^^^^^^^^

.. java:method:: @Override protected void constructMenu(PopupMenuBuilder menu)
   :outertype: UIStateProbe

constructTooltips
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void constructTooltips(TooltipBuilder tooltips)
   :outertype: UIStateProbe

doubleClicked
^^^^^^^^^^^^^

.. java:method:: @Override public void doubleClicked()
   :outertype: UIStateProbe

exportToMatlab
^^^^^^^^^^^^^^

.. java:method:: public void exportToMatlab(String name)
   :outertype: UIStateProbe

   :param name: prefix of the fileName to be exported to
   :throws IOException:

getModel
^^^^^^^^

.. java:method:: @Override public Probe getModel()
   :outertype: UIStateProbe

getTypeName
^^^^^^^^^^^

.. java:method:: @Override public String getTypeName()
   :outertype: UIStateProbe

modelUpdated
^^^^^^^^^^^^

.. java:method:: @Override public void modelUpdated()
   :outertype: UIStateProbe

prepareToDestroyModel
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void prepareToDestroyModel()
   :outertype: UIStateProbe


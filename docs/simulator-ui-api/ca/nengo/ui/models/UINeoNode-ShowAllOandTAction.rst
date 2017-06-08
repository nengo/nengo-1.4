.. java:import:: java.awt.geom Point2D

.. java:import:: java.awt.geom Rectangle2D

.. java:import:: java.io File

.. java:import:: java.io IOException

.. java:import:: java.util ArrayList

.. java:import:: java.util Collection

.. java:import:: java.util HashSet

.. java:import:: java.util Iterator

.. java:import:: java.util LinkedList

.. java:import:: java.util Map.Entry

.. java:import:: java.util Properties

.. java:import:: java.util Vector

.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.io FileManager

.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model.impl FunctionInput

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef.impl DecodedOrigin

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.actions AddProbeAction

.. java:import:: ca.nengo.ui.actions CopyAction

.. java:import:: ca.nengo.ui.actions CreateModelAction

.. java:import:: ca.nengo.ui.actions CutAction

.. java:import:: ca.nengo.ui.actions DefaultModeAction

.. java:import:: ca.nengo.ui.actions DirectModeAction

.. java:import:: ca.nengo.ui.actions RateModeAction

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable UserDialogs

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.actions UserCancelledException

.. java:import:: ca.nengo.ui.lib.objects.activities TransientStatusMessage

.. java:import:: ca.nengo.ui.lib.objects.models ModelObject

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.util.menus AbstractMenuBuilder

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.ui.lib.world DroppableX

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldImpl

.. java:import:: ca.nengo.ui.models NodeContainer.ContainerException

.. java:import:: ca.nengo.ui.models.nodes UIEnsemble

.. java:import:: ca.nengo.ui.models.nodes UIFunctionInput

.. java:import:: ca.nengo.ui.models.nodes UIGenericNode

.. java:import:: ca.nengo.ui.models.nodes UINEFEnsemble

.. java:import:: ca.nengo.ui.models.nodes UINetwork

.. java:import:: ca.nengo.ui.models.nodes UINeuron

.. java:import:: ca.nengo.ui.models.nodes.widgets UIOrigin

.. java:import:: ca.nengo.ui.models.nodes.widgets UIProbe

.. java:import:: ca.nengo.ui.models.nodes.widgets UIStateProbe

.. java:import:: ca.nengo.ui.models.nodes.widgets UITermination

.. java:import:: ca.nengo.ui.models.nodes.widgets Widget

.. java:import:: ca.nengo.ui.models.tooltips TooltipBuilder

.. java:import:: ca.nengo.ui.models.viewers NetworkViewer

.. java:import:: ca.nengo.ui.models.viewers NodeViewer

.. java:import:: ca.nengo.util Probe

.. java:import:: ca.nengo.util VisiblyMutable

.. java:import:: ca.nengo.util VisiblyMutable.Event

UINeoNode.ShowAllOandTAction
============================

.. java:package:: ca.nengo.ui.models
   :noindex:

.. java:type::  class ShowAllOandTAction extends StandardAction
   :outertype: UINeoNode

   Action for showing all origins and terminations

   :author: Shu Wu

Constructors
------------
ShowAllOandTAction
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ShowAllOandTAction(String actionName)
   :outertype: UINeoNode.ShowAllOandTAction

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: UINeoNode.ShowAllOandTAction


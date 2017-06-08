.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef.impl DecodedOrigin

.. java:import:: ca.nengo.plot Plotter

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions ReversableAction

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.actions UserCancelledException

.. java:import:: ca.nengo.ui.lib.util.menus MenuBuilder

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.ui.models.constructors CDecodedOrigin

.. java:import:: ca.nengo.ui.models.constructors CDecodedTermination

.. java:import:: ca.nengo.ui.models.constructors ModelFactory

.. java:import:: ca.nengo.ui.models.nodes.widgets UIOrigin

.. java:import:: ca.nengo.ui.models.nodes.widgets UITermination

.. java:import:: ca.nengo.ui.models.tooltips TooltipBuilder

.. java:import:: ca.nengo.ui.models.viewers NodeViewer

UINEFEnsemble.PlotDecodedOriginMSE
==================================

.. java:package:: ca.nengo.ui.models.nodes
   :noindex:

.. java:type::  class PlotDecodedOriginMSE extends StandardAction
   :outertype: UINEFEnsemble

   Action for plotting the mean squared error for a decoded origin of an ensemble with multiple dimensions.

   :author: Steven Leigh

Fields
------
decodedOriginName
^^^^^^^^^^^^^^^^^

.. java:field::  String decodedOriginName
   :outertype: UINEFEnsemble.PlotDecodedOriginMSE

Constructors
------------
PlotDecodedOriginMSE
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PlotDecodedOriginMSE(String decodedOriginName)
   :outertype: UINEFEnsemble.PlotDecodedOriginMSE

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: UINEFEnsemble.PlotDecodedOriginMSE


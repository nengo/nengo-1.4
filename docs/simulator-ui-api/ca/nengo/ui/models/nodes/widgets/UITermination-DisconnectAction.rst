.. java:import:: java.awt Color

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model.nef.impl DecodedTermination

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.objects.lines ILineTermination

.. java:import:: ca.nengo.ui.lib.objects.lines LineConnector

.. java:import:: ca.nengo.ui.lib.objects.lines LineTerminationIcon

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.icons ModelIcon

.. java:import:: ca.nengo.ui.models.nodes UINetwork

.. java:import:: ca.nengo.ui.models.tooltips TooltipBuilder

UITermination.DisconnectAction
==============================

.. java:package:: ca.nengo.ui.models.nodes.widgets
   :noindex:

.. java:type::  class DisconnectAction extends StandardAction
   :outertype: UITermination

   Action for removing attached connection from the termination

   :author: Shu Wu

Constructors
------------
DisconnectAction
^^^^^^^^^^^^^^^^

.. java:constructor:: public DisconnectAction(String actionName)
   :outertype: UITermination.DisconnectAction

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: UITermination.DisconnectAction


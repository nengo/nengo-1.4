.. java:import:: java.util Map.Entry

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions ReversableAction

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.nodes.widgets UIProbe

AddProbeAction
==============

.. java:package:: ca.nengo.ui.actions
   :noindex:

.. java:type:: public class AddProbeAction extends ReversableAction

   Action for adding probes

   :author: Shu Wu

Constructors
------------
AddProbeAction
^^^^^^^^^^^^^^

.. java:constructor:: public AddProbeAction(UINeoNode nodeParent, Entry<String, String> state)
   :outertype: AddProbeAction

   TODO

   :param nodeParent: TODO
   :param state: TODO

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: AddProbeAction

undo
^^^^

.. java:method:: @Override protected void undo() throws ActionException
   :outertype: AddProbeAction


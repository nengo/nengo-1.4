.. java:import:: java.util Collection

.. java:import:: java.util HashMap

.. java:import:: javax.swing JOptionPane

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions ReversableAction

.. java:import:: ca.nengo.ui.lib.objects.models ModelObject

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.nodes.widgets UIProbe

AddProbesAction
===============

.. java:package:: ca.nengo.ui.actions
   :noindex:

.. java:type:: public class AddProbesAction extends ReversableAction

   Action for adding probes to a collection of nodes

   :author: Shu Wu

Constructors
------------
AddProbesAction
^^^^^^^^^^^^^^^

.. java:constructor:: public AddProbesAction(Collection<ModelObject> nodes)
   :outertype: AddProbesAction

   TODO

   :param nodes: TODO

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: AddProbesAction

undo
^^^^

.. java:method:: @Override protected void undo() throws ActionException
   :outertype: AddProbesAction


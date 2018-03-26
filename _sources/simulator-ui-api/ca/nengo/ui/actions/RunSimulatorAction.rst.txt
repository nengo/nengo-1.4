.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.sim Simulator

.. java:import:: ca.nengo.sim SimulatorEvent

.. java:import:: ca.nengo.sim SimulatorListener

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable ConfigSchemaImpl

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable.descriptors PBoolean

.. java:import:: ca.nengo.ui.configurable.descriptors PFloat

.. java:import:: ca.nengo.ui.configurable.managers ConfigManager

.. java:import:: ca.nengo.ui.configurable.managers ConfigManager.ConfigMode

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.objects.activities TrackedAction

.. java:import:: ca.nengo.ui.lib.objects.activities TrackedStatusMsg

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.models.nodes UINetwork

RunSimulatorAction
==================

.. java:package:: ca.nengo.ui.actions
   :noindex:

.. java:type:: public class RunSimulatorAction extends StandardAction

   Runs the Simulator

   :author: Shu Wu

Constructors
------------
RunSimulatorAction
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public RunSimulatorAction(String actionName, UINetwork uiNetwork)
   :outertype: RunSimulatorAction

   :param actionName: Name of this action
   :param uiNetwork: Simulator to run

RunSimulatorAction
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public RunSimulatorAction(String actionName, UINetwork uiNetwork, float startTime, float endTime, float stepTime)
   :outertype: RunSimulatorAction

   :param actionName: TODO
   :param uiNetwork: TODO
   :param startTime: TODO
   :param endTime: TODO
   :param stepTime: TODO

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: RunSimulatorAction


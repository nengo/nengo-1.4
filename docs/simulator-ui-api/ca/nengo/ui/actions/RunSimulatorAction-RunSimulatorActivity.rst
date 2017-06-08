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

RunSimulatorAction.RunSimulatorActivity
=======================================

.. java:package:: ca.nengo.ui.actions
   :noindex:

.. java:type::  class RunSimulatorActivity extends TrackedAction implements SimulatorListener
   :outertype: RunSimulatorAction

   Activity which will run the simulation

   :author: Shu Wu

Constructors
------------
RunSimulatorActivity
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public RunSimulatorActivity(float startTime, float endTime, float stepTime, boolean showDataViewer)
   :outertype: RunSimulatorAction.RunSimulatorActivity

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: RunSimulatorAction.RunSimulatorActivity

processEvent
^^^^^^^^^^^^

.. java:method:: public void processEvent(SimulatorEvent event)
   :outertype: RunSimulatorAction.RunSimulatorActivity


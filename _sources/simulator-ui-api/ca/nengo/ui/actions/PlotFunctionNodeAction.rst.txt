.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.model.impl FunctionInput

.. java:import:: ca.nengo.plot Plotter

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable.descriptors PFloat

.. java:import:: ca.nengo.ui.configurable.descriptors PInt

.. java:import:: ca.nengo.ui.configurable.managers ConfigManager

.. java:import:: ca.nengo.ui.configurable.managers ConfigManager.ConfigMode

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

PlotFunctionNodeAction
======================

.. java:package:: ca.nengo.ui.actions
   :noindex:

.. java:type:: public class PlotFunctionNodeAction extends StandardAction

   TODO

   :author: TODO

Fields
------
pEnd
^^^^

.. java:field:: static final Property pEnd
   :outertype: PlotFunctionNodeAction

pIncrement
^^^^^^^^^^

.. java:field:: static final Property pIncrement
   :outertype: PlotFunctionNodeAction

pStart
^^^^^^

.. java:field:: static final Property pStart
   :outertype: PlotFunctionNodeAction

Constructors
------------
PlotFunctionNodeAction
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PlotFunctionNodeAction(String plotName, String actionName, FunctionInput functionInput)
   :outertype: PlotFunctionNodeAction

   :param plotName: TODO
   :param actionName: TODO
   :param functionInput: TODO

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: PlotFunctionNodeAction

completeConfiguration
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void completeConfiguration(ConfigResult properties) throws ConfigException
   :outertype: PlotFunctionNodeAction

   :param properties: TODO
   :throws ConfigException: TODO


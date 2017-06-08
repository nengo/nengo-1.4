.. java:import:: javax.swing JDialog

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable.descriptors PFloat

.. java:import:: ca.nengo.ui.configurable.managers ConfigManager

.. java:import:: ca.nengo.ui.configurable.managers ConfigManager.ConfigMode

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.util DialogPlotter

PlotFunctionAction
==================

.. java:package:: ca.nengo.ui.actions
   :noindex:

.. java:type:: public class PlotFunctionAction extends StandardAction

   Plots a function node, which can contain multiple functions

   :author: Shu Wu

Fields
------
pEnd
^^^^

.. java:field:: static final Property pEnd
   :outertype: PlotFunctionAction

pIncrement
^^^^^^^^^^

.. java:field:: static final Property pIncrement
   :outertype: PlotFunctionAction

pStart
^^^^^^

.. java:field:: static final Property pStart
   :outertype: PlotFunctionAction

propD
^^^^^

.. java:field:: static final Property[] propD
   :outertype: PlotFunctionAction

Constructors
------------
PlotFunctionAction
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PlotFunctionAction(String plotName, Function function, JDialog dialogParent)
   :outertype: PlotFunctionAction

   :param plotName: TODO
   :param function: TODO
   :param dialogParent: TODO

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: PlotFunctionAction


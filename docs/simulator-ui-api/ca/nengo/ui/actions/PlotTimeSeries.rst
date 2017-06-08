.. java:import:: ca.nengo.plot Plotter

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable.descriptors PFloat

.. java:import:: ca.nengo.ui.configurable.descriptors PInt

.. java:import:: ca.nengo.ui.configurable.managers ConfigManager.ConfigMode

.. java:import:: ca.nengo.ui.configurable.managers UserConfigurer

.. java:import:: ca.nengo.ui.dataList ProbePlotHelper

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.util DataUtils

.. java:import:: ca.nengo.util TimeSeries

PlotTimeSeries
==============

.. java:package:: ca.nengo.ui.actions
   :noindex:

.. java:type:: public class PlotTimeSeries extends StandardAction

   Action for Plotting with additional options

   :author: Shu Wu

Constructors
------------
PlotTimeSeries
^^^^^^^^^^^^^^

.. java:constructor:: public PlotTimeSeries(String actionName, TimeSeries timeSeries, String plotName, boolean showUserConfigDialog, float defaultTau, int defaultSubSampling)
   :outertype: PlotTimeSeries

   :param actionName: TODO
   :param timeSeries: TODO
   :param plotName: TODO
   :param showUserConfigDialog: TODO
   :param defaultTau: TODO
   :param defaultSubSampling: TODO

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: PlotTimeSeries


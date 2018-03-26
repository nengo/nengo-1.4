.. java:import:: java.util ArrayList

.. java:import:: java.util Collection

.. java:import:: java.util List

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.ui.actions PlotTimeSeries

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.util NengoConfigManager

.. java:import:: ca.nengo.ui.util NengoConfigManager.UserProperties

.. java:import:: ca.nengo.util Probe

.. java:import:: ca.nengo.util TimeSeries

ProbePlotHelper
===============

.. java:package:: ca.nengo.ui.dataList
   :noindex:

.. java:type:: public class ProbePlotHelper

   Helps plot probes

   :author: Shu Wu

Fields
------
DEFAULT_PLOTTER_TAU_FILTER
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public final float DEFAULT_PLOTTER_TAU_FILTER
   :outertype: ProbePlotHelper

   TODO

DEFAULT_SUB_SAMPLING
^^^^^^^^^^^^^^^^^^^^

.. java:field:: public final int DEFAULT_SUB_SAMPLING
   :outertype: ProbePlotHelper

   TODO

Methods
-------
getDefaultAction
^^^^^^^^^^^^^^^^

.. java:method:: public StandardAction getDefaultAction(Probe probe, String plotName)
   :outertype: ProbePlotHelper

   :param probe:
   :param plotName:
   :return: The default plotting action

getDefaultAction
^^^^^^^^^^^^^^^^

.. java:method:: public StandardAction getDefaultAction(TimeSeries data, String plotName, boolean applyFilterByDefault)
   :outertype: ProbePlotHelper

   :param applyFilterByDefault:
   :param data:
   :param plotName:
   :return: The default plotting action

getDefaultSubSampling
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public int getDefaultSubSampling()
   :outertype: ProbePlotHelper

   :return: TODO

getDefaultTauFilter
^^^^^^^^^^^^^^^^^^^

.. java:method:: public float getDefaultTauFilter()
   :outertype: ProbePlotHelper

   :return: TODO

getInstance
^^^^^^^^^^^

.. java:method:: public static ProbePlotHelper getInstance()
   :outertype: ProbePlotHelper

   :return: TODO

getPlotActions
^^^^^^^^^^^^^^

.. java:method:: public Collection<StandardAction> getPlotActions(TimeSeries data, String plotName)
   :outertype: ProbePlotHelper

   :param data: TODO
   :param plotName: TODO
   :return: TODO

isApplyTauFilterByDefault
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isApplyTauFilterByDefault(Probe probe)
   :outertype: ProbePlotHelper

   :param probe: Probe
   :return: Whether to apply tau filters in timeseries plots for that probe

setDefaultSubSampling
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setDefaultSubSampling(int value)
   :outertype: ProbePlotHelper

   :param value: TODO

setDefaultTauFilter
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setDefaultTauFilter(float value)
   :outertype: ProbePlotHelper

   :param value: TODO


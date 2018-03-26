.. java:import:: java.awt BasicStroke

.. java:import:: java.awt BorderLayout

.. java:import:: java.awt Color

.. java:import:: java.util List

.. java:import:: javax.swing JFrame

.. java:import:: javax.swing JPanel

.. java:import:: org.jfree.chart ChartColor

.. java:import:: org.jfree.chart ChartFactory

.. java:import:: org.jfree.chart ChartPanel

.. java:import:: org.jfree.chart JFreeChart

.. java:import:: org.jfree.chart LegendItem

.. java:import:: org.jfree.chart LegendItemCollection

.. java:import:: org.jfree.chart.axis AxisLocation

.. java:import:: org.jfree.chart.axis NumberAxis

.. java:import:: org.jfree.chart.plot PlotOrientation

.. java:import:: org.jfree.chart.plot XYPlot

.. java:import:: org.jfree.chart.renderer.xy XYItemRenderer

.. java:import:: org.jfree.chart.renderer.xy XYLineAndShapeRenderer

.. java:import:: org.jfree.data.xy XYSeries

.. java:import:: org.jfree.data.xy XYSeriesCollection

.. java:import:: org.jfree.util ShapeUtilities

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.model Noise

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model RealOutput

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef NEFNode

.. java:import:: ca.nengo.model.nef.impl DecodedOrigin

.. java:import:: ca.nengo.model.nef.impl NEFEnsembleImpl

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.model.neuron.impl SpikingNeuron

.. java:import:: ca.nengo.plot Plotter

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util SpikePattern

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util TimeSeries1D

DefaultPlotter
==============

.. java:package:: ca.nengo.plot.impl
   :noindex:

.. java:type:: public class DefaultPlotter extends Plotter

   Default Plotter implementation.

   :author: Bryan Tripp

Methods
-------
doPlot
^^^^^^

.. java:method:: public void doPlot(TimeSeries series, String title)
   :outertype: DefaultPlotter

   **See also:** :java:ref:`ca.nengo.plot.Plotter.doPlot(ca.nengo.util.TimeSeries,java.lang.String)`

doPlot
^^^^^^

.. java:method:: public void doPlot(TimeSeries ideal, TimeSeries actual, String title)
   :outertype: DefaultPlotter

   **See also:** :java:ref:`ca.nengo.plot.Plotter.doPlot(ca.nengo.util.TimeSeries,ca.nengo.util.TimeSeries,java.lang.String)`

doPlot
^^^^^^

.. java:method:: public void doPlot(List<TimeSeries> series, List<SpikePattern> patterns, String title)
   :outertype: DefaultPlotter

   **See also:** :java:ref:`ca.nengo.plot.Plotter.doPlot(java.util.List,java.util.List,java.lang.String)`

doPlot
^^^^^^

.. java:method:: public void doPlot(NEFEnsemble ensemble, String name)
   :outertype: DefaultPlotter

   **See also:** :java:ref:`ca.nengo.plot.Plotter.doPlot(ca.nengo.model.nef.NEFEnsemble,java.lang.String)`

doPlot
^^^^^^

.. java:method:: public void doPlot(NEFEnsemble ensemble)
   :outertype: DefaultPlotter

   **See also:** :java:ref:`ca.nengo.plot.Plotter.doPlot(ca.nengo.model.nef.NEFEnsemble)`

doPlot
^^^^^^

.. java:method:: public void doPlot(SpikePattern pattern)
   :outertype: DefaultPlotter

   **See also:** :java:ref:`ca.nengo.plot.Plotter.doPlot(ca.nengo.util.SpikePattern)`

doPlot
^^^^^^

.. java:method:: public void doPlot(Function function, float start, float increment, float end, String title)
   :outertype: DefaultPlotter

   **See also:** :java:ref:`ca.nengo.plot.Plotter.doPlot(ca.nengo.math.Function,float,float,float,String)`

doPlot
^^^^^^

.. java:method:: public void doPlot(float[] vector, String title)
   :outertype: DefaultPlotter

   **See also:** :java:ref:`ca.nengo.plot.Plotter.doPlot(float[],String)`

doPlot
^^^^^^

.. java:method:: public void doPlot(float[] domain, float[] vector, String title)
   :outertype: DefaultPlotter

   **See also:** :java:ref:`ca.nengo.plot.Plotter.doPlot(float[],float[],java.lang.String)`

doPlot
^^^^^^

.. java:method:: public void doPlot(float[] domain, float[][] matrix, String title)
   :outertype: DefaultPlotter

   Accepts a matrix as the second argument, and plots each row of the matrix separately as in doPlot(float[], float[], java.lang.String).

doPlotMSE
^^^^^^^^^

.. java:method:: public void doPlotMSE(NEFEnsemble ensemble, DecodedOrigin origin, String name)
   :outertype: DefaultPlotter

getBarChart
^^^^^^^^^^^

.. java:method:: public ChartPanel getBarChart(float[] vector, String title)
   :outertype: DefaultPlotter

showChart
^^^^^^^^^

.. java:method:: protected void showChart(JFreeChart chart, String title)
   :outertype: DefaultPlotter


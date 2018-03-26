.. java:import:: java.awt BorderLayout

.. java:import:: java.awt Frame

.. java:import:: java.awt Image

.. java:import:: java.awt.event WindowAdapter

.. java:import:: java.awt.event WindowEvent

.. java:import:: java.io IOException

.. java:import:: java.util ArrayList

.. java:import:: java.util Iterator

.. java:import:: java.util List

.. java:import:: javax.imageio ImageIO

.. java:import:: javax.swing JFrame

.. java:import:: javax.swing JPanel

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.plot.impl DefaultPlotter

.. java:import:: ca.nengo.util DataUtils

.. java:import:: ca.nengo.util Environment

.. java:import:: ca.nengo.util SpikePattern

.. java:import:: ca.nengo.util TimeSeries

Plotter
=======

.. java:package:: ca.nengo.plot
   :noindex:

.. java:type:: public abstract class Plotter

   Factory for frequently-used plots.

   :author: Bryan Tripp

Constructors
------------
Plotter
^^^^^^^

.. java:constructor:: public Plotter()
   :outertype: Plotter

Methods
-------
closeAll
^^^^^^^^

.. java:method:: public static void closeAll()
   :outertype: Plotter

   Close all open plots

createFrame
^^^^^^^^^^^

.. java:method:: public JFrame createFrame()
   :outertype: Plotter

   :return: A new JFrame to hold a plot

doPlot
^^^^^^

.. java:method:: public abstract void doPlot(TimeSeries series, String title)
   :outertype: Plotter

   :param series: TimeSeries to plot
   :param title: Plot title

doPlot
^^^^^^

.. java:method:: public abstract void doPlot(TimeSeries ideal, TimeSeries actual, String title)
   :outertype: Plotter

   :param ideal: Ideal time series
   :param actual: Actual time series
   :param title: Plot title

doPlot
^^^^^^

.. java:method:: public abstract void doPlot(List<TimeSeries> series, List<SpikePattern> patterns, String title)
   :outertype: Plotter

   :param series: A list of TimeSeries to plot (can be null if none)
   :param patterns: A list of SpikePatterns to plot (can be null if none)
   :param title: Plot title

doPlot
^^^^^^

.. java:method:: public abstract void doPlot(NEFEnsemble ensemble, String origin)
   :outertype: Plotter

   :param ensemble: NEFEnsemble from which origin arises
   :param origin: Name of origin (must be a DecodedOrigin, not one derived from a combination of neuron origins)

doPlot
^^^^^^

.. java:method:: public abstract void doPlot(NEFEnsemble ensemble)
   :outertype: Plotter

   :param ensemble: An NEFEnsemble

doPlot
^^^^^^

.. java:method:: public abstract void doPlot(SpikePattern pattern)
   :outertype: Plotter

   :param pattern: A SpikePattern for which to plot a raster

doPlot
^^^^^^

.. java:method:: public abstract void doPlot(Function function, float start, float increment, float end, String title)
   :outertype: Plotter

   :param function: Function to plot
   :param start: Minimum of input range
   :param increment: Size of incrememnt along input range
   :param end: Maximum of input range
   :param title: Display title of plot

doPlot
^^^^^^

.. java:method:: public abstract void doPlot(float[] vector, String title)
   :outertype: Plotter

   :param vector: Vector of points to plot
   :param title: Display title of plot

doPlot
^^^^^^

.. java:method:: public abstract void doPlot(float[] domain, float[] vector, String title)
   :outertype: Plotter

   :param domain: Vector of domain values
   :param vector: Vector of range values
   :param title: Display title of plot

filter
^^^^^^

.. java:method:: public static TimeSeries filter(TimeSeries series, float tauFilter)
   :outertype: Plotter

   :param series: A TimeSeries to which to apply a 1-D linear filter
   :param tauFilter: Filter time constant
   :return: Filtered TimeSeries

plot
^^^^

.. java:method:: public static void plot(TimeSeries series, String title)
   :outertype: Plotter

   Static convenience method for producing a TimeSeries plot.

   :param series: TimeSeries to plot
   :param title: Plot title

plot
^^^^

.. java:method:: public static void plot(TimeSeries series, float tauFilter, String title)
   :outertype: Plotter

   As plot(TimeSeries) but series is filtered before plotting. This is useful when plotting NEFEnsemble output (which may consist of spikes) in a manner more similar to the way it would appear within post-synaptic neurons.

   :param series: TimeSeries to plot
   :param tauFilter: Time constant of display filter (s)
   :param title: Plot title

plot
^^^^

.. java:method:: public static void plot(TimeSeries ideal, TimeSeries actual, String title)
   :outertype: Plotter

   Plots ideal and actual TimeSeries' together.

   :param ideal: Ideal time series
   :param actual: Actual time series
   :param title: Plot title

plot
^^^^

.. java:method:: public static void plot(List<TimeSeries> series, List<SpikePattern> patterns, String title)
   :outertype: Plotter

   Plots multiple TimeSeries and/or SpikePatterns together in the same plot.

   :param series: A list of TimeSeries to plot (can be null if none)
   :param patterns: A list of SpikePatterns to plot (can be null if none)
   :param title: Plot title

plot
^^^^

.. java:method:: public static void plot(TimeSeries ideal, TimeSeries actual, float tauFilter, String title)
   :outertype: Plotter

   Plots ideal and actual TimeSeries' together, with each series filtered before plotting.

   :param ideal: Ideal time series
   :param actual: Actual time series
   :param tauFilter: Time constant of display filter (s)
   :param title: Plot title

plot
^^^^

.. java:method:: public static void plot(NEFEnsemble ensemble, String origin)
   :outertype: Plotter

   Static convenience method for producing a decoding error plot of an NEFEnsemble origin.

   :param ensemble: NEFEnsemble from which origin arises
   :param origin: Name of origin (must be a DecodedOrigin, not one derived from a combination of neuron origins)

plot
^^^^

.. java:method:: public static void plot(NEFEnsemble ensemble)
   :outertype: Plotter

   Static convenience method for producing a plot of CONSTANT_RATE responses over range of inputs.

   :param ensemble: An NEFEnsemble

plot
^^^^

.. java:method:: public static void plot(SpikePattern pattern)
   :outertype: Plotter

   Static convenience method for plotting a spike raster.

   :param pattern: SpikePattern to plot

plot
^^^^

.. java:method:: public static void plot(Function function, float start, float increment, float end, String title)
   :outertype: Plotter

   Static convenience method for plotting a Function.

   :param function: Function to plot
   :param start: Minimum of input range
   :param increment: Size of incrememnt along input range
   :param end: Maximum of input range
   :param title: Display title of plot

plot
^^^^

.. java:method:: public static void plot(float[] vector, String title)
   :outertype: Plotter

   Static convenience method for plotting a vector.

   :param vector: Vector of points to plot
   :param title: Display title of plot

plot
^^^^

.. java:method:: public static void plot(float[] domain, float[] vector, String title)
   :outertype: Plotter

   Static convenience method for plotting a vector.

   :param domain: Vector of domain values
   :param vector: Vector of range values
   :param title: Display title of plot

showPlot
^^^^^^^^

.. java:method:: public void showPlot(JPanel plotPanel, String title)
   :outertype: Plotter

   Display a new plot.

   :param plotPanel: A panel containng the plot image
   :param title: The plot title


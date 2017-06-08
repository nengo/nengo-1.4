.. java:import:: java.awt BorderLayout

.. java:import:: javax.swing JDialog

.. java:import:: javax.swing JPanel

.. java:import:: org.jfree.chart ChartPanel

.. java:import:: org.jfree.chart JFreeChart

.. java:import:: ca.nengo.plot.impl DefaultPlotter

DialogPlotter
=============

.. java:package:: ca.nengo.ui.util
   :noindex:

.. java:type:: public class DialogPlotter extends DefaultPlotter

   Plotter uses dialog rather than frames to support parent-child relationship with NeoGraphics components.

   :author: Shu Wu

Constructors
------------
DialogPlotter
^^^^^^^^^^^^^

.. java:constructor:: public DialogPlotter(JDialog parentPanel)
   :outertype: DialogPlotter

Methods
-------
showChart
^^^^^^^^^

.. java:method:: @Override protected void showChart(JFreeChart chart, String title)
   :outertype: DialogPlotter


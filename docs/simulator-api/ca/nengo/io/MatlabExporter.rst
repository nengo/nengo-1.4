.. java:import:: java.io File

.. java:import:: java.io IOException

.. java:import:: java.util HashMap

.. java:import:: java.util Map

.. java:import:: ca.nengo.plot Plotter

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util SpikePattern

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: com.jmatio.io MatFileWriter

.. java:import:: com.jmatio.types MLArray

.. java:import:: com.jmatio.types MLDouble

MatlabExporter
==============

.. java:package:: ca.nengo.io
   :noindex:

.. java:type:: public class MatlabExporter

   A tool for exporting data to Matlab .mat files. Use like this:

   MatlabExport me = new MatlabExport();
   me.add("series1", series1);
   ...
   me.add("series1", series1);
   me.write(new File("c:\\foo.mat"));

   :author: Bryan Tripp

Constructors
------------
MatlabExporter
^^^^^^^^^^^^^^

.. java:constructor:: public MatlabExporter()
   :outertype: MatlabExporter

   Export data to Matlab .mat files

Methods
-------
add
^^^

.. java:method:: public void add(String name, TimeSeries data)
   :outertype: MatlabExporter

   :param name: Matlab variable name
   :param data: Data to be stored in Matlab variable

add
^^^

.. java:method:: public void add(String name, TimeSeries data, float tau)
   :outertype: MatlabExporter

   Filters TimeSeries data with given time constant (this is usually a good idea for spike output, which is a sum of impulses). TODO: this filter is prohibitively slow for large datasets

   :param name: Matlab variable name
   :param data: Data to be stored in Matlab variable
   :param tau: Time constant of filter to apply to data

add
^^^

.. java:method:: public void add(String name, SpikePattern pattern)
   :outertype: MatlabExporter

   :param name: Matlab variable name
   :param pattern: Spike times for a group of neurons

add
^^^

.. java:method:: public void add(String name, float[][] data)
   :outertype: MatlabExporter

   :param name: Matlab variable name
   :param data: A matrix

makeVariableNameValid
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static String makeVariableNameValid(String name)
   :outertype: MatlabExporter

   :param name: original possibly invalid name
   :return: valid name that Matlab can use

removeAll
^^^^^^^^^

.. java:method:: public void removeAll()
   :outertype: MatlabExporter

   Clears all variables

write
^^^^^

.. java:method:: public void write(File destination) throws IOException
   :outertype: MatlabExporter

   Writes to given destination the data that have been added to this exporter.

   :param destination: File to which data are to be written (should have extension .mat)
   :throws IOException: if there's a problem writing to disk


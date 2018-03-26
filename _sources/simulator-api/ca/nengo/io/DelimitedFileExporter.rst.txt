.. java:import:: java.io BufferedReader

.. java:import:: java.io BufferedWriter

.. java:import:: java.io File

.. java:import:: java.io FileReader

.. java:import:: java.io FileWriter

.. java:import:: java.io IOException

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util StringTokenizer

.. java:import:: ca.nengo.plot Plotter

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util SpikePattern

.. java:import:: ca.nengo.util TimeSeries

DelimitedFileExporter
=====================

.. java:package:: ca.nengo.io
   :noindex:

.. java:type:: public class DelimitedFileExporter

   Exports TimeSeries, SpikePattern, and float[][] data to delimited text files.

   :author: Bryan Tripp

Constructors
------------
DelimitedFileExporter
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public DelimitedFileExporter()
   :outertype: DelimitedFileExporter

   Uses default column delimiter ", " and row delimiter "\r\n".

DelimitedFileExporter
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public DelimitedFileExporter(String columnDelim, String rowDelim)
   :outertype: DelimitedFileExporter

   :param columnDelim: String used to delimit items within a matrix row
   :param rowDelim: String used to delimit rows of a matrix

Methods
-------
export
^^^^^^

.. java:method:: public void export(TimeSeries series, File file) throws IOException
   :outertype: DelimitedFileExporter

   Exports a TimeSeries with times in the first column and data from each dimension in subsequent columns.

   :param series: TimeSeries to export
   :param file: File to which to export the TimeSeries
   :throws IOException: if there's a problem writing to disk

export
^^^^^^

.. java:method:: public void export(TimeSeries series, File file, float tau) throws IOException
   :outertype: DelimitedFileExporter

   Exports a TimeSeries as a matrix with times in the first column and data from each dimension in subsequent rows.

   :param series: TimeSeries to export
   :param file: File to which to export the TimeSeries
   :param tau: Time constant with which to filter data
   :throws IOException: if there's a problem writing to disk

export
^^^^^^

.. java:method:: public void export(SpikePattern pattern, File file) throws IOException
   :outertype: DelimitedFileExporter

   Exports a SpikePattern as a matrix with spikes times of each neuron in a different row.

   :param pattern: SpikePattern to export
   :param file: File to which to export the SpikePattern
   :throws IOException: if there's a problem writing to disk

export
^^^^^^

.. java:method:: public void export(float[][] matrix, File file) throws IOException
   :outertype: DelimitedFileExporter

   Exports a matrix with rows and columns delimited as specified in the constructor.

   :param matrix: The matrix to export
   :param file: File to which to export the matrix
   :throws IOException: if there's a problem writing to disk

importAsMatrix
^^^^^^^^^^^^^^

.. java:method:: public float[][] importAsMatrix(File file) throws IOException
   :outertype: DelimitedFileExporter

   Imports a delimited file as a matrix. Assumes that rows are delimited as lines, and items in a row are delimited with one or more of the following: comma, colon, semicolon, space, tab.

   :param file: File from which to load matrix
   :throws IOException: if there's a problem writing to disk
   :return: Matrix from file


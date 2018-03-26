.. java:import:: java.io File

.. java:import:: java.io FileInputStream

.. java:import:: java.io FileOutputStream

.. java:import:: java.io IOException

.. java:import:: java.io ObjectInputStream

.. java:import:: java.io ObjectOutputStream

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model.impl NetworkImpl

.. java:import:: ca.nengo.util TimeSeries

FileManager
===========

.. java:package:: ca.nengo.io
   :noindex:

.. java:type:: public class FileManager

   Handles saving and loading of Node TODO: a better job (this is a quick one) TODO: is there any metadata to store? TODO: test

   :author: Bryan Tripp

Fields
------
ENSEMBLE_EXTENSION
^^^^^^^^^^^^^^^^^^

.. java:field:: public static final String ENSEMBLE_EXTENSION
   :outertype: FileManager

   Extension for serialized NEF networks

Methods
-------
generate
^^^^^^^^

.. java:method:: public void generate(Node node, String destination) throws IOException
   :outertype: FileManager

load
^^^^

.. java:method:: public Object load(File source) throws IOException, ClassNotFoundException
   :outertype: FileManager

   :param source: Serialized file to load
   :throws ClassNotFoundException: if the serialized file contains classes not known in this context
   :throws IOException: if there's a problem writing to disk
   :return: Object represented by the serialized file

save
^^^^

.. java:method:: public void save(Node node, File destination) throws IOException
   :outertype: FileManager

   :param node: Node to serialize
   :param destination: File to save serialized Node in
   :throws IOException: if there's a problem writing to disk

save
^^^^

.. java:method:: public void save(TimeSeries timeSeries, File destination) throws IOException
   :outertype: FileManager

   :param timeSeries: TimeSeries to serialize
   :param destination: File to save serialized TimeSeries in
   :throws IOException: if there's a problem writing to disk


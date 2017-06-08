.. java:import:: java.io BufferedInputStream

.. java:import:: java.io BufferedOutputStream

.. java:import:: java.io File

.. java:import:: java.io FileInputStream

.. java:import:: java.io FileNotFoundException

.. java:import:: java.io FileOutputStream

.. java:import:: java.io IOException

.. java:import:: java.io ObjectInput

.. java:import:: java.io ObjectInputStream

.. java:import:: java.io ObjectOutput

.. java:import:: java.io ObjectOutputStream

.. java:import:: java.util ArrayList

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: ca.nengo.ui.lib.util Util

HistoryCompletor
================

.. java:package:: ca.nengo.ui.script
   :noindex:

.. java:type:: public class HistoryCompletor extends CommandCompletor

   A list of commands that have been entered previously.

   :author: Bryan Tripp

Fields
------
HISTORY_LOCATION_PROPERTY
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static String HISTORY_LOCATION_PROPERTY
   :outertype: HistoryCompletor

Constructors
------------
HistoryCompletor
^^^^^^^^^^^^^^^^

.. java:constructor:: public HistoryCompletor()
   :outertype: HistoryCompletor

Methods
-------
add
^^^

.. java:method:: public void add(String command)
   :outertype: HistoryCompletor

   Add command string to CommandCompletor and update commandhistory file


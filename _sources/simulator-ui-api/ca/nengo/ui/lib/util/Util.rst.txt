.. java:import:: java.io BufferedInputStream

.. java:import:: java.io BufferedOutputStream

.. java:import:: java.io ByteArrayInputStream

.. java:import:: java.io ByteArrayOutputStream

.. java:import:: java.io File

.. java:import:: java.io FileInputStream

.. java:import:: java.io FileOutputStream

.. java:import:: java.io IOException

.. java:import:: java.io ObjectInputStream

.. java:import:: java.io ObjectOutputStream

.. java:import:: java.io Serializable

.. java:import:: java.lang.reflect Array

.. java:import:: java.util ListIterator

.. java:import:: javax.swing JOptionPane

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PiccoloNodeInWorld

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

.. java:import:: edu.umd.cs.piccolo.util PStack

Util
====

.. java:package:: ca.nengo.ui.lib.util
   :noindex:

.. java:type:: public class Util

   Miscellaneous static functions used by the user interface

   :author: Shu Wu

Methods
-------
Assert
^^^^^^

.. java:method:: public static void Assert(boolean bool)
   :outertype: Util

Assert
^^^^^^

.. java:method:: public static void Assert(boolean bool, String msg)
   :outertype: Util

Message
^^^^^^^

.. java:method:: public static void Message(String msg, String title)
   :outertype: Util

arrayToString
^^^^^^^^^^^^^

.. java:method:: public static String arrayToString(Object array)
   :outertype: Util

cloneSerializable
^^^^^^^^^^^^^^^^^

.. java:method:: public static Object cloneSerializable(Serializable obj)
   :outertype: Util

copyFile
^^^^^^^^

.. java:method:: public static void copyFile(File fromFile, File toFile) throws IOException
   :outertype: Util

debugMsg
^^^^^^^^

.. java:method:: public static void debugMsg(String msg)
   :outertype: Util

format
^^^^^^

.. java:method:: public static String format(double val, int n, int w)
   :outertype: Util

getExtension
^^^^^^^^^^^^

.. java:method:: public static String getExtension(File f)
   :outertype: Util

getNodeFromPickPath
^^^^^^^^^^^^^^^^^^^

.. java:method:: @SuppressWarnings public static WorldObject getNodeFromPickPath(PInputEvent event, Class<? extends WorldObject> type)
   :outertype: Util

   :param event: Event sent from Piccolo
   :param type: The type of node to be picked from the pick tree
   :return: The first node on the pick path that matches the parameter type

isArray
^^^^^^^

.. java:method:: public static boolean isArray(Object obj)
   :outertype: Util

showException
^^^^^^^^^^^^^

.. java:method:: public static void showException(Exception exception)
   :outertype: Util

sleep
^^^^^

.. java:method:: public static void sleep(long time)
   :outertype: Util

truncateString
^^^^^^^^^^^^^^

.. java:method:: public static String truncateString(String input, int maxLength)
   :outertype: Util


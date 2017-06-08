.. java:import:: java.io File

.. java:import:: java.lang.reflect Constructor

.. java:import:: java.lang.reflect Method

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util StringTokenizer

.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.util ClassUtils

.. java:import:: com.thoughtworks.qdox JavaDocBuilder

.. java:import:: com.thoughtworks.qdox.model AbstractJavaEntity

.. java:import:: com.thoughtworks.qdox.model DocletTag

.. java:import:: com.thoughtworks.qdox.model JavaClass

.. java:import:: com.thoughtworks.qdox.model JavaMethod

.. java:import:: com.thoughtworks.qdox.model JavaParameter

JavaSourceParser
================

.. java:package:: ca.nengo.config
   :noindex:

.. java:type:: public class JavaSourceParser

   Utilities for extracting data from Java source code files, including variable names and documentation.

   :author: Bryan Tripp

Methods
-------
addSource
^^^^^^^^^

.. java:method:: public static void addSource(File baseDir)
   :outertype: JavaSourceParser

   Adds source code under the given directory to the database.

   :param baseDir: Root directory of source code

getArgDocs
^^^^^^^^^^

.. java:method:: public static String getArgDocs(Method m, int arg)
   :outertype: JavaSourceParser

   :param m: A Java method
   :param arg: Index of an argument on this method
   :return: Argument documentation if available, otherwise null

getArgDocs
^^^^^^^^^^

.. java:method:: public static String getArgDocs(Constructor<?> c, int arg)
   :outertype: JavaSourceParser

   :param c: A Java constructor
   :param arg: Index of an argument on this constructor
   :return: Argument documentation if available, otherwise null

getArgNames
^^^^^^^^^^^

.. java:method:: public static String[] getArgNames(Method m)
   :outertype: JavaSourceParser

   :param m: A Java method
   :return: Names of method arguments if available, otherwise the default {"arg0", "arg1", ...}

getArgNames
^^^^^^^^^^^

.. java:method:: public static String[] getArgNames(Constructor<?> c)
   :outertype: JavaSourceParser

   :param c: A Java constructor
   :return: Names of constructor arguments if available, otherwise the default {"arg0", "arg1", ...}

getDocs
^^^^^^^

.. java:method:: public static String getDocs(Class<?> c)
   :outertype: JavaSourceParser

   :param c: A Java class
   :return: Class-level documentation if available, othewise null

getDocs
^^^^^^^

.. java:method:: public static String getDocs(Method m)
   :outertype: JavaSourceParser

   :param m: A Java method
   :return: Method-level documentation if available, otherwise empty string

getDocs
^^^^^^^

.. java:method:: public static String getDocs(Constructor<?> c)
   :outertype: JavaSourceParser

   :param c: A Java constructor
   :return: Constructor documentation if available, otherwise empty string

getMethod
^^^^^^^^^

.. java:method:: public static Method getMethod(String reference, String referringClassName) throws SecurityException, NoSuchMethodException, ClassNotFoundException
   :outertype: JavaSourceParser

   :param reference: A JavaDoc see-tag-style reference, ie fully.qualified.ClassName#methodName(ArgType0, ArgType1)
   :param referringClassName: Name of class on which the see tag is written (used to find default package if arg types are not qualified, and class if undefined)
   :throws SecurityException: if we can't access the method
   :throws NoSuchMethodException: if the method doesn't exist
   :throws ClassNotFoundException: if the class doesn't exist
   :return: Matching Method if possible, otherwise null

getSignature
^^^^^^^^^^^^

.. java:method:: public static String getSignature(Method m)
   :outertype: JavaSourceParser

   :param m: A Java method
   :return: A text representation of the method signature (for display)

removeTags
^^^^^^^^^^

.. java:method:: public static String removeTags(String html)
   :outertype: JavaSourceParser

   :param html: Some text
   :return: The same text with HTML tags removed


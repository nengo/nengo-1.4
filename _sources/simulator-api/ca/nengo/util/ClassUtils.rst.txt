.. java:import:: java.lang.reflect Array

ClassUtils
==========

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public class ClassUtils

   Class-related utility methods.

   :author: Bryan Tripp

Methods
-------
forName
^^^^^^^

.. java:method:: public static Class<?> forName(String name) throws ClassNotFoundException
   :outertype: ClassUtils

   As Class.forName(String) but arrays and primitives are also handled.

   :param name: Name of a Class
   :throws ClassNotFoundException:
   :return: Named Class

getName
^^^^^^^

.. java:method:: public static String getName(Class<?> c)
   :outertype: ClassUtils

   :param c: A Class
   :return: The class name, with arrays identified with trailing "[]"


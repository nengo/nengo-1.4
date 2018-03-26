.. java:import:: java.io BufferedReader

.. java:import:: java.io IOException

.. java:import:: java.io InputStream

.. java:import:: java.io InputStreamReader

.. java:import:: java.lang.reflect Modifier

.. java:import:: java.util ArrayList

.. java:import:: java.util HashMap

.. java:import:: java.util Iterator

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: org.apache.log4j Logger

ClassRegistry
=============

.. java:package:: ca.nengo.config
   :noindex:

.. java:type:: public final class ClassRegistry

   A registry of implementations of selected types of interest (subclasses and interface implementations). This gets used when generating the list of available input functions (e.g., PFunction in simulator-ui) TODO: unit tests

   :author: Bryan Tripp

Fields
------
IMPLS_LOCATION_PROPERTY
^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final String IMPLS_LOCATION_PROPERTY
   :outertype: ClassRegistry

   Location of implementations...

TYPES_LOCATION_PROPERTY
^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final String TYPES_LOCATION_PROPERTY
   :outertype: ClassRegistry

   Location of types...

Methods
-------
addRegisterableType
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void addRegisterableType(Class<?> type)
   :outertype: ClassRegistry

   Adds a class to the list of types whose implementations can be registered (only implementations of certain types can be registered).

   :param type: Type to add to list of registerable types

getImplementations
^^^^^^^^^^^^^^^^^^

.. java:method:: public List<Class<?>> getImplementations(Class<?> type)
   :outertype: ClassRegistry

   :param type: A registerable type
   :return: A list of registered implementations of the given type (empty if type is unknown)

getInstance
^^^^^^^^^^^

.. java:method:: public static synchronized ClassRegistry getInstance()
   :outertype: ClassRegistry

   :return: Shared instance

register
^^^^^^^^

.. java:method:: public void register(Class<?> implementation)
   :outertype: ClassRegistry

   Registers an implementation against any of the registerable types which it is assignable from.

   :param implementation: Class to register as an implementation of matching registerable types

register
^^^^^^^^

.. java:method:: public void register(String implementationName) throws ClassNotFoundException
   :outertype: ClassRegistry

   As register(Class), but by name.

   :param implementationName: Name of implementation to register
   :throws ClassNotFoundException: if the class doesn't exist?


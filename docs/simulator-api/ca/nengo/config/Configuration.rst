.. java:import:: java.util List

.. java:import:: ca.nengo.model StructuralException

Configuration
=============

.. java:package:: ca.nengo.config
   :noindex:

.. java:type:: public interface Configuration

   Contains all the variable parameters of a Configurable object.

   :author: Bryan Tripp

Methods
-------
getConfigurable
^^^^^^^^^^^^^^^

.. java:method:: public Object getConfigurable()
   :outertype: Configuration

   :return: The Object to which this Configuration belongs

getProperty
^^^^^^^^^^^

.. java:method:: public Property getProperty(String name) throws StructuralException
   :outertype: Configuration

   :param name: Name of a configuration property
   :throws StructuralException: if the named property does not exist
   :return: Parameter of the given name

getPropertyNames
^^^^^^^^^^^^^^^^

.. java:method:: public List<String> getPropertyNames()
   :outertype: Configuration

   :return: Names of configuration properties


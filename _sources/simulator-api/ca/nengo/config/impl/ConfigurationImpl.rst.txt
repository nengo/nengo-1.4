.. java:import:: java.util ArrayList

.. java:import:: java.util HashMap

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: ca.nengo.config Configuration

.. java:import:: ca.nengo.config Property

.. java:import:: ca.nengo.model StructuralException

ConfigurationImpl
=================

.. java:package:: ca.nengo.config.impl
   :noindex:

.. java:type:: public class ConfigurationImpl implements Configuration

   Default implementation of Configuration. This implementation reports property names in the order they are defined.

   :author: Bryan Tripp

Constructors
------------
ConfigurationImpl
^^^^^^^^^^^^^^^^^

.. java:constructor:: public ConfigurationImpl(Object configurable)
   :outertype: ConfigurationImpl

   :param configurable: The Object to which this Configuration belongs

Methods
-------
defineProperty
^^^^^^^^^^^^^^

.. java:method:: public void defineProperty(Property property)
   :outertype: ConfigurationImpl

   To be called by the associated Configurable, immediately after construction (once per property).

   :param property: The new Property

defineSingleValuedProperty
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public SingleValuedPropertyImpl defineSingleValuedProperty(String name, Class<?> c, boolean mutable)
   :outertype: ConfigurationImpl

   :param name: Property to be defined
   :param c: Class on which the property is defined
   :param mutable: Mutable?
   :return: SingleValuedPropertyImpl

defineTemplateProperty
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public TemplateProperty defineTemplateProperty(String name, Class<?> c, Object defaultValue)
   :outertype: ConfigurationImpl

   :param name: Property to be defined
   :param c: Class on which the property is defined
   :param defaultValue: Default object
   :return: TemplateProperty

getConfigurable
^^^^^^^^^^^^^^^

.. java:method:: public Object getConfigurable()
   :outertype: ConfigurationImpl

   **See also:** :java:ref:`ca.nengo.config.Configuration.getConfigurable()`

getProperty
^^^^^^^^^^^

.. java:method:: public Property getProperty(String name) throws StructuralException
   :outertype: ConfigurationImpl

   **See also:** :java:ref:`ca.nengo.config.Configuration.getProperty(java.lang.String)`

getPropertyNames
^^^^^^^^^^^^^^^^

.. java:method:: public List<String> getPropertyNames()
   :outertype: ConfigurationImpl

   **See also:** :java:ref:`ca.nengo.config.Configuration.getPropertyNames()`

removeProperty
^^^^^^^^^^^^^^

.. java:method:: public void removeProperty(String name)
   :outertype: ConfigurationImpl

   :param name: Property to remove

renameProperty
^^^^^^^^^^^^^^

.. java:method:: public void renameProperty(String oldName, String newName)
   :outertype: ConfigurationImpl

   :param oldName: The existing name of the Property
   :param newName: The replacement name of the Property


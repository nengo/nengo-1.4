.. java:import:: java.util ArrayList

.. java:import:: java.util List

ConfigSchemaImpl
================

.. java:package:: ca.nengo.ui.configurable
   :noindex:

.. java:type:: public class ConfigSchemaImpl implements ConfigSchema

   Default implementation of a IConfigSchema

   :author: Shu Wu

Constructors
------------
ConfigSchemaImpl
^^^^^^^^^^^^^^^^

.. java:constructor:: public ConfigSchemaImpl()
   :outertype: ConfigSchemaImpl

   Default constructor, no property descriptors

ConfigSchemaImpl
^^^^^^^^^^^^^^^^

.. java:constructor:: public ConfigSchemaImpl(Property property)
   :outertype: ConfigSchemaImpl

   :param property: TODO

ConfigSchemaImpl
^^^^^^^^^^^^^^^^

.. java:constructor:: public ConfigSchemaImpl(Property[] properties)
   :outertype: ConfigSchemaImpl

   :param properties: TODO

ConfigSchemaImpl
^^^^^^^^^^^^^^^^

.. java:constructor:: public ConfigSchemaImpl(Property[] properties, Property[] advancedProperties)
   :outertype: ConfigSchemaImpl

   :param properties: TODO
   :param advancedProperties: TODO

Methods
-------
addProperty
^^^^^^^^^^^

.. java:method:: public void addProperty(Property propDesc, int position)
   :outertype: ConfigSchemaImpl

   :param propDesc: Property Descriptor
   :param position: Location to insert into the property list

getAdvancedProperties
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public List<Property> getAdvancedProperties()
   :outertype: ConfigSchemaImpl

getProperties
^^^^^^^^^^^^^

.. java:method:: public List<Property> getProperties()
   :outertype: ConfigSchemaImpl


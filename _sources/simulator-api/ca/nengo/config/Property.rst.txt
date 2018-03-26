Property
========

.. java:package:: ca.nengo.config
   :noindex:

.. java:type:: public interface Property

   An element of a Configuration; wraps some property of an object (eg a bean-pattern property).

   :author: Bryan Tripp

Methods
-------
getDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public String getDocumentation()
   :outertype: Property

   :return: Text describing the property semantics (plain text or HTML)

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: Property

   :return: Property name

getType
^^^^^^^

.. java:method:: public Class<?> getType()
   :outertype: Property

   :return: Class to which values belong

isFixedCardinality
^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isFixedCardinality()
   :outertype: Property

   :return: True if the property has a fixed number of values

isMutable
^^^^^^^^^

.. java:method:: public boolean isMutable()
   :outertype: Property

   :return: True if values can be changed after construction of the Configurable

setName
^^^^^^^

.. java:method:: public void setName(String name)
   :outertype: Property

   :param name: New Property name


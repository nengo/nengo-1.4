.. java:import:: java.lang.reflect Method

.. java:import:: ca.nengo.config Configuration

.. java:import:: ca.nengo.config JavaSourceParser

.. java:import:: ca.nengo.config Property

AbstractProperty
================

.. java:package:: ca.nengo.config.impl
   :noindex:

.. java:type:: public abstract class AbstractProperty implements Property

   Base implementation of Property.

   :author: Bryan Tripp

Constructors
------------
AbstractProperty
^^^^^^^^^^^^^^^^

.. java:constructor:: public AbstractProperty(Configuration configuration, String name, Class<?> c, boolean mutable)
   :outertype: AbstractProperty

   :param configuration: Configuration to which the Property belongs
   :param name: Name of the Property
   :param c: Type of the Property
   :param mutable: Whether the Property value(s) can be modified

Methods
-------
getConfiguration
^^^^^^^^^^^^^^^^

.. java:method:: protected Configuration getConfiguration()
   :outertype: AbstractProperty

getDefaultDocumentation
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected String getDefaultDocumentation(Method[] methods)
   :outertype: AbstractProperty

   :param methods: The methods that underlie this property
   :return: A default documentation string composed of javadocs for these methods

getDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public String getDocumentation()
   :outertype: AbstractProperty

   **See also:** :java:ref:`ca.nengo.config.Property.getDocumentation()`

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: AbstractProperty

   **See also:** :java:ref:`ca.nengo.config.Property.getName()`

getType
^^^^^^^

.. java:method:: public Class<?> getType()
   :outertype: AbstractProperty

   **See also:** :java:ref:`ca.nengo.config.Property.getType()`

isMutable
^^^^^^^^^

.. java:method:: public boolean isMutable()
   :outertype: AbstractProperty

   **See also:** :java:ref:`ca.nengo.config.Property.isMutable()`

setDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public void setDocumentation(String text)
   :outertype: AbstractProperty

   :param text: New documentation text (can be plain text or HTML)

setName
^^^^^^^

.. java:method:: public void setName(String name)
   :outertype: AbstractProperty

   **See also:** :java:ref:`ca.nengo.config.Property.setName(java.lang.String)`


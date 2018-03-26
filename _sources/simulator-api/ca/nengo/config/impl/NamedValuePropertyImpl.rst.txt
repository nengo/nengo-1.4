.. java:import:: java.lang.reflect Array

.. java:import:: java.lang.reflect InvocationTargetException

.. java:import:: java.lang.reflect Method

.. java:import:: java.util ArrayList

.. java:import:: java.util Arrays

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: ca.nengo.config Configuration

.. java:import:: ca.nengo.config NamedValueProperty

.. java:import:: ca.nengo.model StructuralException

NamedValuePropertyImpl
======================

.. java:package:: ca.nengo.config.impl
   :noindex:

.. java:type:: public class NamedValuePropertyImpl extends AbstractProperty implements NamedValueProperty

   Default implementation of NamedValueProperty. This implementation uses reflection to call methods on an underlying configurable object in order to get and set multiple property values.

   Use of this class is analogous to \ :java:ref:`ca.nengo.config.impl.ListPropertyImpl`\ . See ListPropertyImpl docs for more information.

   :author: Bryan Tripp

Constructors
------------
NamedValuePropertyImpl
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public NamedValuePropertyImpl(Configuration configuration, String name, Class<?> c, Method getter, Method namesGetter)
   :outertype: NamedValuePropertyImpl

   :param configuration: Configuration to which this Property belongs
   :param name: Parameter name
   :param c: Parameter type
   :param getter: A method on type c with a String argument that returns the named property value
   :param namesGetter: A zero-argument method on type c that returns a String array with names of the property values

NamedValuePropertyImpl
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public NamedValuePropertyImpl(Configuration configuration, String name, Class<?> c, Method mapGetter)
   :outertype: NamedValuePropertyImpl

   :param configuration: Configuration to which this Property belongs
   :param name: Parameter name
   :param c: Parameter type
   :param mapGetter: A zero-argument method on type c that returns a Map containing values of the property

Methods
-------
getDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: @Override public String getDocumentation()
   :outertype: NamedValuePropertyImpl

getNamedValueProperty
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static NamedValueProperty getNamedValueProperty(Configuration configuration, String name, Class<?> type)
   :outertype: NamedValuePropertyImpl

   :param configuration: Configuration to which this Property belongs
   :param name: Parameter name
   :param type: Parameter type
   :return: Property or null if the necessary methods don't exist on the underlying class

getValue
^^^^^^^^

.. java:method:: public Object getValue(String name) throws StructuralException
   :outertype: NamedValuePropertyImpl

   :throws StructuralException: if the value can't be retrieved

   **See also:** :java:ref:`ca.nengo.config.NamedValueProperty.getValue(java.lang.String)`

getValueNames
^^^^^^^^^^^^^

.. java:method:: @SuppressWarnings public List<String> getValueNames()
   :outertype: NamedValuePropertyImpl

   **See also:** :java:ref:`ca.nengo.config.NamedValueProperty.getValueNames()`

isFixedCardinality
^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isFixedCardinality()
   :outertype: NamedValuePropertyImpl

   **See also:** :java:ref:`ca.nengo.config.Property.isFixedCardinality()`

isMutable
^^^^^^^^^

.. java:method:: public boolean isMutable()
   :outertype: NamedValuePropertyImpl

   **See also:** :java:ref:`ca.nengo.config.impl.AbstractProperty.isMutable()`

isNamedAutomatically
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isNamedAutomatically()
   :outertype: NamedValuePropertyImpl

   **See also:** :java:ref:`ca.nengo.config.NamedValueProperty.isNamedAutomatically()`

removeValue
^^^^^^^^^^^

.. java:method:: public void removeValue(String name) throws StructuralException
   :outertype: NamedValuePropertyImpl

   **See also:** :java:ref:`ca.nengo.config.NamedValueProperty.removeValue(java.lang.String)`

setModifiers
^^^^^^^^^^^^

.. java:method:: public void setModifiers(Method setter, Method remover)
   :outertype: NamedValuePropertyImpl

   Sets optional methods used to make the property mutable.

   :param setter: A setter method with arg types {String, Object}; {Object} is also OK if the getType() has a zero-arg method getName() which returns a String
   :param remover: A method that removes a value by name; arg types {String}

setValue
^^^^^^^^

.. java:method:: @SuppressWarnings public void setValue(String name, Object value) throws StructuralException
   :outertype: NamedValuePropertyImpl

setValue
^^^^^^^^

.. java:method:: public void setValue(Object value) throws StructuralException
   :outertype: NamedValuePropertyImpl


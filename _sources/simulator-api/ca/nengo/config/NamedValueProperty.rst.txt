.. java:import:: java.util List

.. java:import:: ca.nengo.model StructuralException

NamedValueProperty
==================

.. java:package:: ca.nengo.config
   :noindex:

.. java:type:: public interface NamedValueProperty extends Property

   A property that can have multiple values, each of which is indexed by a String name.

   :author: Bryan Tripp

Methods
-------
getValue
^^^^^^^^

.. java:method:: public Object getValue(String name) throws StructuralException
   :outertype: NamedValueProperty

   :param name: Name of a value of this property
   :throws StructuralException: if there is no value of the given name
   :return: The value

getValueNames
^^^^^^^^^^^^^

.. java:method:: public List<String> getValueNames()
   :outertype: NamedValueProperty

   :return: Names of all values

isNamedAutomatically
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isNamedAutomatically()
   :outertype: NamedValueProperty

   :return: True if values are named automatically, in which case the setter setValue(Object) can be used; otherwise value names must be provided by the caller via setValue(String, Object)

removeValue
^^^^^^^^^^^

.. java:method:: public void removeValue(String name) throws StructuralException
   :outertype: NamedValueProperty

   Removes a value by name

   :param name: Name of value to remove
   :throws StructuralException: if isFixedCardinality()

setValue
^^^^^^^^

.. java:method:: public void setValue(String name, Object value) throws StructuralException
   :outertype: NamedValueProperty

   Sets a value by name.

   :param name: Name of the value
   :param value: New value of the value
   :throws StructuralException: if !isMutable

setValue
^^^^^^^^

.. java:method:: public void setValue(Object value) throws StructuralException
   :outertype: NamedValueProperty

   Sets an automatically-named value

   :param value: New value of the value, from which the Property can automaticall determine the name
   :throws StructuralException: if !isNamedAutomatically() or !isMutable


.. java:import:: java.lang.reflect Array

.. java:import:: java.lang.reflect InvocationTargetException

.. java:import:: java.lang.reflect Method

.. java:import:: java.util List

.. java:import:: ca.nengo.config ConfigUtil

.. java:import:: ca.nengo.config Configuration

.. java:import:: ca.nengo.config ListProperty

.. java:import:: ca.nengo.model StructuralException

ListPropertyImpl
================

.. java:package:: ca.nengo.config.impl
   :noindex:

.. java:type:: public class ListPropertyImpl extends AbstractProperty implements ListProperty

   Default implementation of ListProperty. This implementation uses reflection to call methods on an underlying configurable object in order to get and set multiple property values.

   The easiest way to use this class is via the factory method getListProperty(...). In this case, the class of configuration.getConfigurable() is searched for methods that appear to be getters, setters, etc. of a named parameter. For example if the parameter is named "X" and has type Foo, then a method getX(int) with return type Foo is taken as the getter. Methods to get, set, insert, add, remove values, get/set arrays, and get lists are searched based on return and argument types, and a variety of probable names (including bean patterns). If there are not methods available for at least getting and counting values, null is returned. The set of other methods found determines whether the property is mutable and has fixed cardinality (i.e. # of values).

   If customization is needed, there are two alternative public constructors that accept user-specified methods, and additional functionality can then be imparted via setSetter(...), setArraySetter(...), and setInserter(...). This allows use of methods with unexpected names, although the expected arguments and return types are still required.

   If further customization is needed (e.g. using an Integer index argument instead of an int), then the methods of this class must be overridden.

   :author: Bryan Tripp

Constructors
------------
ListPropertyImpl
^^^^^^^^^^^^^^^^

.. java:constructor:: public ListPropertyImpl(Configuration configuration, String name, Class<?> c, Method listGetter)
   :outertype: ListPropertyImpl

   :param configuration: Configuration to which this Property is to belong
   :param name: Name of the Property, eg X if it is accessed via getX(...)
   :param c: Class of the Property
   :param listGetter: A method on the underlying class (the configuration target; not c) that returns multiple values of the property, as either an array of c or a list of c.

ListPropertyImpl
^^^^^^^^^^^^^^^^

.. java:constructor:: public ListPropertyImpl(Configuration configuration, String name, Class<?> c, Method getter, Method countGetter)
   :outertype: ListPropertyImpl

   :param configuration: Configuration to which this Property is to belong
   :param name: Name of the Property, eg X if it is accessed via getX()
   :param c: Class of the Property
   :param getter: A method on the underlying class (the configuration target; not c) that returns a single indexed value of the property, eg getX(int).
   :param countGetter: A method on the underlying class that returns the number of values of the property

Methods
-------
addValue
^^^^^^^^

.. java:method:: public void addValue(Object value) throws StructuralException
   :outertype: ListPropertyImpl

   **See also:** :java:ref:`ca.nengo.config.ListProperty.addValue(java.lang.Object)`

getDefaultValue
^^^^^^^^^^^^^^^

.. java:method:: public Object getDefaultValue()
   :outertype: ListPropertyImpl

   **See also:** :java:ref:`ca.nengo.config.ListProperty.getDefaultValue()`

getDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: @Override public String getDocumentation()
   :outertype: ListPropertyImpl

getListProperty
^^^^^^^^^^^^^^^

.. java:method:: public static ListProperty getListProperty(Configuration configuration, String name, Class<?> type)
   :outertype: ListPropertyImpl

   :param configuration: Configuration to which this Property belongs
   :param name: Parameter name
   :param type: Parameter type
   :return: Property or null if the necessary methods don't exist on the underlying class

getMethod
^^^^^^^^^

.. java:method:: public static Method getMethod(Class<?> c, String[] names, Class<?>[] argTypes, Class<?> returnType)
   :outertype: ListPropertyImpl

   Looks for defined method

   :param c: Class to search
   :param names: Methods to find?
   :param argTypes: Argument types
   :param returnType: Return type
   :return: The Method, or null if it doesn't exist

getNumValues
^^^^^^^^^^^^

.. java:method:: public int getNumValues()
   :outertype: ListPropertyImpl

   **See also:** :java:ref:`ca.nengo.config.ListProperty.getNumValues()`

getValue
^^^^^^^^

.. java:method:: public Object getValue(int index) throws StructuralException
   :outertype: ListPropertyImpl

   **See also:** :java:ref:`ca.nengo.config.ListProperty.getValue(int)`

insert
^^^^^^

.. java:method:: public void insert(int index, Object value) throws StructuralException
   :outertype: ListPropertyImpl

   **See also:** :java:ref:`ca.nengo.config.ListProperty.insert(int,java.lang.Object)`

isFixedCardinality
^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isFixedCardinality()
   :outertype: ListPropertyImpl

   **See also:** :java:ref:`ca.nengo.config.Property.isFixedCardinality()`

isMutable
^^^^^^^^^

.. java:method:: @Override public boolean isMutable()
   :outertype: ListPropertyImpl

remove
^^^^^^

.. java:method:: public void remove(int index) throws StructuralException
   :outertype: ListPropertyImpl

   **See also:** :java:ref:`ca.nengo.config.ListProperty.remove(int)`

setArraySetter
^^^^^^^^^^^^^^

.. java:method:: public void setArraySetter(Method arraySetter)
   :outertype: ListPropertyImpl

   :param arraySetter: A method on the underlying class that acts as a setter for all values of the property using an array argument, eg setX(Object[])

setInserter
^^^^^^^^^^^

.. java:method:: public void setInserter(Method inserter, Method adder, Method remover)
   :outertype: ListPropertyImpl

   :param inserter: A method on the underlying class that inserts a value, eg insertX(int, Object)
   :param adder: A method on the underlying class that adds a value to the end of the list, eg addX(Object)
   :param remover: A method on the underlying class that removes a value, eg removeX(int)

setSetter
^^^^^^^^^

.. java:method:: public void setSetter(Method setter)
   :outertype: ListPropertyImpl

   :param setter: A method on the underlying class acts as a setter for a single value of the property, eg setX(int, Object)

setValue
^^^^^^^^

.. java:method:: public void setValue(int index, Object value) throws StructuralException
   :outertype: ListPropertyImpl

   **See also:** :java:ref:`ca.nengo.config.ListProperty.setValue(int,java.lang.Object)`


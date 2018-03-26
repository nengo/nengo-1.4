.. java:import:: java.lang.reflect InvocationTargetException

.. java:import:: java.lang.reflect Method

.. java:import:: ca.nengo.config Configuration

.. java:import:: ca.nengo.config SingleValuedProperty

.. java:import:: ca.nengo.model StructuralException

SingleValuedPropertyImpl
========================

.. java:package:: ca.nengo.config.impl
   :noindex:

.. java:type:: public class SingleValuedPropertyImpl extends AbstractProperty implements SingleValuedProperty

   Default implementation of single-valued Properties.

   :author: Bryan Tripp

Constructors
------------
SingleValuedPropertyImpl
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public SingleValuedPropertyImpl(Configuration configuration, String name, Class<?> c, Method getter)
   :outertype: SingleValuedPropertyImpl

   Constructor for immutable single-valued properties.

   :param configuration: Configuration to which this Property belongs
   :param name: Parameter name
   :param c: Parameter type
   :param getter: Zero-arg getter method

SingleValuedPropertyImpl
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public SingleValuedPropertyImpl(Configuration configuration, String name, Class<?> c, Method getter, Method setter)
   :outertype: SingleValuedPropertyImpl

   Constructor for mutable single-valued properties.

   :param configuration: Configuration to which this Property belongs
   :param name: Parameter name
   :param c: Parameter type
   :param getter: Zero-arg getter method
   :param setter: Single-arg setter method

Methods
-------
getDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: @Override public String getDocumentation()
   :outertype: SingleValuedPropertyImpl

getSingleValuedProperty
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static SingleValuedProperty getSingleValuedProperty(Configuration configuration, String name, Class<?> type)
   :outertype: SingleValuedPropertyImpl

   :param configuration: Configuration to which this Property belongs
   :param name: Parameter name
   :param type: Parameter type
   :return: Property or null if the necessary methods don't exist on the underlying class

getValue
^^^^^^^^

.. java:method:: public Object getValue()
   :outertype: SingleValuedPropertyImpl

   **See also:** :java:ref:`ca.nengo.config.SingleValuedProperty.getValue()`

isFixedCardinality
^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isFixedCardinality()
   :outertype: SingleValuedPropertyImpl

   **See also:** :java:ref:`ca.nengo.config.Property.isFixedCardinality()`

setValue
^^^^^^^^

.. java:method:: public void setValue(Object value) throws StructuralException
   :outertype: SingleValuedPropertyImpl

   By default, attempts to call method setX(y) on Configurable, where X is the name of the property (with first letter capitalized) and y is the value (changed to a primitive if it's a primitive wrapper). A Configurable that needs different behaviour should override this method.

   **See also:** :java:ref:`ca.nengo.config.SingleValuedProperty.setValue(java.lang.Object)`


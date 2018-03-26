.. java:import:: ca.nengo.config Configuration

.. java:import:: ca.nengo.config SingleValuedProperty

.. java:import:: ca.nengo.model StructuralException

TemplateProperty
================

.. java:package:: ca.nengo.config.impl
   :noindex:

.. java:type:: public class TemplateProperty extends AbstractProperty implements SingleValuedProperty

   A SingleValuedProperty that is not attached to getter/setter methods on an underlying class, but instead stores its value internally. It can be used to manage values of constructor/method arguments (rather than object properties).

   :author: Bryan Tripp

Constructors
------------
TemplateProperty
^^^^^^^^^^^^^^^^

.. java:constructor:: public TemplateProperty(Configuration configuration, String name, Class<?> c, Object defaultValue)
   :outertype: TemplateProperty

   :param configuration: Configuration to which this Property belongs
   :param name: Name of the property
   :param c: Type of the property value
   :param defaultValue: Default property value

Methods
-------
getValue
^^^^^^^^

.. java:method:: public Object getValue()
   :outertype: TemplateProperty

   **See also:** :java:ref:`ca.nengo.config.SingleValuedProperty.getValue()`

isFixedCardinality
^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isFixedCardinality()
   :outertype: TemplateProperty

   **See also:** :java:ref:`ca.nengo.config.Property.isFixedCardinality()`

setValue
^^^^^^^^

.. java:method:: public void setValue(Object value) throws StructuralException
   :outertype: TemplateProperty

   **See also:** :java:ref:`ca.nengo.config.SingleValuedProperty.setValue(java.lang.Object)`


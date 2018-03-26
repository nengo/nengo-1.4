.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: ca.nengo.config Configuration

.. java:import:: ca.nengo.config ListProperty

.. java:import:: ca.nengo.model StructuralException

TemplateArrayProperty
=====================

.. java:package:: ca.nengo.config.impl
   :noindex:

.. java:type:: public class TemplateArrayProperty extends AbstractProperty implements ListProperty

   A ListProperty that is not attached to getter/setter methods on an underlying class, but instead stores its values internally. It can be used to manage array or list values of constructor/method arguments (rather than multi-valued object properties). Similar to TemplateProperty but multivalued.

   :author: Bryan Tripp

Constructors
------------
TemplateArrayProperty
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public TemplateArrayProperty(Configuration configuration, String name, Class<?> c)
   :outertype: TemplateArrayProperty

   :param configuration: Configuration to which this Property belongs
   :param name: Name of the property
   :param c: Type of the property value

Methods
-------
addValue
^^^^^^^^

.. java:method:: public void addValue(Object value) throws StructuralException
   :outertype: TemplateArrayProperty

   **See also:** :java:ref:`ca.nengo.config.ListProperty.addValue(java.lang.Object)`

getDefaultValue
^^^^^^^^^^^^^^^

.. java:method:: public Object getDefaultValue()
   :outertype: TemplateArrayProperty

   **See also:** :java:ref:`ca.nengo.config.ListProperty.getDefaultValue()`

getNumValues
^^^^^^^^^^^^

.. java:method:: public int getNumValues()
   :outertype: TemplateArrayProperty

   **See also:** :java:ref:`ca.nengo.config.ListProperty.getNumValues()`

getValue
^^^^^^^^

.. java:method:: public Object getValue(int index) throws StructuralException
   :outertype: TemplateArrayProperty

   **See also:** :java:ref:`ca.nengo.config.ListProperty.getValue(int)`

insert
^^^^^^

.. java:method:: public void insert(int index, Object value) throws StructuralException
   :outertype: TemplateArrayProperty

   **See also:** :java:ref:`ca.nengo.config.ListProperty.insert(int,java.lang.Object)`

isFixedCardinality
^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isFixedCardinality()
   :outertype: TemplateArrayProperty

   **See also:** :java:ref:`ca.nengo.config.Property.isFixedCardinality()`

remove
^^^^^^

.. java:method:: public void remove(int index) throws StructuralException
   :outertype: TemplateArrayProperty

   **See also:** :java:ref:`ca.nengo.config.ListProperty.remove(int)`

setValue
^^^^^^^^

.. java:method:: public void setValue(int index, Object value) throws StructuralException
   :outertype: TemplateArrayProperty

   **See also:** :java:ref:`ca.nengo.config.ListProperty.setValue(int,java.lang.Object)`


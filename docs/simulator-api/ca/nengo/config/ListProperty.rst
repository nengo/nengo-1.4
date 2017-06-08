.. java:import:: ca.nengo.model StructuralException

ListProperty
============

.. java:package:: ca.nengo.config
   :noindex:

.. java:type:: public interface ListProperty extends Property

   A Property that can have multiple values, each of which is identified by an integer index.

   :author: Bryan Tripp

Methods
-------
addValue
^^^^^^^^

.. java:method:: public void addValue(Object value) throws StructuralException
   :outertype: ListProperty

   :param value: New value to be added to the end of the list
   :throws StructuralException: if the value is invalid (as in setValue) or the Property is immutable or fixed-cardinality

getDefaultValue
^^^^^^^^^^^^^^^

.. java:method:: public Object getDefaultValue()
   :outertype: ListProperty

   :return: Default value for insertions TODO: remove; use default from NewConfigurableDialog (move to ConfigUtil)

getNumValues
^^^^^^^^^^^^

.. java:method:: public int getNumValues()
   :outertype: ListProperty

   :return: Number of repeated values of this Property

getValue
^^^^^^^^

.. java:method:: public Object getValue(int index) throws StructuralException
   :outertype: ListProperty

   :param index: Index of a certain single value of a multi-valued property
   :throws StructuralException: if the given index is out of range
   :return: The value at the given index

insert
^^^^^^

.. java:method:: public void insert(int index, Object value) throws StructuralException
   :outertype: ListProperty

   :param index: Index at which new value is to be inserted
   :param value: New value
   :throws StructuralException: if the value is invalid (as in setValue) or the Property is immutable or fixed-cardinality or the index is out of range

remove
^^^^^^

.. java:method:: public void remove(int index) throws StructuralException
   :outertype: ListProperty

   :param index: Index of a single value of a multi-valued property that is to be removed
   :throws StructuralException: if the given index is out of range or the Property is immutable or fixed cardinality

setValue
^^^^^^^^

.. java:method:: public void setValue(int index, Object value) throws StructuralException
   :outertype: ListProperty

   :param index: Index of a certain single value of a multi-valued property
   :param value: New value to replace that at the given index
   :throws StructuralException: if the value is invalid (as in setValue) or the given index is out of range or the Property is immutable


.. java:import:: ca.nengo.model StructuralException

SingleValuedProperty
====================

.. java:package:: ca.nengo.config
   :noindex:

.. java:type:: public interface SingleValuedProperty extends Property

   A Property that has a single value.

   :author: Bryan Tripp

Methods
-------
getValue
^^^^^^^^

.. java:method:: public Object getValue()
   :outertype: SingleValuedProperty

   :return: Value (for single-valued properties) or first value (for multi-valued properties)

setValue
^^^^^^^^

.. java:method:: public void setValue(Object value) throws StructuralException
   :outertype: SingleValuedProperty

   :param value: New value (for single-valued properties) or first value (for multi-valued properties)
   :throws StructuralException: if the given value is not one of the allowed classes, or if the Configurable rejects it for any other reason (eg inconsistency with other properties)


.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math.impl ConstantFunction

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable ConfigSchema

.. java:import:: ca.nengo.ui.configurable ConfigSchemaImpl

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable.descriptors PFloat

.. java:import:: ca.nengo.ui.configurable.descriptors PInt

FnConstant
==========

.. java:package:: ca.nengo.ui.configurable.descriptors.functions
   :noindex:

.. java:type:: public class FnConstant extends AbstractFn

   TODO

   :author: TODO

Constructors
------------
FnConstant
^^^^^^^^^^

.. java:constructor:: public FnConstant(int dimension, boolean isEditable)
   :outertype: FnConstant

   :param dimension: TODO
   :param isEditable: TODO

Methods
-------
createFunction
^^^^^^^^^^^^^^

.. java:method:: @Override protected Function createFunction(ConfigResult props) throws ConfigException
   :outertype: FnConstant

getFunction
^^^^^^^^^^^

.. java:method:: @Override public ConstantFunction getFunction()
   :outertype: FnConstant

getSchema
^^^^^^^^^

.. java:method:: public ConfigSchema getSchema()
   :outertype: FnConstant


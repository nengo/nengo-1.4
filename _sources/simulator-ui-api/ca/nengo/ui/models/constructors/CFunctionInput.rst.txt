.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl FunctionInput

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable ConfigSchemaImpl

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable.descriptors PFunctionArray

.. java:import:: ca.nengo.ui.models.nodes UIFunctionInput

CFunctionInput
==============

.. java:package:: ca.nengo.ui.models.constructors
   :noindex:

.. java:type:: public class CFunctionInput extends ConstructableNode

Constructors
------------
CFunctionInput
^^^^^^^^^^^^^^

.. java:constructor:: public CFunctionInput()
   :outertype: CFunctionInput

Methods
-------
createNode
^^^^^^^^^^

.. java:method:: @Override protected Node createNode(ConfigResult props, String name) throws ConfigException
   :outertype: CFunctionInput

getNodeConfigSchema
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public ConfigSchemaImpl getNodeConfigSchema()
   :outertype: CFunctionInput

getTypeName
^^^^^^^^^^^

.. java:method:: public String getTypeName()
   :outertype: CFunctionInput


.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model.impl NetworkImpl

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable ConfigSchemaImpl

.. java:import:: ca.nengo.ui.models.nodes UINetwork

CNetwork
========

.. java:package:: ca.nengo.ui.models.constructors
   :noindex:

.. java:type:: public class CNetwork extends ConstructableNode

Constructors
------------
CNetwork
^^^^^^^^

.. java:constructor:: public CNetwork()
   :outertype: CNetwork

Methods
-------
createNode
^^^^^^^^^^

.. java:method:: @Override protected Object createNode(ConfigResult configuredProperties, String name) throws ConfigException
   :outertype: CNetwork

getNodeConfigSchema
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public ConfigSchemaImpl getNodeConfigSchema()
   :outertype: CNetwork

getTypeName
^^^^^^^^^^^

.. java:method:: public String getTypeName()
   :outertype: CNetwork


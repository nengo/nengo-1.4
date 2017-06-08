.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable ConfigSchema

.. java:import:: ca.nengo.ui.configurable ConfigSchemaImpl

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable.descriptors PString

ConstructableNode
=================

.. java:package:: ca.nengo.ui.models.constructors
   :noindex:

.. java:type:: public abstract class ConstructableNode extends AbstractConstructable

Fields
------
pName
^^^^^

.. java:field:: protected static final Property pName
   :outertype: ConstructableNode

Constructors
------------
ConstructableNode
^^^^^^^^^^^^^^^^^

.. java:constructor:: public ConstructableNode()
   :outertype: ConstructableNode

Methods
-------
configureModel
^^^^^^^^^^^^^^

.. java:method:: @Override protected final Object configureModel(ConfigResult configuredProperties) throws ConfigException
   :outertype: ConstructableNode

createNode
^^^^^^^^^^

.. java:method:: protected abstract Object createNode(ConfigResult configuredProperties, String name) throws ConfigException
   :outertype: ConstructableNode

getNodeConfigSchema
^^^^^^^^^^^^^^^^^^^

.. java:method:: public abstract ConfigSchema getNodeConfigSchema()
   :outertype: ConstructableNode

getSchema
^^^^^^^^^

.. java:method:: public final ConfigSchema getSchema()
   :outertype: ConstructableNode


.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable ConfigSchema

.. java:import:: ca.nengo.ui.configurable ConfigSchemaImpl

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable.descriptors PBoolean

.. java:import:: ca.nengo.ui.configurable.descriptors PFloat

.. java:import:: ca.nengo.ui.configurable.descriptors PTerminationWeights

.. java:import:: ca.nengo.ui.models.nodes.widgets UIDecodedTermination

CDecodedTermination
===================

.. java:package:: ca.nengo.ui.models.constructors
   :noindex:

.. java:type:: public class CDecodedTermination extends ProjectionConstructor

Constructors
------------
CDecodedTermination
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CDecodedTermination(NEFEnsemble nefEnsembleParent)
   :outertype: CDecodedTermination

Methods
-------
IsNameAvailable
^^^^^^^^^^^^^^^

.. java:method:: @Override protected boolean IsNameAvailable(String name)
   :outertype: CDecodedTermination

createModel
^^^^^^^^^^^

.. java:method:: @Override protected Object createModel(ConfigResult configuredProperties, String uniqueName) throws ConfigException
   :outertype: CDecodedTermination

getSchema
^^^^^^^^^

.. java:method:: @Override public ConfigSchema getSchema()
   :outertype: CDecodedTermination

getTypeName
^^^^^^^^^^^

.. java:method:: public String getTypeName()
   :outertype: CDecodedTermination


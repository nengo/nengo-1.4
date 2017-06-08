.. java:import:: javax.swing JComboBox

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable ConfigSchema

.. java:import:: ca.nengo.ui.configurable ConfigSchemaImpl

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable PropertyInputPanel

.. java:import:: ca.nengo.ui.configurable.descriptors PFunctionArray

.. java:import:: ca.nengo.ui.configurable.descriptors PString

.. java:import:: ca.nengo.ui.models.nodes.widgets UIDecodedOrigin

CDecodedOrigin
==============

.. java:package:: ca.nengo.ui.models.constructors
   :noindex:

.. java:type:: public class CDecodedOrigin extends ProjectionConstructor

Constructors
------------
CDecodedOrigin
^^^^^^^^^^^^^^

.. java:constructor:: public CDecodedOrigin(NEFEnsemble enfEnsembleParent)
   :outertype: CDecodedOrigin

Methods
-------
IsNameAvailable
^^^^^^^^^^^^^^^

.. java:method:: @Override protected boolean IsNameAvailable(String name)
   :outertype: CDecodedOrigin

createModel
^^^^^^^^^^^

.. java:method:: @Override protected Object createModel(ConfigResult configuredProperties, String uniqueName) throws ConfigException
   :outertype: CDecodedOrigin

getSchema
^^^^^^^^^

.. java:method:: @Override public ConfigSchema getSchema()
   :outertype: CDecodedOrigin

getTypeName
^^^^^^^^^^^

.. java:method:: public String getTypeName()
   :outertype: CDecodedOrigin


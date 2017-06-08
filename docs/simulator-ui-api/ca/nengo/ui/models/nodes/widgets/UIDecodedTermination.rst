.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef.impl DecodedTermination

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.models UINeoNode

UIDecodedTermination
====================

.. java:package:: ca.nengo.ui.models.nodes.widgets
   :noindex:

.. java:type:: public class UIDecodedTermination extends UITermination

   UI Wrapper for a Decoded Termination

   :author: Shu Wu

Fields
------
typeName
^^^^^^^^

.. java:field:: public static final String typeName
   :outertype: UIDecodedTermination

Constructors
------------
UIDecodedTermination
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: protected UIDecodedTermination(UINeoNode ensembleProxy, DecodedTermination term)
   :outertype: UIDecodedTermination

Methods
-------
destroyTerminationModel
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void destroyTerminationModel()
   :outertype: UIDecodedTermination

getTypeName
^^^^^^^^^^^

.. java:method:: @Override public String getTypeName()
   :outertype: UIDecodedTermination


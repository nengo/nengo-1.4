.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef.impl DecodedOrigin

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.models UINeoNode

UIDecodedOrigin
===============

.. java:package:: ca.nengo.ui.models.nodes.widgets
   :noindex:

.. java:type:: public class UIDecodedOrigin extends UIOrigin

   UI Wrapper for a Decoded Termination

   :author: Shu Wu

Fields
------
typeName
^^^^^^^^

.. java:field:: public static final String typeName
   :outertype: UIDecodedOrigin

Constructors
------------
UIDecodedOrigin
^^^^^^^^^^^^^^^

.. java:constructor:: protected UIDecodedOrigin(UINeoNode ensembleProxy, DecodedOrigin origin)
   :outertype: UIDecodedOrigin

Methods
-------
destroyOriginModel
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void destroyOriginModel()
   :outertype: UIDecodedOrigin

getInputDimensions
^^^^^^^^^^^^^^^^^^

.. java:method:: protected int getInputDimensions()
   :outertype: UIDecodedOrigin

getTypeName
^^^^^^^^^^^

.. java:method:: @Override public String getTypeName()
   :outertype: UIDecodedOrigin


.. java:import:: java.awt Color

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model.nef.impl DecodedOrigin

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.icons ModelIcon

.. java:import:: ca.nengo.ui.models.nodes UINetwork

.. java:import:: ca.nengo.ui.models.tooltips TooltipBuilder

UIGenericOrigin
===============

.. java:package:: ca.nengo.ui.models.nodes.widgets
   :noindex:

.. java:type::  class UIGenericOrigin extends UIOrigin

Constructors
------------
UIGenericOrigin
^^^^^^^^^^^^^^^

.. java:constructor:: protected UIGenericOrigin(UINeoNode nodeParent, Origin origin)
   :outertype: UIGenericOrigin

Methods
-------
destroyOriginModel
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void destroyOriginModel()
   :outertype: UIGenericOrigin

showRemoveModelAction
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected boolean showRemoveModelAction()
   :outertype: UIGenericOrigin


.. java:import:: ca.nengo.model.impl FunctionInput

.. java:import:: ca.nengo.ui.actions PlotFunctionNodeAction

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.icons FunctionInputIcon

.. java:import:: ca.nengo.ui.models.tooltips TooltipBuilder

UIFunctionInput
===============

.. java:package:: ca.nengo.ui.models.nodes
   :noindex:

.. java:type:: public class UIFunctionInput extends UINeoNode

   UI Wrapper of FunctionInput

   :author: Shu Wu

Fields
------
typeName
^^^^^^^^

.. java:field:: public static final String typeName
   :outertype: UIFunctionInput

Constructors
------------
UIFunctionInput
^^^^^^^^^^^^^^^

.. java:constructor:: public UIFunctionInput(FunctionInput model)
   :outertype: UIFunctionInput

Methods
-------
constructMenu
^^^^^^^^^^^^^

.. java:method:: @Override protected void constructMenu(PopupMenuBuilder menu)
   :outertype: UIFunctionInput

constructTooltips
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void constructTooltips(TooltipBuilder tooltips)
   :outertype: UIFunctionInput

getModel
^^^^^^^^

.. java:method:: @Override public FunctionInput getModel()
   :outertype: UIFunctionInput

getTypeName
^^^^^^^^^^^

.. java:method:: @Override public String getTypeName()
   :outertype: UIFunctionInput


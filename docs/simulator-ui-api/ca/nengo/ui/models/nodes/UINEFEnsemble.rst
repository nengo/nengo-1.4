.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef.impl DecodedOrigin

.. java:import:: ca.nengo.plot Plotter

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions ReversableAction

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.actions UserCancelledException

.. java:import:: ca.nengo.ui.lib.util.menus MenuBuilder

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.ui.models.constructors CDecodedOrigin

.. java:import:: ca.nengo.ui.models.constructors CDecodedTermination

.. java:import:: ca.nengo.ui.models.constructors ModelFactory

.. java:import:: ca.nengo.ui.models.nodes.widgets UIOrigin

.. java:import:: ca.nengo.ui.models.nodes.widgets UITermination

.. java:import:: ca.nengo.ui.models.tooltips TooltipBuilder

.. java:import:: ca.nengo.ui.models.viewers NodeViewer

UINEFEnsemble
=============

.. java:package:: ca.nengo.ui.models.nodes
   :noindex:

.. java:type:: public class UINEFEnsemble extends UIEnsemble

   A UI object for NEFEnsemble

   :author: Shu Wu

Fields
------
typeName
^^^^^^^^

.. java:field:: public static final String typeName
   :outertype: UINEFEnsemble

Constructors
------------
UINEFEnsemble
^^^^^^^^^^^^^

.. java:constructor:: public UINEFEnsemble(NEFEnsemble model)
   :outertype: UINEFEnsemble

Methods
-------
addDecodedOrigin
^^^^^^^^^^^^^^^^

.. java:method:: public UIOrigin addDecodedOrigin()
   :outertype: UINEFEnsemble

addDecodedTermination
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public UITermination addDecodedTermination()
   :outertype: UINEFEnsemble

   Adds a decoded termination to the UI and Ensemble Model The UI is used to configure it

   :return: PTermination created, null if not

constructMenu
^^^^^^^^^^^^^

.. java:method:: @Override protected void constructMenu(PopupMenuBuilder menu)
   :outertype: UINEFEnsemble

constructTooltips
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void constructTooltips(TooltipBuilder tooltips)
   :outertype: UINEFEnsemble

getDimensionality
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getDimensionality()
   :outertype: UINEFEnsemble

getModel
^^^^^^^^

.. java:method:: @Override public NEFEnsemble getModel()
   :outertype: UINEFEnsemble

getTypeName
^^^^^^^^^^^

.. java:method:: @Override public String getTypeName()
   :outertype: UINEFEnsemble

modelUpdated
^^^^^^^^^^^^

.. java:method:: @Override protected void modelUpdated()
   :outertype: UINEFEnsemble


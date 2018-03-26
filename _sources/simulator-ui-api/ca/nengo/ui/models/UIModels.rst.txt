.. java:import:: java.util Collection

.. java:import:: ca.nengo.ui.actions AddProbesAction

.. java:import:: ca.nengo.ui.actions RemoveModelsAction

.. java:import:: ca.nengo.ui.lib.objects.models ModelObject

.. java:import:: ca.nengo.ui.lib.util.menus AbstractMenuBuilder

UIModels
========

.. java:package:: ca.nengo.ui.models
   :noindex:

.. java:type:: public class UIModels

   Contains static members which reveal what sort of Nodes can be created by the UI

   :author: Shu

Methods
-------
constructMenuForModels
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static void constructMenuForModels(AbstractMenuBuilder menuBuilder, Class<? extends ModelObject> modelType, String typeName, Collection<ModelObject> homogeneousModels)
   :outertype: UIModels


.. java:import:: java.security InvalidParameterException

.. java:import:: java.util ArrayList

.. java:import:: java.util HashSet

.. java:import:: javax.swing JPopupMenu

.. java:import:: ca.nengo.ui.actions RemoveModelAction

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.ui.lib.world Interactable

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.activities Pulsator

.. java:import:: ca.nengo.ui.lib.world.elastic ElasticObject

.. java:import:: ca.nengo.ui.models.tooltips Tooltip

.. java:import:: ca.nengo.ui.models.tooltips TooltipBuilder

ModelObject.ModelListener
=========================

.. java:package:: ca.nengo.ui.lib.objects.models
   :noindex:

.. java:type:: public static interface ModelListener
   :outertype: ModelObject

Methods
-------
modelDestroyStarted
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void modelDestroyStarted(Object model)
   :outertype: ModelObject.ModelListener

modelDestroyed
^^^^^^^^^^^^^^

.. java:method:: public void modelDestroyed(Object model)
   :outertype: ModelObject.ModelListener


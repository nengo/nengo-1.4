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

ModelObject
===========

.. java:package:: ca.nengo.ui.lib.objects.models
   :noindex:

.. java:type:: public abstract class ModelObject extends ElasticObject implements Interactable

   :author: User

Fields
------
PROPERTY_MODEL
^^^^^^^^^^^^^^

.. java:field:: public static final String PROPERTY_MODEL
   :outertype: ModelObject

   The property name that identifies a change in this node's Model

Constructors
------------
ModelObject
^^^^^^^^^^^

.. java:constructor:: public ModelObject(Object model)
   :outertype: ModelObject

   Create a UI Wrapper around a Model

   :param model: Model

Methods
-------
addModelListener
^^^^^^^^^^^^^^^^

.. java:method:: public void addModelListener(ModelListener listener)
   :outertype: ModelObject

attachViewToModel
^^^^^^^^^^^^^^^^^

.. java:method:: protected void attachViewToModel()
   :outertype: ModelObject

   Attaches the UI from the model

constructMenu
^^^^^^^^^^^^^

.. java:method:: protected void constructMenu(PopupMenuBuilder menu)
   :outertype: ModelObject

   :return: Constructed Context Menu

constructTooltips
^^^^^^^^^^^^^^^^^

.. java:method:: protected void constructTooltips(TooltipBuilder builder)
   :outertype: ModelObject

destroyModel
^^^^^^^^^^^^

.. java:method:: public final void destroyModel()
   :outertype: ModelObject

detachViewFromModel
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void detachViewFromModel()
   :outertype: ModelObject

   Detaches the UI form the model

doubleClicked
^^^^^^^^^^^^^

.. java:method:: @Override public void doubleClicked()
   :outertype: ModelObject

   Called if this object is double clicked on

getContextMenu
^^^^^^^^^^^^^^

.. java:method:: public final JPopupMenu getContextMenu()
   :outertype: ModelObject

getFullName
^^^^^^^^^^^

.. java:method:: public String getFullName()
   :outertype: ModelObject

getIcon
^^^^^^^

.. java:method:: public WorldObject getIcon()
   :outertype: ModelObject

   :return: Icon of this node

getModel
^^^^^^^^

.. java:method:: public Object getModel()
   :outertype: ModelObject

   :return: Model

getTooltip
^^^^^^^^^^

.. java:method:: @Override public final WorldObject getTooltip()
   :outertype: ModelObject

getTypeName
^^^^^^^^^^^

.. java:method:: public abstract String getTypeName()
   :outertype: ModelObject

   :return: What this type of Model is called

initialize
^^^^^^^^^^

.. java:method:: protected void initialize()
   :outertype: ModelObject

isModelBusy
^^^^^^^^^^^

.. java:method:: public boolean isModelBusy()
   :outertype: ModelObject

modelUpdated
^^^^^^^^^^^^

.. java:method:: protected void modelUpdated()
   :outertype: ModelObject

   Updates the UI from the model

prepareForDestroy
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void prepareForDestroy()
   :outertype: ModelObject

prepareToDestroyModel
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void prepareToDestroyModel()
   :outertype: ModelObject

removeModelListener
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeModelListener(ModelListener listener)
   :outertype: ModelObject

setIcon
^^^^^^^

.. java:method:: protected void setIcon(WorldObject newIcon)
   :outertype: ModelObject

   :param newIcon: New Icon

setModelBusy
^^^^^^^^^^^^

.. java:method:: public void setModelBusy(boolean isBusy)
   :outertype: ModelObject

   :param isBusy: Whether the model is currently busy. If it is busy, the object will not be interactable.

showRemoveModelAction
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected boolean showRemoveModelAction()
   :outertype: ModelObject


.. java:import:: ca.nengo.ui.lib.objects.models ModelObject

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world WorldObject.Listener

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives Text

ModelIcon
=========

.. java:package:: ca.nengo.ui.models.icons
   :noindex:

.. java:type:: public class ModelIcon extends WorldObjectImpl implements Listener

   An Icon which has a representation and an label. It is used to represent NEO models.

   :author: Shu Wu

Constructors
------------
ModelIcon
^^^^^^^^^

.. java:constructor:: public ModelIcon(ModelObject parent, WorldObject icon)
   :outertype: ModelIcon

   :param parent: The Model the icon is representing
   :param icon: the UI representation
   :param scale: Scale of the Icon

Methods
-------
altClicked
^^^^^^^^^^

.. java:method:: @Override public void altClicked()
   :outertype: ModelIcon

configureLabel
^^^^^^^^^^^^^^

.. java:method:: public void configureLabel(boolean showType)
   :outertype: ModelIcon

   Configures the label

   :param showType: Whether to show the model type in the label

doubleClicked
^^^^^^^^^^^^^

.. java:method:: @Override public void doubleClicked()
   :outertype: ModelIcon

getIconReal
^^^^^^^^^^^

.. java:method:: protected WorldObject getIconReal()
   :outertype: ModelIcon

getModelParent
^^^^^^^^^^^^^^

.. java:method:: protected ModelObject getModelParent()
   :outertype: ModelIcon

getName
^^^^^^^

.. java:method:: @Override public String getName()
   :outertype: ModelIcon

   :return: the name of the label

layoutChildren
^^^^^^^^^^^^^^

.. java:method:: @Override public void layoutChildren()
   :outertype: ModelIcon

modelUpdated
^^^^^^^^^^^^

.. java:method:: protected void modelUpdated()
   :outertype: ModelIcon

   Called when the NEO model has been updated

propertyChanged
^^^^^^^^^^^^^^^

.. java:method:: public void propertyChanged(Property event)
   :outertype: ModelIcon

setLabelVisible
^^^^^^^^^^^^^^^

.. java:method:: public void setLabelVisible(boolean isVisible)
   :outertype: ModelIcon

   :param isVisible: Whether the label is visible

updateLabel
^^^^^^^^^^^

.. java:method:: public void updateLabel()
   :outertype: ModelIcon

   Updates the label text


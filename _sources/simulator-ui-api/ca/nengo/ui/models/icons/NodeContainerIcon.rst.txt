.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives Text

.. java:import:: ca.nengo.ui.models.nodes UINodeViewable

NodeContainerIcon
=================

.. java:package:: ca.nengo.ui.models.icons
   :noindex:

.. java:type:: public abstract class NodeContainerIcon extends ModelIcon

   Icon for a Node Container. The size of this icon scales depending on the number of nodes contained by the model.

   :author: Shu

Fields
------
MAX_SCALE
^^^^^^^^^

.. java:field:: public static final float MAX_SCALE
   :outertype: NodeContainerIcon

MIN_SCALE
^^^^^^^^^

.. java:field:: public static final float MIN_SCALE
   :outertype: NodeContainerIcon

Constructors
------------
NodeContainerIcon
^^^^^^^^^^^^^^^^^

.. java:constructor:: public NodeContainerIcon(UINodeViewable parent, WorldObject icon)
   :outertype: NodeContainerIcon

Methods
-------
getModelParent
^^^^^^^^^^^^^^

.. java:method:: @Override public UINodeViewable getModelParent()
   :outertype: NodeContainerIcon

getNodeCountNormalization
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract int getNodeCountNormalization()
   :outertype: NodeContainerIcon

layoutChildren
^^^^^^^^^^^^^^

.. java:method:: @Override public void layoutChildren()
   :outertype: NodeContainerIcon

modelUpdated
^^^^^^^^^^^^

.. java:method:: @Override public void modelUpdated()
   :outertype: NodeContainerIcon


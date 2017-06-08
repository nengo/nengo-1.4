.. java:import:: ca.nengo.ui.lib AppFrame

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.world WorldLayer

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects SelectionBorder

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects TooltipWrapper

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects Window

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PiccoloNodeInWorld

.. java:import:: edu.umd.cs.piccolo PNode

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

TooltipPickHandler
==================

.. java:package:: ca.nengo.ui.lib.world.handlers
   :noindex:

.. java:type:: public class TooltipPickHandler extends AbstractPickHandler

   Picks objects in which to show tooltips for. Handles both both and keyboard events.

   :author: Shu Wu

Fields
------
TOOLTIP_BORDER_ATTR
^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final String TOOLTIP_BORDER_ATTR
   :outertype: TooltipPickHandler

Constructors
------------
TooltipPickHandler
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public TooltipPickHandler(WorldImpl world, int pickDelay, int keepPickDelay)
   :outertype: TooltipPickHandler

Methods
-------
getKeepPickDelay
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected int getKeepPickDelay()
   :outertype: TooltipPickHandler

getPickDelay
^^^^^^^^^^^^

.. java:method:: @Override protected int getPickDelay()
   :outertype: TooltipPickHandler

getTooltipNode
^^^^^^^^^^^^^^

.. java:method:: protected WorldObject getTooltipNode(PInputEvent event)
   :outertype: TooltipPickHandler

keyPressed
^^^^^^^^^^

.. java:method:: @Override public void keyPressed(PInputEvent event)
   :outertype: TooltipPickHandler

keyReleased
^^^^^^^^^^^

.. java:method:: @Override public void keyReleased(PInputEvent event)
   :outertype: TooltipPickHandler

nodePicked
^^^^^^^^^^

.. java:method:: @Override protected void nodePicked()
   :outertype: TooltipPickHandler

nodeUnPicked
^^^^^^^^^^^^

.. java:method:: @Override protected void nodeUnPicked()
   :outertype: TooltipPickHandler

processMouseEvent
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void processMouseEvent(PInputEvent event)
   :outertype: TooltipPickHandler

setKeyboardTooltipFocus
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void setKeyboardTooltipFocus(WorldObject wo)
   :outertype: TooltipPickHandler


.. java:import:: java.lang.reflect InvocationTargetException

.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldImpl

.. java:import:: edu.umd.cs.piccolo.event PBasicInputEventHandler

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

AbstractPickHandler
===================

.. java:package:: ca.nengo.ui.lib.world.handlers
   :noindex:

.. java:type:: public abstract class AbstractPickHandler extends PBasicInputEventHandler

   Abstract handler which picks and unpicks nodes with a delay

   :author: Shu Wu

Constructors
------------
AbstractPickHandler
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public AbstractPickHandler(WorldImpl parent)
   :outertype: AbstractPickHandler

Methods
-------
getKeepPickDelay
^^^^^^^^^^^^^^^^

.. java:method:: protected abstract int getKeepPickDelay()
   :outertype: AbstractPickHandler

getPickDelay
^^^^^^^^^^^^

.. java:method:: protected abstract int getPickDelay()
   :outertype: AbstractPickHandler

getPickedNode
^^^^^^^^^^^^^

.. java:method:: protected WorldObject getPickedNode()
   :outertype: AbstractPickHandler

getWorld
^^^^^^^^

.. java:method:: protected WorldImpl getWorld()
   :outertype: AbstractPickHandler

isKeepPickAlive
^^^^^^^^^^^^^^^

.. java:method:: public boolean isKeepPickAlive()
   :outertype: AbstractPickHandler

mouseDragged
^^^^^^^^^^^^

.. java:method:: @Override public void mouseDragged(PInputEvent event)
   :outertype: AbstractPickHandler

mouseMoved
^^^^^^^^^^

.. java:method:: @Override public void mouseMoved(PInputEvent event)
   :outertype: AbstractPickHandler

nodePicked
^^^^^^^^^^

.. java:method:: protected abstract void nodePicked()
   :outertype: AbstractPickHandler

nodeUnPicked
^^^^^^^^^^^^

.. java:method:: protected abstract void nodeUnPicked()
   :outertype: AbstractPickHandler

processEvent
^^^^^^^^^^^^

.. java:method:: @Override public void processEvent(PInputEvent event, int type)
   :outertype: AbstractPickHandler

processMouseEvent
^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract void processMouseEvent(PInputEvent event)
   :outertype: AbstractPickHandler

setKeepPickAlive
^^^^^^^^^^^^^^^^

.. java:method:: protected void setKeepPickAlive(boolean keepPickAlive)
   :outertype: AbstractPickHandler

setSelectedNode
^^^^^^^^^^^^^^^

.. java:method:: protected void setSelectedNode(WorldObject selectedNode)
   :outertype: AbstractPickHandler


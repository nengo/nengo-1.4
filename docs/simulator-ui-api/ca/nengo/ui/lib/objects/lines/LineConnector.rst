.. java:import:: java.util Collection

.. java:import:: javax.swing JPopupMenu

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.ui.lib.world DroppableX

.. java:import:: ca.nengo.ui.lib.world Interactable

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world WorldObject.Listener

.. java:import:: ca.nengo.ui.lib.world WorldObject.Property

.. java:import:: ca.nengo.ui.lib.world.elastic ElasticEdge

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldGroundImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PXEdge

.. java:import:: edu.umd.cs.piccolo.util PPaintContext

LineConnector
=============

.. java:package:: ca.nengo.ui.lib.objects.lines
   :noindex:

.. java:type:: public abstract class LineConnector extends WorldObjectImpl implements Interactable, DroppableX

   :author: Shu

Constructors
------------
LineConnector
^^^^^^^^^^^^^

.. java:constructor:: public LineConnector(LineWell well)
   :outertype: LineConnector

Methods
-------
altClicked
^^^^^^^^^^

.. java:method:: @Override public void altClicked()
   :outertype: LineConnector

disconnectFromTermination
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract void disconnectFromTermination()
   :outertype: LineConnector

   Called when the LineEnd is first disconnected from a Line end holder

droppedOnTargets
^^^^^^^^^^^^^^^^

.. java:method:: public void droppedOnTargets(Collection<WorldObject> targets)
   :outertype: LineConnector

getContextMenu
^^^^^^^^^^^^^^

.. java:method:: public JPopupMenu getContextMenu()
   :outertype: LineConnector

getEdge
^^^^^^^

.. java:method:: protected PXEdge getEdge()
   :outertype: LineConnector

getTermination
^^^^^^^^^^^^^^

.. java:method:: public ILineTermination getTermination()
   :outertype: LineConnector

getWell
^^^^^^^

.. java:method:: protected LineWell getWell()
   :outertype: LineConnector

initTarget
^^^^^^^^^^

.. java:method:: protected boolean initTarget(ILineTermination target, boolean modifyModel)
   :outertype: LineConnector

   :param target:
   :return: Whether the connection was successfully initialized

prepareForDestroy
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void prepareForDestroy()
   :outertype: LineConnector

setPointerVisible
^^^^^^^^^^^^^^^^^

.. java:method:: public void setPointerVisible(boolean visible)
   :outertype: LineConnector

   :param visible: Whether the edge associated with this LineEnd has it's direction pointer visible

tryConnectTo
^^^^^^^^^^^^

.. java:method:: public boolean tryConnectTo(ILineTermination newTermination, boolean modifyModel)
   :outertype: LineConnector


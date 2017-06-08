.. java:import:: java.awt.geom Point2D

.. java:import:: ca.nengo.ui.lib.misc WorldLayout

.. java:import:: ca.nengo.ui.lib.world World

.. java:import:: ca.nengo.ui.lib.world WorldObject

LayoutAction
============

.. java:package:: ca.nengo.ui.lib.actions
   :noindex:

.. java:type:: public abstract class LayoutAction extends ReversableAction

Constructors
------------
LayoutAction
^^^^^^^^^^^^

.. java:constructor:: public LayoutAction(World world, String description, String actionName)
   :outertype: LayoutAction

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: LayoutAction

applyLayout
^^^^^^^^^^^

.. java:method:: protected abstract void applyLayout()
   :outertype: LayoutAction

restoreNodePositions
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void restoreNodePositions()
   :outertype: LayoutAction

undo
^^^^

.. java:method:: @Override protected void undo() throws ActionException
   :outertype: LayoutAction


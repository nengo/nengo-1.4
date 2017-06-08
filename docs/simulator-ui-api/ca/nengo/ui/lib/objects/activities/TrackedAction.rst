.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: ca.nengo.ui NengoGraphics

TrackedAction
=============

.. java:package:: ca.nengo.ui.lib.objects.activities
   :noindex:

.. java:type:: public abstract class TrackedAction extends StandardAction

   An action which is tracked by the UI. Since tracked actions are slow and have UI messages associated with them, they do never execute inside the Swing dispatcher thread.

   :author: Shu Wu

Constructors
------------
TrackedAction
^^^^^^^^^^^^^

.. java:constructor:: public TrackedAction(String taskName)
   :outertype: TrackedAction

TrackedAction
^^^^^^^^^^^^^

.. java:constructor:: public TrackedAction(String taskName, WorldObjectImpl wo)
   :outertype: TrackedAction

Methods
-------
doAction
^^^^^^^^

.. java:method:: @Override public void doAction()
   :outertype: TrackedAction

doActionInternal
^^^^^^^^^^^^^^^^

.. java:method:: protected void doActionInternal()
   :outertype: TrackedAction

postAction
^^^^^^^^^^

.. java:method:: @Override protected void postAction()
   :outertype: TrackedAction


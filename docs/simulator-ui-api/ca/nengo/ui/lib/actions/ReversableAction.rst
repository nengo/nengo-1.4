.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util UserMessages

ReversableAction
================

.. java:package:: ca.nengo.ui.lib.actions
   :noindex:

.. java:type:: public abstract class ReversableAction extends StandardAction

   A reversable action than can be undone.

   :author: Shu

Constructors
------------
ReversableAction
^^^^^^^^^^^^^^^^

.. java:constructor:: public ReversableAction(String description)
   :outertype: ReversableAction

ReversableAction
^^^^^^^^^^^^^^^^

.. java:constructor:: public ReversableAction(String description, String actionName)
   :outertype: ReversableAction

ReversableAction
^^^^^^^^^^^^^^^^

.. java:constructor:: public ReversableAction(String description, String actionName, boolean isSwingAction)
   :outertype: ReversableAction

Methods
-------
finalizeAction
^^^^^^^^^^^^^^

.. java:method:: protected void finalizeAction()
   :outertype: ReversableAction

   This function is called if completing the action requires two stages. First stage does the action but it can still be undone (leaving some threads intact). This function is the second (optional stage). It completes the action in such a way that it cannot be undone.

isReversable
^^^^^^^^^^^^

.. java:method:: protected boolean isReversable()
   :outertype: ReversableAction

   :return: True, if this action is reversable

postAction
^^^^^^^^^^

.. java:method:: @Override protected void postAction()
   :outertype: ReversableAction

undo
^^^^

.. java:method:: protected abstract void undo() throws ActionException
   :outertype: ReversableAction

   Does the undo work

   :return: Whether the undo action was successful

undoAction
^^^^^^^^^^

.. java:method:: public void undoAction()
   :outertype: ReversableAction

   Undo the action


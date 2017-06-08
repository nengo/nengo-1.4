.. java:import:: java.util Vector

.. java:import:: ca.nengo.ui.lib AppFrame

.. java:import:: ca.nengo.ui.lib.util UserMessages

ReversableActionManager
=======================

.. java:package:: ca.nengo.ui.lib.actions
   :noindex:

.. java:type:: public class ReversableActionManager

   Manages reversable actions

   :author: Shu Wu

Fields
------
MAX_NUM_OF_UNDO_ACTIONS
^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: static final int MAX_NUM_OF_UNDO_ACTIONS
   :outertype: ReversableActionManager

   Max number of undo steps to reference

Constructors
------------
ReversableActionManager
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ReversableActionManager(AppFrame parent)
   :outertype: ReversableActionManager

   Create a new reversable action manager

   :param parent: Application parent of this manager

Methods
-------
addReversableAction
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void addReversableAction(ReversableAction action)
   :outertype: ReversableActionManager

   :param action: Action to add

canRedo
^^^^^^^

.. java:method:: public boolean canRedo()
   :outertype: ReversableActionManager

   :return: True, if an action can be redone

canUndo
^^^^^^^

.. java:method:: public boolean canUndo()
   :outertype: ReversableActionManager

   :return: True, if an action can be undone

getRedoActionDescription
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public String getRedoActionDescription()
   :outertype: ReversableActionManager

   :return: Description of the action that can be redone

getUndoActionDescription
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public String getUndoActionDescription()
   :outertype: ReversableActionManager

   :return: Description of the action that can be undone

redoAction
^^^^^^^^^^

.. java:method:: public void redoAction()
   :outertype: ReversableActionManager

   Redo the focused action

undoAction
^^^^^^^^^^

.. java:method:: public void undoAction()
   :outertype: ReversableActionManager

   Undo the focused action


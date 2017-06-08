.. java:import:: java.awt.event ActionEvent

.. java:import:: java.io Serializable

.. java:import:: javax.swing AbstractAction

.. java:import:: javax.swing Action

.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.lib.util UserMessages

StandardAction
==============

.. java:package:: ca.nengo.ui.lib.actions
   :noindex:

.. java:type:: public abstract class StandardAction implements Serializable

   A standard non-reversable action

   :author: Shu Wu

Fields
------
runSwingType
^^^^^^^^^^^^

.. java:field:: protected RunThreadType runSwingType
   :outertype: StandardAction

   If true, this action will execute inside the Swing Event dispatcher Thread. If false, the action can execute in any non-swing thread. The second type allows actions to proceed without blocking the UI.s

Constructors
------------
StandardAction
^^^^^^^^^^^^^^

.. java:constructor:: public StandardAction(String description)
   :outertype: StandardAction

   :param description: Description of the action

StandardAction
^^^^^^^^^^^^^^

.. java:constructor:: public StandardAction(String description, boolean isSwingAction)
   :outertype: StandardAction

StandardAction
^^^^^^^^^^^^^^

.. java:constructor:: public StandardAction(String description, String actionName)
   :outertype: StandardAction

StandardAction
^^^^^^^^^^^^^^

.. java:constructor:: public StandardAction(String description, String actionName, boolean isSwingAction)
   :outertype: StandardAction

   :param description: Description of the action
   :param actionName: Name to give to the Swing Action Object
   :param threadType:

Methods
-------
action
^^^^^^

.. java:method:: protected abstract void action() throws ActionException
   :outertype: StandardAction

   Does the work

   :return: Whether the action was successful

blockUntilCompleted
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void blockUntilCompleted()
   :outertype: StandardAction

   Blocks the calling thread until this action is completed.

doAction
^^^^^^^^

.. java:method:: public void doAction()
   :outertype: StandardAction

   Does the action layer, starts an appropriate thread

doActionInternal
^^^^^^^^^^^^^^^^

.. java:method:: protected void doActionInternal()
   :outertype: StandardAction

   Does the action

doSomething
^^^^^^^^^^^

.. java:method:: protected void doSomething(boolean isUndo)
   :outertype: StandardAction

getActionName
^^^^^^^^^^^^^

.. java:method:: protected String getActionName()
   :outertype: StandardAction

   :return: Name of the action

getDescription
^^^^^^^^^^^^^^

.. java:method:: public String getDescription()
   :outertype: StandardAction

   :return: Description of the action.

isActionCompleted
^^^^^^^^^^^^^^^^^

.. java:method:: protected boolean isActionCompleted()
   :outertype: StandardAction

   :return: Whether the action successfully completed

isEnabled
^^^^^^^^^

.. java:method:: public boolean isEnabled()
   :outertype: StandardAction

   :return: Whether this action is enabled

postAction
^^^^^^^^^^

.. java:method:: protected void postAction()
   :outertype: StandardAction

   An subclass may put something here to do after an action has completed successfully

setActionCompleted
^^^^^^^^^^^^^^^^^^

.. java:method:: protected void setActionCompleted(boolean actionCompleted)
   :outertype: StandardAction

setEnabled
^^^^^^^^^^

.. java:method:: public void setEnabled(boolean isEnabled)
   :outertype: StandardAction

   :param isEnabled: True, if this action is enabled

toSwingAction
^^^^^^^^^^^^^

.. java:method:: public Action toSwingAction()
   :outertype: StandardAction

   :return: Swing-type action, which can be used in Swing components


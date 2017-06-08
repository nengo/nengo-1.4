.. java:import:: java.util ArrayList

.. java:import:: javax.swing JOptionPane

.. java:import:: ca.nengo.ui.lib.actions UserCancelledException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.objects.models ModelObject

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

RemoveModelAction
=================

.. java:package:: ca.nengo.ui.actions
   :noindex:

.. java:type:: public class RemoveModelAction extends StandardAction

   Action for removing this UI Wrapper and the model

   :author: Shu Wu

Fields
------
modelsToRemove
^^^^^^^^^^^^^^

.. java:field::  ArrayList<ModelObject> modelsToRemove
   :outertype: RemoveModelAction

Constructors
------------
RemoveModelAction
^^^^^^^^^^^^^^^^^

.. java:constructor:: public RemoveModelAction(String actionName, ArrayList<ModelObject> modelsToRemove)
   :outertype: RemoveModelAction

   :param actionName: TODO
   :param modelToRemove: TODO

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws UserCancelledException
   :outertype: RemoveModelAction


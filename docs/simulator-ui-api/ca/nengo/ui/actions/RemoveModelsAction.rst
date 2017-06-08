.. java:import:: java.util Collection

.. java:import:: javax.swing JOptionPane

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.objects.models ModelObject

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

RemoveModelsAction
==================

.. java:package:: ca.nengo.ui.actions
   :noindex:

.. java:type:: public class RemoveModelsAction extends StandardAction

   Action for removing a collection of UI Wrappers and their models

   :author: Shu Wu

Constructors
------------
RemoveModelsAction
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public RemoveModelsAction(Collection<ModelObject> objectsToRemove)
   :outertype: RemoveModelsAction

   :param objectsToRemove: TODO

RemoveModelsAction
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public RemoveModelsAction(Collection<ModelObject> objectsToRemove, String typeName, boolean showWarning)
   :outertype: RemoveModelsAction

   :param objectsToRemove: TODO
   :param typeName: TODO
   :param showWarning: TODO

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: RemoveModelsAction


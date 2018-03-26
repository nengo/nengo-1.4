.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.world WorldObject

RemoveObjectAction
==================

.. java:package:: ca.nengo.ui.actions
   :noindex:

.. java:type:: public class RemoveObjectAction extends StandardAction

   Action for removing this UI Wrapper and the model

   :author: Shu Wu

Fields
------
objectToRemove
^^^^^^^^^^^^^^

.. java:field::  WorldObject objectToRemove
   :outertype: RemoveObjectAction

Constructors
------------
RemoveObjectAction
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public RemoveObjectAction(String actionName, WorldObject objectToRemove)
   :outertype: RemoveObjectAction

   :param actionName: TODO
   :param objectToRemove: TODO

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: RemoveObjectAction


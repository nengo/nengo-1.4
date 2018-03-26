.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives Text

TrackedStatusMsg
================

.. java:package:: ca.nengo.ui.lib.objects.activities
   :noindex:

.. java:type:: public class TrackedStatusMsg

   Displays and removes a task message from the application status bar

   :author: Shu Wu

Fields
------
taskText
^^^^^^^^

.. java:field::  Text taskText
   :outertype: TrackedStatusMsg

Constructors
------------
TrackedStatusMsg
^^^^^^^^^^^^^^^^

.. java:constructor:: public TrackedStatusMsg(String taskName)
   :outertype: TrackedStatusMsg

TrackedStatusMsg
^^^^^^^^^^^^^^^^

.. java:constructor:: public TrackedStatusMsg(String taskName, WorldObjectImpl wo)
   :outertype: TrackedStatusMsg

Methods
-------
finished
^^^^^^^^

.. java:method:: public void finished()
   :outertype: TrackedStatusMsg

   Removes the task message from the application status bar.

getTaskName
^^^^^^^^^^^

.. java:method:: protected String getTaskName()
   :outertype: TrackedStatusMsg

setTaskName
^^^^^^^^^^^

.. java:method:: protected void setTaskName(String taskName)
   :outertype: TrackedStatusMsg


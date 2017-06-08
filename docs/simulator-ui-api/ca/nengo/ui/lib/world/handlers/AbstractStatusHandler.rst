.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldImpl

.. java:import:: edu.umd.cs.piccolo.event PBasicInputEventHandler

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

AbstractStatusHandler
=====================

.. java:package:: ca.nengo.ui.lib.world.handlers
   :noindex:

.. java:type:: public abstract class AbstractStatusHandler extends PBasicInputEventHandler

   Handles events which change the Application status bar

   :author: Shu Wu

Constructors
------------
AbstractStatusHandler
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public AbstractStatusHandler(WorldImpl world)
   :outertype: AbstractStatusHandler

   :param world: World this handler belongs to

Methods
-------
getStatusMessage
^^^^^^^^^^^^^^^^

.. java:method:: protected abstract String getStatusMessage(PInputEvent event)
   :outertype: AbstractStatusHandler

   :param event: Input event
   :return: Message to show on the status bar

getWorld
^^^^^^^^

.. java:method:: protected WorldImpl getWorld()
   :outertype: AbstractStatusHandler

   :return: World this handler belongs to

mouseMoved
^^^^^^^^^^

.. java:method:: @Override public void mouseMoved(PInputEvent event)
   :outertype: AbstractStatusHandler


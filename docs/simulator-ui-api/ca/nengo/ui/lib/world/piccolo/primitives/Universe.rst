.. java:import:: java.util ArrayList

.. java:import:: java.util Collection

.. java:import:: java.util LinkedList

.. java:import:: java.util List

.. java:import:: java.util Vector

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.world Destroyable

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world WorldObject.ChildListener

.. java:import:: ca.nengo.ui.lib.world WorldObject.Listener

.. java:import:: ca.nengo.ui.lib.world WorldObject.Property

.. java:import:: ca.nengo.ui.lib.world.elastic ElasticWorld

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects Window

.. java:import:: edu.umd.cs.piccolo PCanvas

.. java:import:: edu.umd.cs.piccolo PLayer

.. java:import:: edu.umd.cs.piccolo.event PBasicInputEventHandler

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

Universe
========

.. java:package:: ca.nengo.ui.lib.world.piccolo.primitives
   :noindex:

.. java:type:: public class Universe extends PCanvas implements Destroyable

   Holder of worlds

   :author: Shu Wu

Fields
------
CLICK_ZOOM_PADDING
^^^^^^^^^^^^^^^^^^

.. java:field:: static final double CLICK_ZOOM_PADDING
   :outertype: Universe

SELECTION_MODE_NOTIFICATION
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final String SELECTION_MODE_NOTIFICATION
   :outertype: Universe

TEXT_PADDING
^^^^^^^^^^^^

.. java:field:: static final double TEXT_PADDING
   :outertype: Universe

Constructors
------------
Universe
^^^^^^^^

.. java:constructor:: public Universe()
   :outertype: Universe

Methods
-------
addTaskStatusMsg
^^^^^^^^^^^^^^^^

.. java:method:: public String addTaskStatusMsg(String message)
   :outertype: Universe

   :param message: Task related status message to remove from the status bar
   :return: status message

addWorld
^^^^^^^^

.. java:method:: public void addWorld(WorldImpl world)
   :outertype: Universe

destroy
^^^^^^^

.. java:method:: public void destroy()
   :outertype: Universe

getWorld
^^^^^^^^

.. java:method:: public ElasticWorld getWorld()
   :outertype: Universe

getWorldWindows
^^^^^^^^^^^^^^^

.. java:method:: public List<Window> getWorldWindows()
   :outertype: Universe

getWorlds
^^^^^^^^^

.. java:method:: public Collection<WorldImpl> getWorlds()
   :outertype: Universe

initialize
^^^^^^^^^^

.. java:method:: public void initialize(ElasticWorld world)
   :outertype: Universe

   :param world: World to be the background for it all

isSelectionMode
^^^^^^^^^^^^^^^

.. java:method:: public boolean isSelectionMode()
   :outertype: Universe

   Checks whether the UI is in selection or navigation mode.

   :return: If true, selection mode is enabled. If false, navigation mode is enabled.

layoutText
^^^^^^^^^^

.. java:method:: protected void layoutText()
   :outertype: Universe

removeTaskStatusMsg
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeTaskStatusMsg(String message)
   :outertype: Universe

   :param message: Task-related status message to add to the status bar

removeWorld
^^^^^^^^^^^

.. java:method:: public void removeWorld(WorldImpl world)
   :outertype: Universe

setBounds
^^^^^^^^^

.. java:method:: @Override public void setBounds(int x, int y, int w, int h)
   :outertype: Universe

setSelectionMode
^^^^^^^^^^^^^^^^

.. java:method:: public void setSelectionMode(boolean enabled)
   :outertype: Universe

   :param enabled: True if selection mode is enabled, False if navigation

setStatusMessage
^^^^^^^^^^^^^^^^

.. java:method:: public void setStatusMessage(String message)
   :outertype: Universe

   :param message: Sets the text of the status bar in the UI

updateTaskMessages
^^^^^^^^^^^^^^^^^^

.. java:method:: protected void updateTaskMessages()
   :outertype: Universe

   Updates task-related messages in the status bar


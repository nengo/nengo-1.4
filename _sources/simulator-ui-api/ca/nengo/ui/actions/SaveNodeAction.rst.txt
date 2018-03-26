.. java:import:: java.io File

.. java:import:: java.io IOException

.. java:import:: javax.swing JFileChooser

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.actions UserCancelledException

.. java:import:: ca.nengo.ui.lib.objects.activities TrackedAction

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.models UINeoNode

SaveNodeAction
==============

.. java:package:: ca.nengo.ui.actions
   :noindex:

.. java:type:: public class SaveNodeAction extends StandardAction

   Saves a nodeUI

   :author: Shu Wu

Constructors
------------
SaveNodeAction
^^^^^^^^^^^^^^

.. java:constructor:: public SaveNodeAction(UINeoNode nodeUI)
   :outertype: SaveNodeAction

   :param nodeUI: Node to be saved

SaveNodeAction
^^^^^^^^^^^^^^

.. java:constructor:: public SaveNodeAction(UINeoNode nodeUI, boolean isBlocking)
   :outertype: SaveNodeAction

   :param nodeUI: Node to be saved
   :param isBlocking: Whether or not the save should be blocking. A blocking save will perform the save before returning, rather than saving in a separate thread. This is useful on exit, when we want to be sure the save has finished before leaving Nengo.

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: SaveNodeAction

getSaveSuccessful
^^^^^^^^^^^^^^^^^

.. java:method:: public boolean getSaveSuccessful()
   :outertype: SaveNodeAction

   :return: TODO


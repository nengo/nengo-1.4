.. java:import:: javax.swing JPopupMenu

Interactable
============

.. java:package:: ca.nengo.ui.lib.world
   :noindex:

.. java:type:: public interface Interactable extends WorldObject

   Objects which can be interacted with through context menus

   :author: Shu

Methods
-------
getContextMenu
^^^^^^^^^^^^^^

.. java:method:: public JPopupMenu getContextMenu()
   :outertype: Interactable

   :param event: The input event triggering the context menu
   :return: context menu associated to the Named Object


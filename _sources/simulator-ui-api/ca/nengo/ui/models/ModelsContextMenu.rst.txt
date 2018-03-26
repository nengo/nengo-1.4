.. java:import:: java.util Collection

.. java:import:: java.util HashMap

.. java:import:: java.util LinkedList

.. java:import:: javax.swing JPopupMenu

.. java:import:: ca.nengo.ui.lib.objects.models ModelObject

.. java:import:: ca.nengo.ui.lib.util.menus AbstractMenuBuilder

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.ui.lib.world WorldObject

ModelsContextMenu
=================

.. java:package:: ca.nengo.ui.models
   :noindex:

.. java:type:: public class ModelsContextMenu

   Creates a Popup menu which applies to a collection of models

   :author: Shu Wu

Constructors
------------
ModelsContextMenu
^^^^^^^^^^^^^^^^^

.. java:constructor:: protected ModelsContextMenu(PopupMenuBuilder menuBuilder, Collection<ModelObject> models)
   :outertype: ModelsContextMenu

Methods
-------
constructMenu
^^^^^^^^^^^^^

.. java:method:: public static void constructMenu(PopupMenuBuilder menuBuilder, Collection<ModelObject> selectedObjects)
   :outertype: ModelsContextMenu

   :param selectedObjects: Selected objects which a popup menu is created for
   :return: Context menu for selected objects

constructMenu
^^^^^^^^^^^^^

.. java:method:: protected void constructMenu()
   :outertype: ModelsContextMenu


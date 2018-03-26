.. java:import:: java.awt Component

.. java:import:: java.awt Font

.. java:import:: javax.swing ButtonGroup

.. java:import:: javax.swing JLabel

.. java:import:: javax.swing JMenuItem

.. java:import:: javax.swing JPopupMenu

.. java:import:: javax.swing JRadioButtonMenuItem

.. java:import:: javax.swing JSeparator

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.actions StandardAction

PopupMenuBuilder
================

.. java:package:: ca.nengo.ui.lib.util.menus
   :noindex:

.. java:type:: public class PopupMenuBuilder extends AbstractMenuBuilder

   Used to build a popup menu. The created menu can later be converted to a Swing component.

   :author: Shu Wu

Fields
------
isFirstSection
^^^^^^^^^^^^^^

.. java:field::  boolean isFirstSection
   :outertype: PopupMenuBuilder

menu
^^^^

.. java:field::  JPopupMenu menu
   :outertype: PopupMenuBuilder

Constructors
------------
PopupMenuBuilder
^^^^^^^^^^^^^^^^

.. java:constructor:: public PopupMenuBuilder(String label)
   :outertype: PopupMenuBuilder

Methods
-------
addAction
^^^^^^^^^

.. java:method:: @Override public void addAction(StandardAction standardAction)
   :outertype: PopupMenuBuilder

addAction
^^^^^^^^^

.. java:method:: public void addAction(StandardAction standardAction, int index)
   :outertype: PopupMenuBuilder

addAction
^^^^^^^^^

.. java:method:: public void addAction(String section, StandardAction action)
   :outertype: PopupMenuBuilder

addActionsRadio
^^^^^^^^^^^^^^^

.. java:method:: public void addActionsRadio(StandardAction[] actions, int selected)
   :outertype: PopupMenuBuilder

addLabel
^^^^^^^^

.. java:method:: @Override public void addLabel(String msg)
   :outertype: PopupMenuBuilder

addSection
^^^^^^^^^^

.. java:method:: public void addSection(String name)
   :outertype: PopupMenuBuilder

addSection
^^^^^^^^^^

.. java:method:: public void addSection(String name, Font fontStyle)
   :outertype: PopupMenuBuilder

   Creates a new section in the Popup menu

   :param name: The name of the new section
   :param fontStyle: Style of font for the subsection label

addSubMenu
^^^^^^^^^^

.. java:method:: @Override public MenuBuilder addSubMenu(String label)
   :outertype: PopupMenuBuilder

toJPopupMenu
^^^^^^^^^^^^

.. java:method:: public JPopupMenu toJPopupMenu()
   :outertype: PopupMenuBuilder

   :return: Swing component


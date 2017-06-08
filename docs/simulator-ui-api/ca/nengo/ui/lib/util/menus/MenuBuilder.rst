.. java:import:: java.awt Font

.. java:import:: javax.swing ButtonGroup

.. java:import:: javax.swing JLabel

.. java:import:: javax.swing JMenu

.. java:import:: javax.swing JMenuItem

.. java:import:: javax.swing JRadioButtonMenuItem

.. java:import:: javax.swing JSeparator

.. java:import:: javax.swing KeyStroke

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.actions StandardAction

MenuBuilder
===========

.. java:package:: ca.nengo.ui.lib.util.menus
   :noindex:

.. java:type:: public class MenuBuilder extends AbstractMenuBuilder

Fields
------
isFirstSection
^^^^^^^^^^^^^^

.. java:field::  boolean isFirstSection
   :outertype: MenuBuilder

menu
^^^^

.. java:field::  JMenu menu
   :outertype: MenuBuilder

Constructors
------------
MenuBuilder
^^^^^^^^^^^

.. java:constructor:: public MenuBuilder(String label)
   :outertype: MenuBuilder

MenuBuilder
^^^^^^^^^^^

.. java:constructor:: public MenuBuilder(String label, boolean applyCustomStyle)
   :outertype: MenuBuilder

Methods
-------
addAction
^^^^^^^^^

.. java:method:: @Override public void addAction(StandardAction action)
   :outertype: MenuBuilder

addAction
^^^^^^^^^

.. java:method:: public void addAction(StandardAction action, KeyStroke accelerator)
   :outertype: MenuBuilder

addAction
^^^^^^^^^

.. java:method:: public void addAction(StandardAction action, int mnemonic, KeyStroke accelerator)
   :outertype: MenuBuilder

addAction
^^^^^^^^^

.. java:method:: public void addAction(StandardAction action, int mnemonic)
   :outertype: MenuBuilder

   :param action: Action to add
   :param mnemonic: Mnemonic to assign to this action

addActionsRadio
^^^^^^^^^^^^^^^

.. java:method:: public void addActionsRadio(StandardAction[] actions, int selected)
   :outertype: MenuBuilder

addLabel
^^^^^^^^

.. java:method:: @Override public void addLabel(String msg)
   :outertype: MenuBuilder

addSection
^^^^^^^^^^

.. java:method:: public void addSection(String name)
   :outertype: MenuBuilder

addSection
^^^^^^^^^^

.. java:method:: public void addSection(String name, Font fontStyle)
   :outertype: MenuBuilder

   Creates a new section in the Popup menu

   :param name: the name of the new section
   :param fontStyle: style of font for the subsection label

addSubMenu
^^^^^^^^^^

.. java:method:: @Override public MenuBuilder addSubMenu(String label)
   :outertype: MenuBuilder

getJMenu
^^^^^^^^

.. java:method:: public JMenu getJMenu()
   :outertype: MenuBuilder

reset
^^^^^

.. java:method:: public void reset()
   :outertype: MenuBuilder

   removes all elements to start over


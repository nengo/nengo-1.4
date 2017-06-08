.. java:import:: javax.swing JComponent

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.actions StandardAction

AbstractMenuBuilder
===================

.. java:package:: ca.nengo.ui.lib.util.menus
   :noindex:

.. java:type:: public abstract class AbstractMenuBuilder

   Used to build a menu. The created menu can later be converted to a Swing component.

   :author: Shu

Constructors
------------
AbstractMenuBuilder
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public AbstractMenuBuilder(boolean applyCustomStyle)
   :outertype: AbstractMenuBuilder

Methods
-------
addAction
^^^^^^^^^

.. java:method:: public abstract void addAction(StandardAction action)
   :outertype: AbstractMenuBuilder

addActionsRadio
^^^^^^^^^^^^^^^

.. java:method:: public abstract void addActionsRadio(StandardAction[] actions, int selected)
   :outertype: AbstractMenuBuilder

addLabel
^^^^^^^^

.. java:method:: public abstract void addLabel(String msg)
   :outertype: AbstractMenuBuilder

addSection
^^^^^^^^^^

.. java:method:: public abstract void addSection(String name)
   :outertype: AbstractMenuBuilder

addSubMenu
^^^^^^^^^^

.. java:method:: public abstract AbstractMenuBuilder addSubMenu(String label)
   :outertype: AbstractMenuBuilder

isCustomStyle
^^^^^^^^^^^^^

.. java:method:: public boolean isCustomStyle()
   :outertype: AbstractMenuBuilder

style
^^^^^

.. java:method:: public void style(JComponent item)
   :outertype: AbstractMenuBuilder

style
^^^^^

.. java:method:: public void style(JComponent item, boolean isTitle)
   :outertype: AbstractMenuBuilder


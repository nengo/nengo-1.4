.. java:import:: java.awt Color

.. java:import:: javax.swing JOptionPane

.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions ReversableAction

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.actions UserCancelledException

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.lib.util.menus AbstractMenuBuilder

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.ui.models UINeoModel

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.nodes UINetwork

.. java:import:: ca.nengo.ui.models.tooltips TooltipBuilder

.. java:import:: edu.umd.cs.piccolo.nodes PText

Widget.ShowWidgetAction
=======================

.. java:package:: ca.nengo.ui.models.nodes.widgets
   :noindex:

.. java:type::  class ShowWidgetAction extends ReversableAction
   :outertype: Widget

   Action for showing this widget

   :author: Shu Wu

Constructors
------------
ShowWidgetAction
^^^^^^^^^^^^^^^^

.. java:constructor:: public ShowWidgetAction(String actionName)
   :outertype: Widget.ShowWidgetAction

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: Widget.ShowWidgetAction

undo
^^^^

.. java:method:: @Override protected void undo() throws ActionException
   :outertype: Widget.ShowWidgetAction


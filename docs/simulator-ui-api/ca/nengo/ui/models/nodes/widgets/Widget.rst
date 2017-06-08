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

Widget
======

.. java:package:: ca.nengo.ui.models.nodes.widgets
   :noindex:

.. java:type:: public abstract class Widget extends UINeoModel

   Widgets are models such as Terminations and Origins which can be attached to a PNeoNode

   :author: Shu Wu

Fields
------
EXPOSED_COLOR
^^^^^^^^^^^^^

.. java:field:: public static final Color EXPOSED_COLOR
   :outertype: Widget

Constructors
------------
Widget
^^^^^^

.. java:constructor:: public Widget(UINeoNode nodeParent, Object model)
   :outertype: Widget

Methods
-------
constructMenu
^^^^^^^^^^^^^

.. java:method:: @Override protected void constructMenu(PopupMenuBuilder menu)
   :outertype: Widget

constructTooltips
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void constructTooltips(TooltipBuilder tooltips)
   :outertype: Widget

constructWidgetMenu
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void constructWidgetMenu(AbstractMenuBuilder menu)
   :outertype: Widget

   Constructs widget-specific menu

   :param menu:

expose
^^^^^^

.. java:method:: protected void expose(String exposedName)
   :outertype: Widget

   Exposes this origin/termination outside the Network

   :param exposedName: Name of the newly exposed origin/termination

exposeModel
^^^^^^^^^^^

.. java:method:: protected abstract void exposeModel(UINetwork networkUI, String exposedName)
   :outertype: Widget

getColor
^^^^^^^^

.. java:method:: public abstract Color getColor()
   :outertype: Widget

getExposedName
^^^^^^^^^^^^^^

.. java:method:: protected String getExposedName()
   :outertype: Widget

getExposedName
^^^^^^^^^^^^^^

.. java:method:: protected abstract String getExposedName(Network network)
   :outertype: Widget

getModelName
^^^^^^^^^^^^

.. java:method:: protected abstract String getModelName()
   :outertype: Widget

getNodeParent
^^^^^^^^^^^^^

.. java:method:: public UINeoNode getNodeParent()
   :outertype: Widget

isWidgetVisible
^^^^^^^^^^^^^^^

.. java:method:: public boolean isWidgetVisible()
   :outertype: Widget

   :return: Whether this widget is visible on the parent

modelUpdated
^^^^^^^^^^^^

.. java:method:: @Override public void modelUpdated()
   :outertype: Widget

setExposed
^^^^^^^^^^

.. java:method:: protected abstract void setExposed(boolean isExposed)
   :outertype: Widget

setWidgetVisible
^^^^^^^^^^^^^^^^

.. java:method:: public void setWidgetVisible(boolean isVisible)
   :outertype: Widget

   :param isVisible: Whether the user has marked this widget as hidden

unExpose
^^^^^^^^

.. java:method:: protected void unExpose()
   :outertype: Widget

   UnExposes this origin/termination outside the Network

unExpose
^^^^^^^^

.. java:method:: protected abstract void unExpose(Network network)
   :outertype: Widget


.. java:import:: java.awt Dimension

.. java:import:: java.lang.reflect Constructor

.. java:import:: java.lang.reflect InvocationTargetException

.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable.descriptors PInt

.. java:import:: ca.nengo.ui.configurable.managers ConfigManager

.. java:import:: ca.nengo.ui.configurable.managers ConfigManager.ConfigMode

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions LayoutAction

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.objects.activities TrackedAction

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldSkyImpl

.. java:import:: edu.uci.ics.jung.graph Graph

.. java:import:: edu.uci.ics.jung.visualization Layout

ElasticWorld.DoJungLayout
=========================

.. java:package:: ca.nengo.ui.lib.world.elastic
   :noindex:

.. java:type::  class DoJungLayout extends TrackedAction
   :outertype: ElasticWorld

   Activity for performing a Jung Layout.

   :author: Shu Wu

Constructors
------------
DoJungLayout
^^^^^^^^^^^^

.. java:constructor:: public DoJungLayout(Class<? extends Layout> layoutType)
   :outertype: ElasticWorld.DoJungLayout

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: ElasticWorld.DoJungLayout


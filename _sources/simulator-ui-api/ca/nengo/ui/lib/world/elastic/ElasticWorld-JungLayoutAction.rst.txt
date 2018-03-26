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

ElasticWorld.JungLayoutAction
=============================

.. java:package:: ca.nengo.ui.lib.world.elastic
   :noindex:

.. java:type::  class JungLayoutAction extends LayoutAction
   :outertype: ElasticWorld

   Action for applying a Jung Layout. It implements LayoutAction, which allows it to be reversable.

   :author: Shu

Fields
------
layoutClass
^^^^^^^^^^^

.. java:field::  Class<? extends Layout> layoutClass
   :outertype: ElasticWorld.JungLayoutAction

Constructors
------------
JungLayoutAction
^^^^^^^^^^^^^^^^

.. java:constructor:: public JungLayoutAction(Class<? extends Layout> layoutClass, String name)
   :outertype: ElasticWorld.JungLayoutAction

Methods
-------
applyLayout
^^^^^^^^^^^

.. java:method:: @Override protected void applyLayout()
   :outertype: ElasticWorld.JungLayoutAction


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

ElasticWorld
============

.. java:package:: ca.nengo.ui.lib.world.elastic
   :noindex:

.. java:type:: public class ElasticWorld extends WorldImpl

   A World which supports Spring layout. Objects within this world attract and repel each other

   :author: Shu Wu

Constructors
------------
ElasticWorld
^^^^^^^^^^^^

.. java:constructor:: public ElasticWorld(String name)
   :outertype: ElasticWorld

ElasticWorld
^^^^^^^^^^^^

.. java:constructor:: public ElasticWorld(String name, ElasticGround ground)
   :outertype: ElasticWorld

ElasticWorld
^^^^^^^^^^^^

.. java:constructor:: public ElasticWorld(String name, WorldSkyImpl sky, ElasticGround ground)
   :outertype: ElasticWorld

Methods
-------
applyJungLayout
^^^^^^^^^^^^^^^

.. java:method:: protected void applyJungLayout(Class<? extends Layout> layoutType)
   :outertype: ElasticWorld

constructMenu
^^^^^^^^^^^^^

.. java:method:: @Override protected void constructMenu(PopupMenuBuilder menu, Double posX, Double posY)
   :outertype: ElasticWorld

constructMenu
^^^^^^^^^^^^^

.. java:method:: protected void constructMenu(PopupMenuBuilder menu)
   :outertype: ElasticWorld

doFeedForwardLayout
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void doFeedForwardLayout()
   :outertype: ElasticWorld

   Creates the layout context menu

   :param menu: menu builder

getGround
^^^^^^^^^

.. java:method:: @Override public ElasticGround getGround()
   :outertype: ElasticWorld

getLayoutBounds
^^^^^^^^^^^^^^^

.. java:method:: protected Dimension getLayoutBounds()
   :outertype: ElasticWorld

   :return: Layout bounds to be used by Layout algorithms

setLayoutBounds
^^^^^^^^^^^^^^^

.. java:method:: public void setLayoutBounds(Dimension bounds)
   :outertype: ElasticWorld

   :param bounds: New bounds


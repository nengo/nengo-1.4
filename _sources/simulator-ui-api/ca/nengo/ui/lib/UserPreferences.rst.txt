.. java:import:: java.awt BorderLayout

.. java:import:: java.awt Container

.. java:import:: java.awt Dimension

.. java:import:: java.awt DisplayMode

.. java:import:: java.awt GraphicsDevice

.. java:import:: java.awt GraphicsEnvironment

.. java:import:: java.awt KeyEventDispatcher

.. java:import:: java.awt Rectangle

.. java:import:: java.awt Toolkit

.. java:import:: java.awt.event KeyAdapter

.. java:import:: java.awt.event KeyEvent

.. java:import:: java.awt.event KeyListener

.. java:import:: java.awt.event WindowEvent

.. java:import:: java.awt.event WindowListener

.. java:import:: java.io ByteArrayOutputStream

.. java:import:: java.io File

.. java:import:: java.io FileInputStream

.. java:import:: java.io FileOutputStream

.. java:import:: java.io IOException

.. java:import:: java.io ObjectInputStream

.. java:import:: java.io ObjectOutputStream

.. java:import:: java.io Serializable

.. java:import:: java.lang.reflect InvocationTargetException

.. java:import:: java.util ArrayList

.. java:import:: java.util Collection

.. java:import:: java.util EventListener

.. java:import:: java.util Iterator

.. java:import:: java.util LinkedList

.. java:import:: javax.swing FocusManager

.. java:import:: javax.swing JEditorPane

.. java:import:: javax.swing JFrame

.. java:import:: javax.swing JLabel

.. java:import:: javax.swing JMenuBar

.. java:import:: javax.swing JOptionPane

.. java:import:: javax.swing KeyStroke

.. java:import:: javax.swing SwingUtilities

.. java:import:: javax.swing.event HyperlinkEvent

.. java:import:: javax.swing.event HyperlinkListener

.. java:import:: org.simplericity.macify.eawt ApplicationEvent

.. java:import:: org.simplericity.macify.eawt ApplicationListener

.. java:import:: ca.nengo.plot Plotter

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions ExitAction

.. java:import:: ca.nengo.ui.lib.actions OpenURLAction

.. java:import:: ca.nengo.ui.lib.actions ReversableActionManager

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.actions ZoomToFitAction

.. java:import:: ca.nengo.ui.lib.misc ShortcutKey

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util.menus MenuBuilder

.. java:import:: ca.nengo.ui.lib.world World

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.elastic ElasticWorld

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects Window

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PXGrid

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives Universe

.. java:import:: edu.umd.cs.piccolo PCamera

.. java:import:: edu.umd.cs.piccolo.activities PActivity

.. java:import:: edu.umd.cs.piccolo.util PDebug

.. java:import:: edu.umd.cs.piccolo.util PPaintContext

.. java:import:: edu.umd.cs.piccolo.util PUtil

UserPreferences
===============

.. java:package:: ca.nengo.ui.lib
   :noindex:

.. java:type::  class UserPreferences implements Serializable

   :author: Shu

Methods
-------
apply
^^^^^

.. java:method:: public void apply(AppFrame applyTo)
   :outertype: UserPreferences

   Applies preferences

   :param applyTo: The application in which to apply the preferences to

isEnableTooltips
^^^^^^^^^^^^^^^^

.. java:method:: public boolean isEnableTooltips()
   :outertype: UserPreferences

isGridVisible
^^^^^^^^^^^^^

.. java:method:: public boolean isGridVisible()
   :outertype: UserPreferences

isWelcomeScreen
^^^^^^^^^^^^^^^

.. java:method:: public boolean isWelcomeScreen()
   :outertype: UserPreferences

setEnableTooltips
^^^^^^^^^^^^^^^^^

.. java:method:: public void setEnableTooltips(boolean enableTooltips)
   :outertype: UserPreferences

setGridVisible
^^^^^^^^^^^^^^

.. java:method:: public void setGridVisible(boolean gridVisible)
   :outertype: UserPreferences

setWelcomeScreen
^^^^^^^^^^^^^^^^

.. java:method:: public void setWelcomeScreen(boolean isWelcomeScreen)
   :outertype: UserPreferences


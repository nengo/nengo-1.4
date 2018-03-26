.. java:import:: java.awt BorderLayout

.. java:import:: java.awt Container

.. java:import:: java.awt Dimension

.. java:import:: java.awt Image

.. java:import:: java.awt.event KeyEvent

.. java:import:: java.awt.geom Point2D

.. java:import:: java.awt.image BufferedImage

.. java:import:: java.io File

.. java:import:: java.io IOException

.. java:import:: java.util ArrayList

.. java:import:: java.util Collection

.. java:import:: java.util LinkedList

.. java:import:: javax.imageio ImageIO

.. java:import:: javax.swing JFrame

.. java:import:: javax.swing JMenuBar

.. java:import:: javax.swing JOptionPane

.. java:import:: javax.swing JPanel

.. java:import:: javax.swing JScrollPane

.. java:import:: javax.swing JToolBar

.. java:import:: javax.swing KeyStroke

.. java:import:: javax.swing ScrollPaneConstants

.. java:import:: javax.swing UIManager

.. java:import:: javax.swing UnsupportedLookAndFeelException

.. java:import:: org.python.util PythonInterpreter

.. java:import:: org.simplericity.macify.eawt Application

.. java:import:: ca.nengo.config ConfigUtil

.. java:import:: ca.nengo.config JavaSourceParser

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.ui.actions ClearAllAction

.. java:import:: ca.nengo.ui.actions CopyAction

.. java:import:: ca.nengo.ui.actions CreateModelAction

.. java:import:: ca.nengo.ui.actions CutAction

.. java:import:: ca.nengo.ui.actions GeneratePDFAction

.. java:import:: ca.nengo.ui.actions GeneratePythonScriptAction

.. java:import:: ca.nengo.ui.actions OpenNeoFileAction

.. java:import:: ca.nengo.ui.actions PasteAction

.. java:import:: ca.nengo.ui.actions RemoveModelAction

.. java:import:: ca.nengo.ui.actions RunInteractivePlotsAction

.. java:import:: ca.nengo.ui.actions RunSimulatorAction

.. java:import:: ca.nengo.ui.actions SaveNodeAction

.. java:import:: ca.nengo.ui.dataList DataListView

.. java:import:: ca.nengo.ui.dataList SimulatorDataModel

.. java:import:: ca.nengo.ui.lib AppFrame

.. java:import:: ca.nengo.ui.lib AuxillarySplitPane

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions DisabledAction

.. java:import:: ca.nengo.ui.lib.actions DragAction

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.actions UserCancelledException

.. java:import:: ca.nengo.ui.lib.actions ZoomToFitAction

.. java:import:: ca.nengo.ui.lib.misc ShortcutKey

.. java:import:: ca.nengo.ui.lib.objects.models ModelObject

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.util.menus MenuBuilder

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world WorldObject.Property

.. java:import:: ca.nengo.ui.lib.world.elastic ElasticWorld

.. java:import:: ca.nengo.ui.lib.world.handlers MouseHandler

.. java:import:: ca.nengo.ui.lib.world.handlers SelectionHandler

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects Window

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives Universe

.. java:import:: ca.nengo.ui.models NodeContainer

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.constructors CNetwork

.. java:import:: ca.nengo.ui.models.nodes UINetwork

.. java:import:: ca.nengo.ui.models.nodes.widgets UIProbe

.. java:import:: ca.nengo.ui.models.nodes.widgets UIProjection

.. java:import:: ca.nengo.ui.models.nodes.widgets Widget

.. java:import:: ca.nengo.ui.script ScriptConsole

.. java:import:: ca.nengo.ui.util NengoClipboard

.. java:import:: ca.nengo.ui.util NengoConfigManager

.. java:import:: ca.nengo.ui.util NengoConfigManager.UserProperties

.. java:import:: ca.nengo.ui.util NeoFileChooser

.. java:import:: ca.nengo.ui.util ProgressIndicator

.. java:import:: ca.nengo.ui.util ScriptWorldWrapper

.. java:import:: ca.nengo.ui.world NengoWorld

.. java:import:: ca.nengo.util Environment

RunNetworkAction
================

.. java:package:: ca.nengo.ui
   :noindex:

.. java:type::  class RunNetworkAction extends StandardAction

   Runs the closest network to the currently selected obj

   :author: Shu Wu

Constructors
------------
RunNetworkAction
^^^^^^^^^^^^^^^^

.. java:constructor:: public RunNetworkAction(String description)
   :outertype: RunNetworkAction

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: RunNetworkAction


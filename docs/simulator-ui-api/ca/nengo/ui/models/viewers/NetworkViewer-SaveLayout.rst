.. java:import:: java.io BufferedReader

.. java:import:: java.io BufferedWriter

.. java:import:: java.io File

.. java:import:: java.io FileReader

.. java:import:: java.io FileWriter

.. java:import:: java.io IOException

.. java:import:: java.util Enumeration

.. java:import:: java.util HashMap

.. java:import:: java.util HashSet

.. java:import:: java.util LinkedList

.. java:import:: javax.swing JOptionPane

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model Projection

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model.impl NetworkImpl

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects Button

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects.icons ArrowIcon

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects.icons LoadIcon

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects.icons SaveIcon

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects.icons ZoomIcon

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives Path

.. java:import:: ca.nengo.ui.models NodeContainer

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.nodes UINetwork

.. java:import:: ca.nengo.ui.models.nodes.widgets UIOrigin

.. java:import:: ca.nengo.ui.models.nodes.widgets UIProbe

.. java:import:: ca.nengo.ui.models.nodes.widgets UIProjection

.. java:import:: ca.nengo.ui.models.nodes.widgets UIStateProbe

.. java:import:: ca.nengo.ui.models.nodes.widgets UITermination

.. java:import:: ca.nengo.util Probe

.. java:import:: edu.umd.cs.piccolo.event PBasicInputEventHandler

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

.. java:import:: edu.umd.cs.piccolo.util PBounds

NetworkViewer.SaveLayout
========================

.. java:package:: ca.nengo.ui.models.viewers
   :noindex:

.. java:type::  class SaveLayout extends StandardAction
   :outertype: NetworkViewer

   Action to save a layout

   :author: Shu Wu

Constructors
------------
SaveLayout
^^^^^^^^^^

.. java:constructor:: public SaveLayout()
   :outertype: NetworkViewer.SaveLayout

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: NetworkViewer.SaveLayout


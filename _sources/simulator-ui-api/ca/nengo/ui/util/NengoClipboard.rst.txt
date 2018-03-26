.. java:import:: java.awt.geom Point2D

.. java:import:: java.security InvalidParameterException

.. java:import:: java.util ArrayList

.. java:import:: java.util LinkedList

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldImpl

NengoClipboard
==============

.. java:package:: ca.nengo.ui.util
   :noindex:

.. java:type:: public class NengoClipboard

Fields
------
listeners
^^^^^^^^^

.. java:field::  LinkedList<ClipboardListener> listeners
   :outertype: NengoClipboard

Methods
-------
addClipboardListener
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void addClipboardListener(ClipboardListener listener)
   :outertype: NengoClipboard

getContents
^^^^^^^^^^^

.. java:method:: public ArrayList<Node> getContents()
   :outertype: NengoClipboard

getContentsNames
^^^^^^^^^^^^^^^^

.. java:method:: public ArrayList<String> getContentsNames()
   :outertype: NengoClipboard

getOffsets
^^^^^^^^^^

.. java:method:: public ArrayList<Point2D> getOffsets()
   :outertype: NengoClipboard

getSourceWorld
^^^^^^^^^^^^^^

.. java:method:: public WorldImpl getSourceWorld()
   :outertype: NengoClipboard

hasContents
^^^^^^^^^^^

.. java:method:: public boolean hasContents()
   :outertype: NengoClipboard

removeClipboardListener
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeClipboardListener(ClipboardListener listener)
   :outertype: NengoClipboard

setContents
^^^^^^^^^^^

.. java:method:: public void setContents(ArrayList<Node> nodes, ArrayList<Point2D> objOffsets)
   :outertype: NengoClipboard

setContents
^^^^^^^^^^^

.. java:method:: public void setContents(ArrayList<Node> nodes, ArrayList<Point2D> objOffsets, WorldImpl srcWorld)
   :outertype: NengoClipboard


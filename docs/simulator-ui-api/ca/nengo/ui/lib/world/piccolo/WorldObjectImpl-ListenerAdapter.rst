.. java:import:: java.awt Paint

.. java:import:: java.awt.geom Dimension2D

.. java:import:: java.awt.geom Point2D

.. java:import:: java.awt.geom Rectangle2D

.. java:import:: java.beans PropertyChangeEvent

.. java:import:: java.beans PropertyChangeListener

.. java:import:: java.security InvalidParameterException

.. java:import:: java.util ArrayList

.. java:import:: java.util Collection

.. java:import:: java.util Enumeration

.. java:import:: java.util HashSet

.. java:import:: java.util Hashtable

.. java:import:: java.util Iterator

.. java:import:: java.util List

.. java:import:: ca.nengo.ui.lib.objects.activities TransientMessage

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.world Destroyable

.. java:import:: ca.nengo.ui.lib.world PaintContext

.. java:import:: ca.nengo.ui.lib.world WorldLayer

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world WorldObject.Listener

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PXNode

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PiccoloNodeInWorld

.. java:import:: edu.umd.cs.piccolo PCamera

.. java:import:: edu.umd.cs.piccolo PNode

.. java:import:: edu.umd.cs.piccolo.activities PInterpolatingActivity

.. java:import:: edu.umd.cs.piccolo.event PInputEventListener

.. java:import:: edu.umd.cs.piccolo.util PBounds

WorldObjectImpl.ListenerAdapter
===============================

.. java:package:: ca.nengo.ui.lib.world.piccolo
   :noindex:

.. java:type::  class ListenerAdapter implements Destroyable
   :outertype: WorldObjectImpl

Constructors
------------
ListenerAdapter
^^^^^^^^^^^^^^^

.. java:constructor:: public ListenerAdapter(Property eventType, Listener listener)
   :outertype: WorldObjectImpl.ListenerAdapter

Methods
-------
destroy
^^^^^^^

.. java:method:: public void destroy()
   :outertype: WorldObjectImpl.ListenerAdapter

getListener
^^^^^^^^^^^

.. java:method:: public Listener getListener()
   :outertype: WorldObjectImpl.ListenerAdapter


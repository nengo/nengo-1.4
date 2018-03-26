.. java:import:: java.awt BasicStroke

.. java:import:: java.awt.geom Point2D

.. java:import:: java.awt.geom Rectangle2D

.. java:import:: java.util ArrayList

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.world Destroyable

.. java:import:: ca.nengo.ui.lib.world WorldLayer

.. java:import:: ca.nengo.ui.lib.world WorldObject.Listener

.. java:import:: ca.nengo.ui.lib.world WorldObject.Property

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives Path

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PiccoloNodeInWorld

Anchor
======

.. java:package:: ca.nengo.ui.lib.world.elastic
   :noindex:

.. java:type::  class Anchor implements Destroyable, Listener

Constructors
------------
Anchor
^^^^^^

.. java:constructor:: public Anchor(ElasticObject obj)
   :outertype: Anchor

Methods
-------
destroy
^^^^^^^

.. java:method:: public void destroy()
   :outertype: Anchor

propertyChanged
^^^^^^^^^^^^^^^

.. java:method:: public void propertyChanged(Property event)
   :outertype: Anchor


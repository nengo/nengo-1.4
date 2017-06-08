.. java:import:: java.awt.geom Point2D

.. java:import:: java.awt.geom Rectangle2D

.. java:import:: java.io Serializable

.. java:import:: java.util Hashtable

.. java:import:: ca.nengo.ui.lib.world World

.. java:import:: ca.nengo.ui.lib.world WorldObject

PointSerializable
=================

.. java:package:: ca.nengo.ui.lib.misc
   :noindex:

.. java:type::  class PointSerializable implements Serializable

   Wraps point2D in a serializable wrapper

   :author: Shu Wu

Fields
------
x
^

.. java:field::  double x
   :outertype: PointSerializable

Constructors
------------
PointSerializable
^^^^^^^^^^^^^^^^^

.. java:constructor:: public PointSerializable(Point2D point)
   :outertype: PointSerializable

Methods
-------
toPoint2D
^^^^^^^^^

.. java:method:: public Point2D toPoint2D()
   :outertype: PointSerializable


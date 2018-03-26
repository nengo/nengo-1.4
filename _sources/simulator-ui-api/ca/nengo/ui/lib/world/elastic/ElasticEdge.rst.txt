.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PXEdge

ElasticEdge
===========

.. java:package:: ca.nengo.ui.lib.world.elastic
   :noindex:

.. java:type:: public class ElasticEdge extends PXEdge

Constructors
------------
ElasticEdge
^^^^^^^^^^^

.. java:constructor:: public ElasticEdge(WorldObjectImpl startNode, WorldObjectImpl endNode, double length, boolean isDirected)
   :outertype: ElasticEdge

ElasticEdge
^^^^^^^^^^^

.. java:constructor:: public ElasticEdge(WorldObjectImpl startNode, WorldObjectImpl endNode, double length)
   :outertype: ElasticEdge

Methods
-------
getLength
^^^^^^^^^

.. java:method:: public double getLength()
   :outertype: ElasticEdge

setLength
^^^^^^^^^

.. java:method:: public void setLength(double length)
   :outertype: ElasticEdge


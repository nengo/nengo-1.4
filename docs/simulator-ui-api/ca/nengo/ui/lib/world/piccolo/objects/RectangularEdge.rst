.. java:import:: java.awt.geom Point2D

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PXEdge

RectangularEdge
===============

.. java:package:: ca.nengo.ui.lib.world.piccolo.objects
   :noindex:

.. java:type:: public class RectangularEdge extends WorldObjectImpl

   An set 4 edges which attach the respective corners of its starting and ending nodes. Has a pseudo-3d effect.

   :author: Shu Wu

Constructors
------------
RectangularEdge
^^^^^^^^^^^^^^^

.. java:constructor:: public RectangularEdge(WorldObjectImpl startNode, WorldObjectImpl endNode)
   :outertype: RectangularEdge

   Creates a new rectangular edge

   :param startNode: Starting node
   :param endNode: Ending node


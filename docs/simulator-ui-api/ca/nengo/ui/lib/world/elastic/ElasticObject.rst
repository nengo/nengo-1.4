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

ElasticObject
=============

.. java:package:: ca.nengo.ui.lib.world.elastic
   :noindex:

.. java:type:: public class ElasticObject extends WorldObjectImpl

Constructors
------------
ElasticObject
^^^^^^^^^^^^^

.. java:constructor:: public ElasticObject()
   :outertype: ElasticObject

ElasticObject
^^^^^^^^^^^^^

.. java:constructor:: public ElasticObject(PiccoloNodeInWorld node)
   :outertype: ElasticObject

ElasticObject
^^^^^^^^^^^^^

.. java:constructor:: public ElasticObject(String name)
   :outertype: ElasticObject

ElasticObject
^^^^^^^^^^^^^

.. java:constructor:: public ElasticObject(String name, PiccoloNodeInWorld node)
   :outertype: ElasticObject

Methods
-------
getElasticWorld
^^^^^^^^^^^^^^^

.. java:method:: protected ElasticGround getElasticWorld()
   :outertype: ElasticObject

getOffset
^^^^^^^^^

.. java:method:: @Override public Point2D getOffset()
   :outertype: ElasticObject

getOffsetReal
^^^^^^^^^^^^^

.. java:method:: public Point2D getOffsetReal()
   :outertype: ElasticObject

   This is the real getOffset function.

   :param x:
   :param y:

getRepulsionRange
^^^^^^^^^^^^^^^^^

.. java:method:: public double getRepulsionRange()
   :outertype: ElasticObject

isAnchored
^^^^^^^^^^

.. java:method:: public boolean isAnchored()
   :outertype: ElasticObject

isPositionLocked
^^^^^^^^^^^^^^^^

.. java:method:: public boolean isPositionLocked()
   :outertype: ElasticObject

prepareForDestroy
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void prepareForDestroy()
   :outertype: ElasticObject

setAnchored
^^^^^^^^^^^

.. java:method:: public void setAnchored(boolean anchored)
   :outertype: ElasticObject

setOffset
^^^^^^^^^

.. java:method:: @Override public void setOffset(double x, double y)
   :outertype: ElasticObject

   **See also:** :java:ref:`edu.umd.cs.piccolo.PNode.setOffset(double,double)
   <p>
   IfNetworkViewerexistsasaparent,thisbecomesare-directto
   NetworkViewer'ssetlocationfunction.
   </p>`

setOffsetReal
^^^^^^^^^^^^^

.. java:method:: public void setOffsetReal(double x, double y)
   :outertype: ElasticObject

   This is the real setOffset function.

   :param x:
   :param y:

setPositionLocked
^^^^^^^^^^^^^^^^^

.. java:method:: public void setPositionLocked(boolean lock)
   :outertype: ElasticObject

setSelected
^^^^^^^^^^^

.. java:method:: @Override public void setSelected(boolean isSelected)
   :outertype: ElasticObject


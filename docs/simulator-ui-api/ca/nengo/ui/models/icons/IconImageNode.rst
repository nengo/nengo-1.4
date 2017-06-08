.. java:import:: java.awt Graphics2D

.. java:import:: java.awt Image

.. java:import:: java.awt.geom Ellipse2D

.. java:import:: java.awt.geom GeneralPath

.. java:import:: java.net URL

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PXImage

.. java:import:: edu.umd.cs.piccolo.util PBounds

.. java:import:: edu.umd.cs.piccolo.util PPaintContext

IconImageNode
=============

.. java:package:: ca.nengo.ui.models.icons
   :noindex:

.. java:type::  class IconImageNode extends PXImage

   Just like PImage, except it semantically zooms (ie. at low scales, it does not paint its bitmap)

   :author: Shu Wu

Fields
------
ENABLE_SEMANTIC_ZOOM
^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static boolean ENABLE_SEMANTIC_ZOOM
   :outertype: IconImageNode

Constructors
------------
IconImageNode
^^^^^^^^^^^^^

.. java:constructor:: public IconImageNode()
   :outertype: IconImageNode

IconImageNode
^^^^^^^^^^^^^

.. java:constructor:: public IconImageNode(Image arg0)
   :outertype: IconImageNode

IconImageNode
^^^^^^^^^^^^^

.. java:constructor:: public IconImageNode(String arg0)
   :outertype: IconImageNode

IconImageNode
^^^^^^^^^^^^^

.. java:constructor:: public IconImageNode(URL arg0)
   :outertype: IconImageNode

Methods
-------
paint
^^^^^

.. java:method:: @Override protected void paint(PPaintContext aPaintContext)
   :outertype: IconImageNode


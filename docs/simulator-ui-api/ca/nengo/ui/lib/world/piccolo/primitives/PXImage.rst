.. java:import:: java.awt Graphics2D

.. java:import:: java.awt GraphicsConfiguration

.. java:import:: java.awt GraphicsEnvironment

.. java:import:: java.awt Image

.. java:import:: java.awt MediaTracker

.. java:import:: java.awt Toolkit

.. java:import:: java.awt.image BufferedImage

.. java:import:: java.io IOException

.. java:import:: java.io ObjectInputStream

.. java:import:: java.io ObjectOutputStream

.. java:import:: javax.imageio ImageIO

.. java:import:: javax.swing ImageIcon

.. java:import:: edu.umd.cs.piccolo.util PBounds

.. java:import:: edu.umd.cs.piccolo.util PPaintContext

PXImage
=======

.. java:package:: ca.nengo.ui.lib.world.piccolo.primitives
   :noindex:

.. java:type:: public class PXImage extends PXNode

   \ **PImage**\  is a wrapper around a java.awt.Image. If this node is copied or serialized that image will be converted into a BufferedImage if it is not already one.

   :author: Jesse Grosjean

Fields
------
PROPERTY_CODE_IMAGE
^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int PROPERTY_CODE_IMAGE
   :outertype: PXImage

PROPERTY_IMAGE
^^^^^^^^^^^^^^

.. java:field:: public static final String PROPERTY_IMAGE
   :outertype: PXImage

   The property name that identifies a change of this node's image (see \ :java:ref:`getImage <getImage>`\ ). Both old and new value will be set correctly to Image objects in any property change event.

Constructors
------------
PXImage
^^^^^^^

.. java:constructor:: public PXImage()
   :outertype: PXImage

PXImage
^^^^^^^

.. java:constructor:: public PXImage(Image newImage)
   :outertype: PXImage

   Construct a new PImage wrapping the given java.awt.Image.

PXImage
^^^^^^^

.. java:constructor:: public PXImage(String fileName)
   :outertype: PXImage

   Construct a new PImage by loading the given fileName and wrapping the resulting java.awt.Image.

PXImage
^^^^^^^

.. java:constructor:: public PXImage(java.net.URL url)
   :outertype: PXImage

   Construct a new PImage by loading the given url and wrapping the resulting java.awt.Image. If the url is \ ``null``\ , create an empty PImage; this behaviour is useful when fetching resources that may be missing.

Methods
-------
getImage
^^^^^^^^

.. java:method:: public Image getImage()
   :outertype: PXImage

   Returns the image that is shown by this node.

   :return: the image that is shown by this node

paint
^^^^^

.. java:method:: protected void paint(PPaintContext paintContext)
   :outertype: PXImage

paramString
^^^^^^^^^^^

.. java:method:: protected String paramString()
   :outertype: PXImage

   Returns a string representing the state of this node. This method is intended to be used only for debugging purposes, and the content and format of the returned string may vary between implementations. The returned string may be empty but may not be \ ``null``\ .

   :return: a string representation of this node's state

setImage
^^^^^^^^

.. java:method:: public void setImage(String fileName)
   :outertype: PXImage

   Set the image that is wrapped by this PImage node. This method will also load the image using a MediaTracker before returning.

setImage
^^^^^^^^

.. java:method:: public void setImage(Image newImage)
   :outertype: PXImage

   Set the image that is wrapped by this PImage node. This method will also load the image using a MediaTracker before returning.

toBufferedImage
^^^^^^^^^^^^^^^

.. java:method:: public static BufferedImage toBufferedImage(Image image, boolean alwaysCreateCopy)
   :outertype: PXImage

   If alwaysCreateCopy is false then if the image is already a buffered image it will not be copied and instead the original image will just be returned.


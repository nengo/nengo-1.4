.. java:import:: java.awt.image BufferedImage

.. java:import:: java.awt.image ColorModel

.. java:import:: java.awt.image DataBuffer

.. java:import:: java.awt.image DataBufferByte

.. java:import:: java.awt.image IndexColorModel

.. java:import:: java.awt.image Raster

.. java:import:: java.awt.image SampleModel

.. java:import:: java.awt.image SinglePixelPackedSampleModel

.. java:import:: java.awt.image WritableRaster

AbstractBrainImage2D
====================

.. java:package:: ca.nengo.ui.brainView
   :noindex:

.. java:type:: public abstract class AbstractBrainImage2D extends BufferedImage

   TODO

   :author: TODO

Fields
------
imageWidth
^^^^^^^^^^

.. java:field::  int imageWidth
   :outertype: AbstractBrainImage2D

viewCoord
^^^^^^^^^

.. java:field::  int viewCoord
   :outertype: AbstractBrainImage2D

Constructors
------------
AbstractBrainImage2D
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public AbstractBrainImage2D(int width, int height)
   :outertype: AbstractBrainImage2D

   :param width: TODO
   :param height: TODO

Methods
-------
getCoord
^^^^^^^^

.. java:method:: public int getCoord()
   :outertype: AbstractBrainImage2D

   :return: TODO

getCoordDefault
^^^^^^^^^^^^^^^

.. java:method:: public int getCoordDefault()
   :outertype: AbstractBrainImage2D

   :return: TODO

getCoordMax
^^^^^^^^^^^

.. java:method:: public abstract int getCoordMax()
   :outertype: AbstractBrainImage2D

   :return: TODO

getCoordMin
^^^^^^^^^^^

.. java:method:: public abstract int getCoordMin()
   :outertype: AbstractBrainImage2D

   :return: TODO

getCoordName
^^^^^^^^^^^^

.. java:method:: public abstract String getCoordName()
   :outertype: AbstractBrainImage2D

   :return: TODO

getImageByte
^^^^^^^^^^^^

.. java:method:: public abstract byte getImageByte(int imageX, int imageY)
   :outertype: AbstractBrainImage2D

   :param imageX: TODO
   :param imageY: TODO
   :return: TODO

getViewName
^^^^^^^^^^^

.. java:method:: public abstract String getViewName()
   :outertype: AbstractBrainImage2D

   :return: TODO

setCoord
^^^^^^^^

.. java:method:: public void setCoord(int coord)
   :outertype: AbstractBrainImage2D

   :param coord: TODO


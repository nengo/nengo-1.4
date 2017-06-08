.. java:import:: java.awt Color

.. java:import:: java.awt Font

.. java:import:: java.awt Graphics2D

.. java:import:: java.awt Paint

.. java:import:: java.awt.font LineBreakMeasurer

.. java:import:: java.awt.font TextAttribute

.. java:import:: java.awt.font TextLayout

.. java:import:: java.text AttributedCharacterIterator

.. java:import:: java.text AttributedString

.. java:import:: java.util ArrayList

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: edu.umd.cs.piccolo.util PPaintContext

PXText
======

.. java:package:: ca.nengo.ui.lib.world.piccolo.primitives
   :noindex:

.. java:type::  class PXText extends PXNode

   \ **PText**\  is a multi-line text node. The text will flow to base on the width of the node's bounds.

   :author: Jesse Grosjean

Fields
------
DEFAULT_FONT
^^^^^^^^^^^^

.. java:field:: public static Font DEFAULT_FONT
   :outertype: PXText

DEFAULT_GREEK_THRESHOLD
^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static double DEFAULT_GREEK_THRESHOLD
   :outertype: PXText

PROPERTY_CODE_FONT
^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int PROPERTY_CODE_FONT
   :outertype: PXText

PROPERTY_CODE_TEXT
^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int PROPERTY_CODE_TEXT
   :outertype: PXText

PROPERTY_FONT
^^^^^^^^^^^^^

.. java:field:: public static final String PROPERTY_FONT
   :outertype: PXText

   The property name that identifies a change of this node's font (see \ :java:ref:`getFont <getFont>`\ ). Both old and new value will be set in any property change event.

PROPERTY_TEXT
^^^^^^^^^^^^^

.. java:field:: public static final String PROPERTY_TEXT
   :outertype: PXText

   The property name that identifies a change of this node's text (see \ :java:ref:`getText <getText>`\ ). Both old and new value will be set in any property change event.

greekThreshold
^^^^^^^^^^^^^^

.. java:field:: protected double greekThreshold
   :outertype: PXText

Constructors
------------
PXText
^^^^^^

.. java:constructor:: public PXText()
   :outertype: PXText

PXText
^^^^^^

.. java:constructor:: public PXText(String aText)
   :outertype: PXText

Methods
-------
computeNextLayout
^^^^^^^^^^^^^^^^^

.. java:method:: protected TextLayout computeNextLayout(LineBreakMeasurer measurer, float availibleWidth, int nextLineBreakOffset)
   :outertype: PXText

getFont
^^^^^^^

.. java:method:: public Font getFont()
   :outertype: PXText

   Returns the font of this PText.

   :return: the font of this PText.

getGreekThreshold
^^^^^^^^^^^^^^^^^

.. java:method:: public double getGreekThreshold()
   :outertype: PXText

   Returns the current greek threshold. When the screen font size will be below this threshold the text is rendered as 'greek' instead of drawing the text glyphs.

getJustification
^^^^^^^^^^^^^^^^

.. java:method:: public float getJustification()
   :outertype: PXText

   Return the justificaiton of the text in the bounds.

   :return: float

getText
^^^^^^^

.. java:method:: public String getText()
   :outertype: PXText

getTextPaint
^^^^^^^^^^^^

.. java:method:: public Paint getTextPaint()
   :outertype: PXText

   Get the paint used to paint this nodes text.

   :return: Paint

getUseGreekThreshold
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static boolean getUseGreekThreshold()
   :outertype: PXText

internalUpdateBounds
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void internalUpdateBounds(double x, double y, double width, double height)
   :outertype: PXText

isConstrainHeightToTextHeight
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isConstrainHeightToTextHeight()
   :outertype: PXText

isConstrainWidthToTextWidth
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isConstrainWidthToTextWidth()
   :outertype: PXText

paint
^^^^^

.. java:method:: protected void paint(PPaintContext paintContext)
   :outertype: PXText

paramString
^^^^^^^^^^^

.. java:method:: protected String paramString()
   :outertype: PXText

   Returns a string representing the state of this node. This method is intended to be used only for debugging purposes, and the content and format of the returned string may vary between implementations. The returned string may be empty but may not be \ ``null``\ .

   :return: a string representation of this node's state

recomputeLayout
^^^^^^^^^^^^^^^

.. java:method:: public void recomputeLayout()
   :outertype: PXText

   Compute the bounds of the text wrapped by this node. The text layout is wrapped based on the bounds of this node.

setConstrainHeightToTextHeight
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setConstrainHeightToTextHeight(boolean constrainHeightToTextHeight)
   :outertype: PXText

   Controls whether this node changes its height to fit the height of its text. If flag is true it does; if flag is false it doesn't

setConstrainWidthToTextWidth
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setConstrainWidthToTextWidth(boolean constrainWidthToTextWidth)
   :outertype: PXText

   Controls whether this node changes its width to fit the width of its text. If flag is true it does; if flag is false it doesn't

setFont
^^^^^^^

.. java:method:: public void setFont(Font aFont)
   :outertype: PXText

   Set the font of this PText. Note that in Piccolo if you want to change the size of a text object it's often a better idea to scale the PText node instead of changing the font size to get that same effect. Using very large font sizes can slow performance.

setGreekThreshold
^^^^^^^^^^^^^^^^^

.. java:method:: public void setGreekThreshold(double threshold)
   :outertype: PXText

   Sets the current greek threshold. When the screen font size will be below this threshold the text is rendered as 'greek' instead of drawing the text glyphs.

   :param threshold: minimum screen font size.

setJustification
^^^^^^^^^^^^^^^^

.. java:method:: public void setJustification(float just)
   :outertype: PXText

   Sets the justificaiton of the text in the bounds.

   :param just:

setText
^^^^^^^

.. java:method:: public void setText(String aText)
   :outertype: PXText

   Set the text for this node. The text will be broken up into multiple lines based on the size of the text and the bounds width of this node.

setTextPaint
^^^^^^^^^^^^

.. java:method:: public void setTextPaint(Paint textPaint)
   :outertype: PXText

   Set the paint used to paint this node's text background.

   :param textPaint:

setUseGreekThreshold
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static void setUseGreekThreshold(boolean state)
   :outertype: PXText


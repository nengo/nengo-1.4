.. java:import:: java.awt Color

.. java:import:: java.awt Container

.. java:import:: java.awt Font

.. java:import:: javax.swing JComponent

.. java:import:: javax.swing UIManager

.. java:import:: javax.swing.tree DefaultTreeCellRenderer

NengoStyle
==========

.. java:package:: ca.nengo.ui.lib.Style
   :noindex:

.. java:type:: public class NengoStyle

   Style constants used by NEO Graphics

   :author: Shu Wu

Fields
------
ANIMATION_DROP_IN_WORLD_MS
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int ANIMATION_DROP_IN_WORLD_MS
   :outertype: NengoStyle

COLOR_ANCHOR
^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_ANCHOR
   :outertype: NengoStyle

COLOR_BACKGROUND
^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_BACKGROUND
   :outertype: NengoStyle

COLOR_BACKGROUND2
^^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_BACKGROUND2
   :outertype: NengoStyle

COLOR_BORDER_SELECTED
^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_BORDER_SELECTED
   :outertype: NengoStyle

COLOR_BUTTON_BACKGROUND
^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_BUTTON_BACKGROUND
   :outertype: NengoStyle

COLOR_BUTTON_BORDER
^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_BUTTON_BORDER
   :outertype: NengoStyle

COLOR_BUTTON_HIGHLIGHT
^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_BUTTON_HIGHLIGHT
   :outertype: NengoStyle

COLOR_BUTTON_SELECTED
^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_BUTTON_SELECTED
   :outertype: NengoStyle

COLOR_CONFIGURE_BACKGROUND
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_CONFIGURE_BACKGROUND
   :outertype: NengoStyle

COLOR_DARKBORDER
^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_DARKBORDER
   :outertype: NengoStyle

COLOR_DARK_BLUE
^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_DARK_BLUE
   :outertype: NengoStyle

COLOR_DISABLED
^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_DISABLED
   :outertype: NengoStyle

COLOR_FOREGROUND
^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_FOREGROUND
   :outertype: NengoStyle

COLOR_FOREGROUND2
^^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_FOREGROUND2
   :outertype: NengoStyle

COLOR_HIGH_SALIENCE
^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_HIGH_SALIENCE
   :outertype: NengoStyle

COLOR_LIGHT_BLUE
^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_LIGHT_BLUE
   :outertype: NengoStyle

COLOR_LIGHT_GREEN
^^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_LIGHT_GREEN
   :outertype: NengoStyle

COLOR_LIGHT_PURPLE
^^^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_LIGHT_PURPLE
   :outertype: NengoStyle

COLOR_LINE
^^^^^^^^^^

.. java:field:: public static final Color COLOR_LINE
   :outertype: NengoStyle

COLOR_LINEEND
^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_LINEEND
   :outertype: NengoStyle

COLOR_LINEENDWELL
^^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_LINEENDWELL
   :outertype: NengoStyle

COLOR_LINEIN
^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_LINEIN
   :outertype: NengoStyle

COLOR_LINE_HIGHLIGHT
^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_LINE_HIGHLIGHT
   :outertype: NengoStyle

COLOR_MENU_BACKGROUND
^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_MENU_BACKGROUND
   :outertype: NengoStyle

COLOR_NOTIFICATION
^^^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_NOTIFICATION
   :outertype: NengoStyle

COLOR_SEARCH_BAD_CHAR
^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_SEARCH_BAD_CHAR
   :outertype: NengoStyle

COLOR_SEARCH_BOX_BORDER
^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_SEARCH_BOX_BORDER
   :outertype: NengoStyle

COLOR_TOOLTIP_BORDER
^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final Color COLOR_TOOLTIP_BORDER
   :outertype: NengoStyle

FONT_BIG
^^^^^^^^

.. java:field:: public static final Font FONT_BIG
   :outertype: NengoStyle

FONT_BOLD
^^^^^^^^^

.. java:field:: public static final Font FONT_BOLD
   :outertype: NengoStyle

FONT_BUTTONS
^^^^^^^^^^^^

.. java:field:: public static final Font FONT_BUTTONS
   :outertype: NengoStyle

FONT_FAMILY
^^^^^^^^^^^

.. java:field:: public static final String FONT_FAMILY
   :outertype: NengoStyle

FONT_LARGE
^^^^^^^^^^

.. java:field:: public static final Font FONT_LARGE
   :outertype: NengoStyle

FONT_MENU
^^^^^^^^^

.. java:field:: public static final Font FONT_MENU
   :outertype: NengoStyle

FONT_MENU_TITLE
^^^^^^^^^^^^^^^

.. java:field:: public static final Font FONT_MENU_TITLE
   :outertype: NengoStyle

FONT_NORMAL
^^^^^^^^^^^

.. java:field:: public static final Font FONT_NORMAL
   :outertype: NengoStyle

FONT_SEARCH_RESULT_COUNT
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final Font FONT_SEARCH_RESULT_COUNT
   :outertype: NengoStyle

FONT_SEARCH_TEXT
^^^^^^^^^^^^^^^^

.. java:field:: public static final Font FONT_SEARCH_TEXT
   :outertype: NengoStyle

FONT_SMALL
^^^^^^^^^^

.. java:field:: public static final Font FONT_SMALL
   :outertype: NengoStyle

FONT_WINDOW_BUTTONS
^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final Font FONT_WINDOW_BUTTONS
   :outertype: NengoStyle

FONT_XLARGE
^^^^^^^^^^^

.. java:field:: public static final Font FONT_XLARGE
   :outertype: NengoStyle

FONT_XXLARGE
^^^^^^^^^^^^

.. java:field:: public static final Font FONT_XXLARGE
   :outertype: NengoStyle

GTK
^^^

.. java:field:: public static boolean GTK
   :outertype: NengoStyle

Methods
-------
applyMenuStyle
^^^^^^^^^^^^^^

.. java:method:: public static void applyMenuStyle(JComponent item, boolean isTitle)
   :outertype: NengoStyle

applyStyle
^^^^^^^^^^

.. java:method:: public static void applyStyle(JComponent item)
   :outertype: NengoStyle

applyStyle
^^^^^^^^^^

.. java:method:: public static void applyStyle(Container item)
   :outertype: NengoStyle

applyStyle
^^^^^^^^^^

.. java:method:: public static void applyStyle(DefaultTreeCellRenderer cellRenderer)
   :outertype: NengoStyle

colorAdd
^^^^^^^^

.. java:method:: public static Color colorAdd(Color c1, Color c2)
   :outertype: NengoStyle

colorTimes
^^^^^^^^^^

.. java:method:: public static Color colorTimes(Color c1, double f)
   :outertype: NengoStyle

createFont
^^^^^^^^^^

.. java:method:: public static Font createFont(int size)
   :outertype: NengoStyle

createFont
^^^^^^^^^^

.. java:method:: public static Font createFont(int size, boolean isBold)
   :outertype: NengoStyle


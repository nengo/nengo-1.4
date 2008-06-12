package ca.shu.ui.lib.Style;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Style constants used by NEO Graphics
 * 
 * @author Shu Wu
 */
public class Style {
	public static final int ANIMATION_DROP_IN_WORLD_MS = 200;
	/*
	 * Colors
	 */
	public static final Color COLOR_BACKGROUND = Color.black;
	public static final Color COLOR_FOREGROUND = Color.white;
	public static final Color COLOR_BACKGROUND2 = Color.darkGray;
	public static final Color COLOR_FOREGROUND2 = Color.gray;
	public static final Color COLOR_BORDER_SELECTED = Color.orange;
	public static final Color COLOR_DARK_BLUE = new Color(0, 0, 80);

	/*
	 * Button Colors
	 */
	public static final Color COLOR_BUTTON_BACKGROUND = Color.darkGray;
	public static final Color COLOR_BUTTON_BORDER = Color.darkGray;

	public static final Color COLOR_BUTTON_HIGHLIGHT = Color.black;

	public static final Color COLOR_BUTTON_SELECTED = Color.gray;
	public static final Color COLOR_DARKBORDER = Color.darkGray;

	/*
	 * Other colors
	 */
	public static final Color COLOR_DISABLED = Color.gray;
	public static final Color COLOR_ANCHOR = Color.lightGray;
	public static final Color COLOR_HIGH_SALIENCE = new Color(150, 0, 0);

	/*
	 * Search colors
	 */
	public static final Color COLOR_SEARCH_BOX_BORDER = Color.green;
	public static final Color COLOR_SEARCH_BAD_CHAR = Color.red;

	/*
	 * Named colors
	 */
	public static final Color COLOR_LIGHT_PURPLE = new Color(225, 180, 255);
	public static final Color COLOR_LIGHT_BLUE = new Color(176, 220, 246);
	public static final Color COLOR_LIGHT_GREEN = new Color(176, 246, 182);

	/*
	 * Line Colors
	 */
	public static final Color COLOR_LINE = COLOR_LIGHT_GREEN;

	public static final Color COLOR_LINE_HIGHLIGHT = Color.red;

	public static final Color COLOR_LINEEND = COLOR_LIGHT_GREEN;

	public static final Color COLOR_LINEENDWELL = COLOR_LIGHT_BLUE;
	public static final Color COLOR_LINEIN = new Color(0, 128, 0);

	public static final Color COLOR_MENU_BACKGROUND = Color.black;
	public static final Color COLOR_NOTIFICATION = Color.orange;

	public static final Color COLOR_TOOLTIP_BORDER = new Color(100, 149, 237);

	/*
	 * Fonts
	 */
	public static final Font FONT_BIG = new Font("Helvetica", Font.BOLD, 16);

	public static final Font FONT_BOLD = new Font("Helvetica", Font.BOLD, 14);
	public static final Font FONT_BUTTONS = new Font("Helvetica", Font.PLAIN, 14);
	public static final Font FONT_LARGE = new Font("Helvetica", Font.BOLD, 18);
	public static final Font FONT_NORMAL = new Font("Helvetica", Font.PLAIN, 14);

	public static final Font FONT_SMALL = new Font("Helvetica", Font.PLAIN, 10);

	public static final Font FONT_WINDOW_BUTTONS = new Font("sansserif", Font.BOLD, 16);
	public static final Font FONT_XLARGE = new Font("Helvetica", Font.BOLD, 22);
	public static final Font FONT_XXLARGE = new Font("Helvetica", Font.BOLD, 32);

	public static final Font FONT_MENU_TITLE = new Font("Helvetica", Font.BOLD, 13);
	public static final Font FONT_MENU = new Font("Helvetica", Font.BOLD, 12);

	/*
	 * Search fonts
	 */
	public static final Font FONT_SEARCH_TEXT = new Font("Helvetica", Font.BOLD, 30);
	public static final Font FONT_SEARCH_RESULT_COUNT = new Font("Helvetica", Font.BOLD, 22);

	public static void applyStyle(JComponent item) {
		item.setBorder(null);
		applyStyle((Container) item);
	}

	public static void applyStyle(Container item) {
		item.setBackground(Style.COLOR_BACKGROUND);
		item.setForeground(Style.COLOR_FOREGROUND);
	}

	public static void applyStyle(DefaultTreeCellRenderer cellRenderer) {
		cellRenderer.setBackgroundNonSelectionColor(Style.COLOR_BACKGROUND);
		cellRenderer.setBackgroundSelectionColor(Style.COLOR_BACKGROUND2);
		cellRenderer.setTextNonSelectionColor(Style.COLOR_FOREGROUND);
		cellRenderer.setTextSelectionColor(Style.COLOR_FOREGROUND);

	}

	public static void applyMenuStyle(JComponent item, boolean isTitle) {
		item.setOpaque(true);
		// item.setBorder(null);
		item.setBackground(Style.COLOR_BACKGROUND);
		item.setForeground(Style.COLOR_FOREGROUND);
		if (isTitle) {
			item.setFont(Style.FONT_MENU_TITLE);
		} else {
			item.setFont(Style.FONT_MENU);
		}
	}

	static public Color colorAdd(Color c1, Color c2) {
		int r = Math.min(c1.getRed() + c2.getRed(), 255);
		int g = Math.min(c1.getGreen() + c2.getGreen(), 255);
		int b = Math.min(c1.getBlue() + c2.getBlue(), 255);
		return new Color(r, g, b);
	}

	static public Color colorTimes(Color c1, double f) {
		int r = (int) Math.min(c1.getRed() * f, 255);
		int g = (int) Math.min(c1.getGreen() * f, 255);
		int b = (int) Math.min(c1.getBlue() * f, 255);
		return new Color(r, g, b);
	}

	public static Font createFont(int size) {
		return createFont(size, false);
	}

	public static Font createFont(int size, boolean isBold) {
		return new Font("Arial", Font.BOLD, size);

	}
}

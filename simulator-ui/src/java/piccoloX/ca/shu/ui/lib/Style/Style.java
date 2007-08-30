package ca.shu.ui.lib.Style;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JComponent;

/**
 * Style constants used by NEO Graphics
 * 
 * @author Shu Wu
 */
public class Style {
	public static final int ANIMATION_DROP_IN_WORLD_MS = 200;
	/*
	 * Named colors
	 */
	public static final Color COLOR_LIGHT_PURPLE = new Color(225, 180, 255);
	public static final Color COLOR_LIGHT_BLUE = new Color(176, 220, 246);
	public static final Color COLOR_LIGHT_GREEN = new Color(176, 246, 182);

	/*
	 * Colors
	 */
	public static final Color COLOR_BACKGROUND = Color.black;
	public static final Color COLOR_BACKGROUND2 = Color.darkGray;

	public static final Color COLOR_BORDER_SELECTED = Color.orange;

	/*
	 * Button Colors
	 */
	public static final Color COLOR_BUTTON_BACKGROUND = Color.darkGray;
	public static final Color COLOR_BUTTON_BORDER = Color.darkGray;

	public static final Color COLOR_BUTTON_HIGHLIGHT = Color.black;
	public static final Color COLOR_BUTTON_SELECTED = Color.gray;
	public static final Color COLOR_DARKBORDER = Color.darkGray;

	public static final Color COLOR_DISABLED = Color.gray;
	public static final Color COLOR_FOREGROUND = Color.white;
	public static final Color COLOR_HIGH_SALIENCE = new Color(150, 0, 0);

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
	public static final Font FONT_BUTTONS = new Font("Helvetica", Font.PLAIN,
			14);
	public static final Font FONT_LARGE = new Font("Helvetica", Font.BOLD, 18);
	public static final Font FONT_NORMAL = new Font("Helvetica", Font.PLAIN, 14);
	public static final Font FONT_SMALL = new Font("Helvetica", Font.PLAIN, 10);

	public static final Font FONT_WINDOW_BUTTONS = new Font("sansserif",
			Font.BOLD, 16);
	public static final Font FONT_XLARGE = new Font("Helvetica", Font.BOLD, 22);
	public static final Font FONT_XXLARGE = new Font("Helvetica", Font.BOLD, 32);

	public static void applyStyleToComponent(JComponent item) {
		item.setOpaque(true);
		item.setBackground(Style.COLOR_BACKGROUND);
		item.setForeground(Style.COLOR_FOREGROUND);
	}

	public static Font createFont(int size) {
		return createFont(size, false);
	}

	public static Font createFont(int size, boolean isBold) {
		return new Font("Arial", Font.BOLD, size);

	}
}

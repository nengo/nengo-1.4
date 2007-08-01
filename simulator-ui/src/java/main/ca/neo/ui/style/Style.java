package ca.neo.ui.style;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JComponent;

public class Style {
	public static final int ANIMATION_DROP_IN_WORLD_MS = 200;
	public static final Color COLOR_BACKGROUND = Color.black;
	public static final Color COLOR_BACKGROUND2 = Color.darkGray;

	public static final Color COLOR_FOREGROUND = Color.white;
	public static final Color COLOR_DISABLED = Color.gray;
	public static final Color COLOR_MENU_BACKGROUND = Color.black;
	public static final Color COLOR_DARKBORDER = Color.darkGray;

	public static final Color COLOR_SELECTED = new Color(100, 149, 237);
	public static final Color COLOR_BORDER_DRAGGED = Color.red;
	public static final Color COLOR_BORDER_CONTEXT = Color.BLUE;

	public static final Color COLOR_WARNING = new Color(150, 0, 0);

	/*
	 * Button Colors
	 */
	public static final Color COLOR_BUTTON_HIGHLIGHT = Color.darkGray;
	public static final Color COLOR_BUTTON_SELECTED = Color.gray;
	public static final Color COLOR_BUTTON_BORDER = Color.darkGray;

	/*
	 * Line Colors
	 */
	public static final Color COLOR_LINEEND = Color.gray;
	public static final Color COLOR_LINEIN = Color.gray;

	/*
	 * Fonts
	 */

	public static final Font FONT_BUTTONS = new Font("Helvetica", Font.PLAIN,
			14);
	public static final Font FONT_SMALL = new Font("Helvetica", Font.PLAIN, 10);
	public static final Font FONT_NORMAL = new Font("Helvetica", Font.PLAIN, 14);
	public static final Font FONT_BIG = new Font("Helvetica", Font.BOLD, 16);
	public static final Font FONT_LARGE = new Font("Helvetica", Font.BOLD, 18);
	public static final Font FONT_XLARGE = new Font("Helvetica", Font.BOLD, 22);
	public static final Font FONT_XXLARGE = new Font("Helvetica", Font.BOLD, 32);

	public static Font createFont(int size) {
		return createFont(size, false);
	}

	public static Font createFont(int size, boolean isBold) {
		return new Font("Arial", Font.BOLD, size);

	}

	public static void applyStyleToComponent(JComponent item) {
		item.setOpaque(true);
		item.setBackground(Style.COLOR_BACKGROUND);
		item.setForeground(Style.COLOR_FOREGROUND);
	}
}

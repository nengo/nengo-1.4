package ca.neo.ui.style;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JComponent;

public class Style {
	public static final int ANIMATION_DROP_IN_WORLD_MS = 200; 
	public static final Color BACKGROUND_COLOR = Color.black;
	public static final Color BACKGROUND2_COLOR = Color.darkGray;

	public static final Color FOREGROUND_COLOR = Color.white;
	public static final Color DISABLED_COLOR = Color.gray;
	public static final Color MENU_BACKGROUND_COLOR = Color.black;
	public static final Color DARK_BORDER_COLOR = Color.darkGray;
	public static final Color SELECTED_BORDER_COLOR = new Color(100, 149, 237);

	public static final Color COLOR_BORDER_DRAGGED = Color.red;
	public static final Color COLOR_BORDER_CONTEXT = Color.BLUE;

	public static final Color WARNING_COLOR = new Color(150, 0, 0);

	/*
	 * Button Colors
	 */
	public static final Color BUTTON_HIGHLIGHT_COLOR = Color.darkGray;
	public static final Color BUTTON_SELECTED_COLOR = Color.gray;
	public static final Color BUTTON_BORDER_COLOR = Color.darkGray;

	/*
	 * Line Colors
	 */
	public static final Color LINEEND_COLOR = Color.gray;
	public static final Color LINEIN_COLOR = Color.gray;

	/*
	 * Fonts
	 */

	public static final Font FONT_BUTTONS = new Font("Helvetica", Font.PLAIN, 14);
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

	/*
	 * Photo Controller
	 */
	public static final Font GPHOTO_DETAILS_FONT = new Font("Arial", Font.BOLD,
			14);

	public static void applyStyleToComponent(JComponent item) {
		item.setOpaque(true);
		item.setBackground(Style.BACKGROUND_COLOR);
		item.setForeground(Style.FOREGROUND_COLOR);
	}
}

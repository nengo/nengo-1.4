package ca.neo.ui.models.icons;

import java.awt.Color;

import ca.neo.ui.models.UIModel;
import ca.shu.ui.lib.Style.Style;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Icon for a Simulator Probe
 * 
 * @author Shu Wu
 */
public class ProbeIcon extends ModelIcon {

	public static Color DEFAULT_COLOR = Style.COLOR_LIGHT_PURPLE;

	private static final long serialVersionUID = 1L;

	public ProbeIcon(UIModel parent) {
		super(parent, new Triangle());

	}

	public void setColor(Color color) {
		getIconReal().setPaint(color);
	}

}

/**
 * Icon which is basically a right-facing equilateral triangle
 * 
 * @author Shu Wu
 */
class Triangle extends PPath {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static final double probeIconSize = 20;

	public Triangle() {
		super();

		float x = 0;
		float y = 0;

		moveTo(x, y);

		x -= probeIconSize * Math.cos(Math.PI / 6);
		y -= probeIconSize * Math.sin(Math.PI / 6);

		lineTo(x, y);

		y += probeIconSize;

		lineTo(x, y);
		closePath();

		setPaint(Style.COLOR_LIGHT_PURPLE);

	}

}

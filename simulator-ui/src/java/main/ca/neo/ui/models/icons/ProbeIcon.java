package ca.neo.ui.models.icons;

import ca.neo.ui.models.UIModel;
import ca.shu.ui.lib.Style.Style;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Icon for a Simulator Probe
 * 
 * @author Shu Wu
 * 
 */
public class ProbeIcon extends ModelIcon {

	private static final long serialVersionUID = 1L;

	public ProbeIcon(UIModel parent) {
		super(parent, new IconNode());

	}

}

/**
 * Icon which is basically a right-facing equilateral triangle
 * 
 * @author Shu Wu
 * 
 */
class IconNode extends PPath {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static final double probeIconSize = 20;

	public IconNode() {
		super();
		// TODO Auto-generated constructor stub
		// setFont(Style.createFont(30, true));

		// PPath result = new PPath();

		float x = 0;
		float y = 0;

		moveTo(x, y);

		x -= probeIconSize * Math.cos(Math.PI / 6);
		y -= probeIconSize * Math.sin(Math.PI / 6);

		lineTo(x, y);

		y += probeIconSize;

		lineTo(x, y);
		closePath();
		// result.moveTo(x1, y1);

		// result.lineTo(x2, y2);
		setPaint(Style.COLOR_LIGHT_PURPLE);

	}

}

package ca.neo.ui.models.nodes.widgets;

import ca.neo.ui.models.PModel;
import ca.neo.ui.models.icons.ModelIcon;
import ca.neo.ui.style.Style;
import edu.umd.cs.piccolo.nodes.PPath;

public class ProbeIcon extends ModelIcon {

	public ProbeIcon(PModel parent) {
		super(parent, new IconNode());
		// TODO Auto-generated constructor stub

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

class IconNode extends PPath {
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
		setPaint(Style.COLOR_SELECTED);

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

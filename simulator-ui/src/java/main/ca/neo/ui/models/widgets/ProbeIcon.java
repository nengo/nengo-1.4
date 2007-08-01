package ca.neo.ui.models.widgets;

import ca.neo.ui.models.PModel;
import ca.neo.ui.models.icons.IconWrapper;
import ca.neo.ui.style.Style;
import ca.shu.ui.lib.objects.GText;
import edu.umd.cs.piccolo.nodes.PImage;

public class ProbeIcon extends IconWrapper {

	public ProbeIcon(PModel parent) {
		super(parent, new IconNode(), 0.7f);
		// TODO Auto-generated constructor stub

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

class IconNode extends GText {

	public IconNode() {
		super("P");
		// TODO Auto-generated constructor stub
		setFont(Style.createFont(30, true));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

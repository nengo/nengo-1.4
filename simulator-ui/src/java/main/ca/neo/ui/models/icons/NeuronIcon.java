package ca.neo.ui.models.icons;

import ca.neo.ui.models.PModel;
import ca.neo.ui.style.Style;
import edu.umd.cs.piccolo.nodes.PPath;

public class NeuronIcon extends IconWrapper {
	public NeuronIcon(PModel parent) {
		super(parent, PPath.createEllipse(0, 0, 50, 50));
		getIconReal().setPaint(Style.COLOR_FOREGROUND);
		configureLabel(false);

	}

	private static final long serialVersionUID = 1L;

}

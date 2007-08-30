package ca.neo.ui.models.icons;

import ca.neo.ui.models.UIModel;
import ca.shu.ui.lib.Style.Style;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Icon for a neuron
 * 
 * @author Shu Wu
 * 
 */
public class NeuronIcon extends ModelIcon {
	private static final long serialVersionUID = 1L;

	public NeuronIcon(UIModel parent) {
		super(parent, PPath.createEllipse(0, 0, 50, 50));
		getIconReal().setPaint(Style.COLOR_FOREGROUND);
		configureLabel(false);

	}

}

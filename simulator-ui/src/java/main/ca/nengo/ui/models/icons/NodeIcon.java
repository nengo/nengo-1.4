package ca.nengo.ui.models.icons;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.objects.models.ModelObject;
import ca.shu.ui.lib.world.piccolo.primitives.Path;

/**
 * Icon for a neuron
 * 
 * @author Shu Wu
 */
public class NodeIcon extends ModelIcon {
	private static final long serialVersionUID = 1L;

	public NodeIcon(ModelObject parent) {
		super(parent, Path.createEllipse(0, 0, 50, 50));
		getIconReal().setPaint(Style.COLOR_FOREGROUND);
		configureLabel(false);

	}
}

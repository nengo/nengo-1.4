package ca.shu.ui.lib.handlers;

import ca.shu.ui.lib.world.impl.WorldImpl;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PZoomEventHandler;

public class ClickHandler extends PBasicInputEventHandler {
	WorldImpl world;

	public ClickHandler(WorldImpl world) {
		super();
		this.world = world;
	}

	@Override
	public void mouseClicked(PInputEvent event) {
		if (event.getClickCount() == 2) {
			PNode node = event.getPickedNode();

			while (node != null) {
				if (node instanceof WorldObjectImpl) {

					WorldObjectImpl wo = (WorldObjectImpl) node;

					wo.doubleClicked();

					break;
				}
				node = node.getParent();
			}

		}
		super.mouseClicked(event);

	}

}

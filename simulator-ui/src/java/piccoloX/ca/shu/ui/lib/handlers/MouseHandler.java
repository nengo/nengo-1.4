package ca.shu.ui.lib.handlers;

import ca.shu.ui.lib.world.impl.World;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PZoomEventHandler;

public class MouseHandler extends PBasicInputEventHandler {
	World world;
	
	
	public MouseHandler(World world) {
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

					if (world.containsNode(wo)) // only
						world.zoomToNode(wo);

					break;
				}
				node = node.getParent();
			}

		}
		super.mouseClicked(event);
		

	}

}

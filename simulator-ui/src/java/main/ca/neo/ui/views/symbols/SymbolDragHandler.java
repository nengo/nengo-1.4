package ca.neo.ui.views.symbols;

import java.awt.geom.Point2D;
import java.util.Iterator;

import ca.sw.graphics.nodes.WorldObject;
import ca.sw.graphics.world.WorldGround;

import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

public class SymbolDragHandler extends PDragSequenceEventHandler {
	WorldObject wo;

	public SymbolDragHandler(WorldObject parentNode) {
		super();
		this.wo = parentNode;
	}

	@Override
	protected void drag(PInputEvent e) {
		// TODO Auto-generated method stub
		super.drag(e);
		PDimension d = e.getCanvasDelta();
		symbolBeingDragged.localToParent(d);
		symbolBeingDragged.offset(d.getWidth(), d.getHeight());

		e.setHandled(true);
	}

	@Override
	protected void endDrag(PInputEvent e) {
		// TODO Auto-generated method stub
		super.endDrag(e);
		
		Point2D position = symbolBeingDragged.getWorld().getPositionInGround(symbolBeingDragged);
		
		ground.addChild(symbolBeingDragged);
		
		symbolBeingDragged.setOffset(position);
	}

	WorldObject symbolBeingDragged = null;
	WorldGround ground;
	

	@Override
	protected void startDrag(PInputEvent e) {

		Iterator it = e.getPath().getNodeStackReference().iterator();

		while (it.hasNext()) {
			Object node = it.next();

			if (node instanceof ISymbol) {
				// TODO Auto-generated method stub
				super.startDrag(e);

				WorldObject wo = (WorldObject) node;
				Point2D position = wo.getWorld().getPositionInSky(wo);
				ground = wo.getWorld().getGround();
				
				symbolBeingDragged = (WorldObject) (wo.clone());			
			
				
				

				symbolBeingDragged.setOffset(position);

				wo.getWorld().addToSky((symbolBeingDragged));
			}
		}

	}

}

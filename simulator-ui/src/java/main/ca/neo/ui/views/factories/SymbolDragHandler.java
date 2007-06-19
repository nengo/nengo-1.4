//package ca.neo.ui.views.factories;
//
//import java.awt.geom.Point2D;
//import java.util.Iterator;
//
//import ca.neo.ui.util.StyleConstants;
//import ca.neo.ui.views.objects.proxies.ProxyGeneric;
//import ca.sw.graphics.nodes.WorldObject;
//import ca.sw.graphics.world.World;
//import edu.umd.cs.piccolo.activities.PActivity;
//import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
//import edu.umd.cs.piccolo.event.PInputEvent;
//import edu.umd.cs.piccolo.util.PDimension;
//
//public class SymbolDragHandler extends PDragSequenceEventHandler {
//	Symbol symbolBeingDragged = null;
//
//	WorldObject wo;
//
//	World world;
//
//	public SymbolDragHandler(WorldObject parentNode) {
//		super();
//		this.wo = parentNode;
//	}
//
//	@Override
//	protected void drag(PInputEvent e) {
//		// TODO Auto-generated method stub
//		super.drag(e);
//		PDimension d = e.getCanvasDelta();
//		
//		symbolBeingDragged.localToParent(d);
//		symbolBeingDragged.offset(d.getWidth(), d.getHeight());
//		
//		e.setHandled(true);
//	}
//
//	@Override
//	protected void endDrag(PInputEvent e) {
//		// TODO Auto-generated method stub
//		super.endDrag(e);
//		world.addActivity(new DropActivity(symbolBeingDragged,
//				StyleConstants.ANIMATION_DROP_IN_WORLD_MS));
//	}
//
//	@Override
//	protected void startDrag(PInputEvent e) {
//
//		Iterator it = e.getPath().getNodeStackReference().iterator();
//
//		while (it.hasNext()) {
//			Object node = it.next();
//
//			if (node instanceof Symbol) {
//				// TODO Auto-generated method stub
//				super.startDrag(e);
//
//				WorldObject wo = (WorldObject) node;
//				Point2D position = wo.getWorld().getPositionInSky(wo);
//				world = wo.getWorld();
//
//				symbolBeingDragged = (Symbol) (wo.clone());
//
//				symbolBeingDragged.setSelected(true);
//
//				symbolBeingDragged.setOffset(position);
//
//				wo.getWorld().addToSky((symbolBeingDragged));
//			}
//		}
//
//	}
//
//}
//
/////*
//// * Creates the representation of the symbol and drops it into the ground of the
//// * World
//// * 
//// */
////class SymbolDropActivity extends PActivity {
////	Symbol symbol;
////
////	World world;
////
////	public SymbolDropActivity(Symbol nodeToDrop, long aDuration) {
////		super(aDuration, aDuration);
////
////		this.symbol = nodeToDrop;
////		world = nodeToDrop.getWorld();
////	}
////
////	@Override
////	protected void activityFinished() {
////		// TODO Auto-generated method stub
////		super.activityFinished();
////		Point2D position = symbol.getWorld().getPositionInGround(symbol);
////
////		ProxyGeneric repNode = symbol.createRepresentation();
////
////		symbol.removeFromParent();
////		repNode.setOffset(position);
////
////		world.getGround().addChild(repNode);
////		repNode.initProxy();
////
////	}
////
////	@Override
////	protected void activityStarted() {
////		// TODO Auto-generated method stub
////		super.activityStarted();
////		symbol.animateToPositionScaleRotation(symbol.getOffset().getX(), symbol
////				.getOffset().getY(), world.getViewScale(), 0, getDuration());
////		symbol.setSelected(false);
////	}
////
////}
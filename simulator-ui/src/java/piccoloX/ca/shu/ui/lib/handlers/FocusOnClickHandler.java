package ca.shu.ui.lib.handlers;

// package ca.sw.handlers;
//
// import edu.umd.cs.piccolo.PCamera;
// import edu.umd.cs.piccolo.PNode;
// import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
// import edu.umd.cs.piccolo.event.PInputEvent;
//
// public class FocusOnClickHandler extends PBasicInputEventHandler {
// PCamera camera;
//
// public FocusOnClickHandler(PCamera camera) {
// super();
// this.camera = camera;
// }
//
// @Override
// public void mouseClicked(PInputEvent event) {
// // TODO Auto-generated method stub
// super.mouseClicked(event);
//
// PNode node = event.getPickedNode();
// if (event.getClickCount() == 2) {
// camera.animateViewToCenterBounds(node.localToGlobal(node
// .getBounds()), false, 1000);
// }
// // camera.animateViewToPanToBounds(node.getBounds(), 1000);
//
// }
//
// }

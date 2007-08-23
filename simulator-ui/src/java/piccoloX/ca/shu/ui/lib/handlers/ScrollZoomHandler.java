package ca.shu.ui.lib.handlers;

import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

public class ScrollZoomHandler extends PBasicInputEventHandler {
	double maxScale = 5;

	double minScale = 0.05;

	PActivity zoomActivity;

	@Override
	public void mouseWheelRotated(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mouseWheelRotated(event);
		int rotationAmount = event.getWheelRotation() * -1;

		double scaleDelta = 1 + (0.2 * rotationAmount);

		// viewTransform.scaleAboutPoint(scale, x, y);
		if (zoomActivity != null) {
			zoomActivity.terminate(PActivity.TERMINATE_AND_FINISH);
		}

		PCamera camera = event.getCamera();
		double currentScale = camera.getViewScale();
		double newScale = currentScale * scaleDelta;

		if (newScale < minScale) {
			scaleDelta = minScale / currentScale;
		}
		if ((maxScale > 0) && (newScale > maxScale)) {
			scaleDelta = maxScale / currentScale;
		}

		Point2D viewZoomPoint = event.getPosition();

		// PAffineTransform transform = camera.getViewTransform();
		// transform.scaleAboutPoint(scaleDelta, viewZoomPoint.getX(),
		// viewZoomPoint.getY());
		//
		// zoomActivity = camera.animateViewToTransform(transform, 100);

		// event.getCamera().scaleView()
		// event.getCamera().scaleView(
		// event.getCamera().getViewScale() * scaleDelta);
		event.getCamera().scaleViewAboutPoint(scaleDelta, viewZoomPoint.getX(),
				viewZoomPoint.getY());
	}

}

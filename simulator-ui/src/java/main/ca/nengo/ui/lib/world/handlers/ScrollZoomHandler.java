package ca.nengo.ui.lib.world.handlers;

import java.awt.geom.Point2D;

import ca.nengo.ui.lib.world.WorldSky;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Zooms the world using the scroll wheel.
 * 
 * @author Shu Wu
 */
public class ScrollZoomHandler extends PBasicInputEventHandler {

	@Override
	public void mouseWheelRotated(PInputEvent event) {

		int rotationAmount = event.getWheelRotation() * -1;

		double scaleDelta = 1 + (0.2 * rotationAmount);

		PCamera camera = event.getCamera();
		double currentScale = camera.getViewScale();
		double newScale = currentScale * scaleDelta;

		if (newScale < WorldSky.MIN_ZOOM_SCALE) {
			scaleDelta = WorldSky.MIN_ZOOM_SCALE / currentScale;
		}
		if (newScale > WorldSky.MAX_ZOOM_SCALE) {
			scaleDelta = WorldSky.MAX_ZOOM_SCALE / currentScale;
		}

		Point2D viewZoomPoint = event.getPosition();

		event.getCamera().scaleViewAboutPoint(scaleDelta, viewZoomPoint.getX(),
				viewZoomPoint.getY());
	}

}

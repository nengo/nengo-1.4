package ca.neo.ui.models.icons;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.net.URL;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.util.UIEnvironment;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Just like PImage, except it semantically zooms (ie. at low scales, it does
 * not paint its bitmap)
 * 
 * @author Shu
 * 
 */
public class IconImage extends PImage {

	private static final long serialVersionUID = 1L;
	public static final boolean ENABLE_SEMANTIC_ZOOM = false;

	public IconImage() {
		super();
		init();
	}

	private transient GeneralPath path;

	private void init() {
		path = new GeneralPath();

	}

	private void updatePath(double scale) {
		path.reset();
		TEMP_ELLIPSE.setFrame(0, 0, 50 * scale, 50 * scale);
		path.append(TEMP_ELLIPSE, false);

	}

	public IconImage(Image arg0) {
		super(arg0);
		init();
	}

	public IconImage(String arg0) {
		super(arg0);
		init();
	}

	public IconImage(URL arg0) {
		super(arg0);
		init();
	}

	private static final Ellipse2D.Float TEMP_ELLIPSE = new Ellipse2D.Float();

	double prevScale = 0;

	@Override
	protected void paint(PPaintContext aPaintContext) {
		double s = aPaintContext.getScale();

		Graphics2D g2 = aPaintContext.getGraphics();

		if (ENABLE_SEMANTIC_ZOOM && s < UIEnvironment.SEMANTIC_ZOOM_LEVEL) {
			if (s != prevScale) {
				double delta = 1 - ((UIEnvironment.SEMANTIC_ZOOM_LEVEL - s) / UIEnvironment.SEMANTIC_ZOOM_LEVEL);

				updatePath(1 / delta);
			}
			g2.setPaint(Style.COLOR_FOREGROUND);
			g2.fill(path);

			// g2.fill(getBoundsReference());

		} else {
			super.paint(aPaintContext);
		}

	}
}

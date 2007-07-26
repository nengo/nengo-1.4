package ca.shu.ui.lib.objects.widgets;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.util.PPaintContext;

public class TransparentCamera extends PCamera {
	float transparency;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AlphaComposite makeComposite(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return (AlphaComposite.getInstance(type, alpha));
	}

	// Nodes that override the visual representation of their super
	// class need to override a paint method.
	public void paint(PPaintContext aPaintContext) {
		Graphics2D g2 = aPaintContext.getGraphics();
		Composite originalComposite = g2.getComposite();
		g2.setComposite(makeComposite((float) transparency));

		super.paint(aPaintContext);

		g2.setComposite(originalComposite);
	}


	public TransparentCamera(float transparency) {
		super();
		this.transparency = transparency;
	}

}

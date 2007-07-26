package ca.shu.ui.lib.objects.widgets;

import ca.shu.ui.lib.objects.Button;
import edu.umd.cs.piccolo.nodes.PImage;


public class ImageButton extends Button {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ImageButton(String imgPath, Runnable action) {
		super(action);
		
		PImage buttonImg = new PImage(imgPath);
		addChild(buttonImg);
		this.setWidth(getFullBounds().getWidth());
		this.setHeight(getFullBounds().getHeight());
	}
	
}

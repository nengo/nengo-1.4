package ca.neo.ui.views.objects;

import ca.sw.graphics.basics.GDefaults;
import ca.sw.graphics.nodes.WorldObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

public class LineEndWell extends WorldObject {

	public LineEndWell() {
		super();

		PNode icon = PPath.createEllipse(0, 0, 30, 30);
		icon.setPaint(GDefaults.FOREGROUND_COLOR);
		
		addChild(icon);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
}

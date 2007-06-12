package ca.neo.ui.views.icons;

import ca.sw.graphics.basics.GDefaults;
import ca.sw.graphics.basics.GText;
import edu.umd.cs.piccolo.PNode;

public class FunctionIcon extends Icon {

	public FunctionIcon() {
		super(new IconNode(), "Function");
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	



}

class IconNode extends GText {

	public IconNode() {
		super("F");
		// TODO Auto-generated constructor stub
		setFont(GDefaults.createFont(30, true));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}

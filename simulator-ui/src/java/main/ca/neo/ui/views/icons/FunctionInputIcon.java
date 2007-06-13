package ca.neo.ui.views.icons;

import ca.sw.graphics.basics.GDefaults;
import ca.sw.graphics.basics.GText;
import edu.umd.cs.piccolo.PNode;

public class FunctionInputIcon extends Icon {

	public FunctionInputIcon() {
		super(new IconNode(), "FunctionInput");
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

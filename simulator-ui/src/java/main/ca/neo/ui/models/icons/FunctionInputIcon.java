package ca.neo.ui.models.icons;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.objects.GText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

public class FunctionInputIcon extends Icon {

	public FunctionInputIcon() {
		super(new PImage("images/FunctionIcon.gif"), "FunctionInput", 0.7f);
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
		setFont(Style.createFont(30, true));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

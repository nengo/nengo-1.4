package ca.shu.ui.lib.objects;

import ca.neo.ui.style.Style;
import edu.umd.cs.piccolo.nodes.PText;

public class GText extends PText {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GText(String aText) {
		
		super(aText);
		
		this.setFont(Style.FONT_NORMAL);
		this.setTextPaint(Style.FOREGROUND_COLOR);
		this.setConstrainWidthToTextWidth(false);
		this.setGreekThreshold(2);
	}

	
}

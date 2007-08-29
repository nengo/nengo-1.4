package ca.neo.ui.models.icons;

import ca.neo.ui.models.UIModel;

/**
 * Icon for an Function Input
 * 
 * @author Shu Wu
 * 
 */
public class FunctionInputIcon extends ModelIcon {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FunctionInputIcon(UIModel parent) {
		super(parent, new IconImage("images/FunctionIcon.gif"));
		this.getIconReal().setScale(0.7f);

	}

}

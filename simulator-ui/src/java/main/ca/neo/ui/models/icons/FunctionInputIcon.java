package ca.neo.ui.models.icons;

import ca.shu.ui.lib.objects.models.ModelObject;

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

	public FunctionInputIcon(ModelObject parent) {
		super(parent, new IconImage("images/neoIcons/FunctionIcon.gif"));
		this.getIconReal().setScale(0.7f);

	}

}

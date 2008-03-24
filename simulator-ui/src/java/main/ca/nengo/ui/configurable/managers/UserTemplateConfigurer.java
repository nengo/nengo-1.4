package ca.nengo.ui.configurable.managers;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;

import ca.nengo.ui.configurable.IConfigurable;
import ca.shu.ui.lib.util.Util;

/**
 * A lot like UserConfigurer, except it allows the user to use templates to save
 * and re-use values
 * 
 * @author Shu Wu
 */
public class UserTemplateConfigurer extends UserConfigurer {
	/**
	 * Name of the default template
	 */
	public static final String DEFAULT_TEMPLATE_NAME = "last_used";

	private boolean isTemplateEditable;

	public UserTemplateConfigurer(IConfigurable configurable) {
		super(configurable);
		init(true);
	}

	public UserTemplateConfigurer(IConfigurable configurable, Container parent) {
		super(configurable, parent);
		init(true);
	}

	public UserTemplateConfigurer(IConfigurable configurable, Container parent,
			boolean isTemplateEditable) {
		super(configurable, parent);
		init(isTemplateEditable);
	}

	private void init(boolean isTemplateEditable) {
		this.isTemplateEditable = isTemplateEditable;
	}

	@Override
	protected ConfigDialog createConfigDialog() {
		if (parent instanceof Frame) {

			return new ConfigTemplateDialog(this, (Frame) parent);
		} else if (parent instanceof Dialog) {
			return new ConfigTemplateDialog(this, (Dialog) parent);
		} else {
			Util
					.Assert(false,
							"Could not create config dialog because parent type if not supported");

		}
		return null;

	}

	public boolean isTemplateEditable() {
		return isTemplateEditable;
	}

}

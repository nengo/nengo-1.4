package ca.neo.ui.configurable.managers;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;

import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.IConfigurable;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertySet;
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

	public static Object configure(PropertyDescriptor prop, String typeName,
			Container parent) throws ConfigException {

		PropertySet properties = configure(new PropertyDescriptor[] { prop },
				typeName, parent);
		return properties.getProperty(prop);
	}

	public static PropertySet configure(PropertyDescriptor[] props,
			String typeName, Container parent) throws ConfigException {

		Configureable configurable = new Configureable(props, typeName);

		UserTemplateConfigurer configurer = new UserTemplateConfigurer(
				configurable, parent, false);

		configurer.configureAndWait();

		return configurable.getProperties();
	}

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

	private static class Configureable implements IConfigurable {

		private PropertySet properties;

		private PropertyDescriptor[] props;
		private String typeName;

		public Configureable(PropertyDescriptor[] props, String typeName) {
			super();
			this.props = props;
			this.typeName = typeName;
		}

		public void completeConfiguration(PropertySet props)
				throws ConfigException {
			properties = props;

		}

		public PropertyDescriptor[] getConfigSchema() {
			return props;
		}

		public PropertySet getProperties() {
			return properties;
		}

		public String getTypeName() {
			return typeName;
		}

	}
}

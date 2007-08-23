package ca.neo.ui.configurable;

import java.io.Serializable;

import ca.neo.ui.configurable.struct.PropDescriptor;
import ca.shu.ui.lib.util.Util;

public class PropertyWrapper implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	PropDescriptor metaProperty;

	Object value;

	public PropertyWrapper(PropDescriptor metaProperty) {
		this(metaProperty, null);

	}

	public PropertyWrapper(PropDescriptor metaProperty, Object value) {
		super();

		this.metaProperty = metaProperty;

		setValue(value);

	}

	public String getName() {
		return metaProperty.getName();
	}

	public Object getValue() {
		return value;
	}

	public boolean isValueSet() {
		return (value != null);
	}

	public void setValue(Object value) {
		if (value != null) {
			if (metaProperty.getTypeClass().isInstance(value)) {
				this.value = value;
			} else {
				Util.UserError("invalid property type");
			}
		}
	}

}

package ca.neo.ui.views.objects.configurable;

import java.io.Serializable;

import ca.neo.ui.views.objects.configurable.struct.PropertyStructure;
import ca.shu.ui.lib.util.Util;

public class PropertyWrapper implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	PropertyStructure metaProperty;

	Object value;

	public PropertyWrapper(PropertyStructure metaProperty) {
		this(metaProperty, null);

	}

	public PropertyWrapper(PropertyStructure metaProperty, Object value) {
		super();

		this.metaProperty = metaProperty;

		setValue(value);

	}

	public Object getValue() {
		return value;
	}

	public boolean isValueSet() {
		return (value != null);
	}

	public String getName() {
		return metaProperty.getName();
	}


	public void setValue(Object value) {
		if (value != null) {
			if (metaProperty.getTypeClass().isInstance(value)) {
				this.value = value;
			} else {
				Util.Error("invalid property type");
			}
		}
	}

}

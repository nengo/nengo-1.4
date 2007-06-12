package ca.neo.ui.views.objects;

import java.io.Serializable;

import ca.sw.util.Util;

public class PropertyWrapper implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	MetaProperty metaProperty;

	Object value;

	public PropertyWrapper(MetaProperty metaProperty) {
		this(metaProperty, null);

	}

	public PropertyWrapper(MetaProperty metaProperty, Object value) {
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

	public Class getPropertyType() {
		return metaProperty.getType();
	}
	
	public void setValue(Object value) {
		if (value != null) {
			if (metaProperty.getType().isInstance(value)) {
				this.value = value;
			} else {
				Util.Error("invalid property type");
			}
		}
	}

}

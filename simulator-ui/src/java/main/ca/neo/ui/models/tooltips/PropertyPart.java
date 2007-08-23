package ca.neo.ui.models.tooltips;

/**
 * @author Shu
 * 
 */
public class PropertyPart extends TooltipPart {
	String propertyName;
	String propertyValue;

	public PropertyPart(String propertyName, String propertyValue) {
		super();
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
	}

	public String toString() {
		return propertyName + ": " + propertyValue;
	}

	protected String getPropertyName() {
		return propertyName;
	}

	protected String getPropertyValue() {
		return propertyValue;
	}
}
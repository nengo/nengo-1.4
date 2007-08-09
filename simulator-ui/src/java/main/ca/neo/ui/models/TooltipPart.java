package ca.neo.ui.models;

/**
 * @author Shu
 * 
 */
public class TooltipPart {
	String propertyName;
	String propertyValue;

	public TooltipPart(String propertyName, String propertyValue) {
		super();
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
	}

	protected String getPropertyName() {
		return propertyName;
	}

	protected String getPropertyValue() {
		return propertyValue;
	}
}
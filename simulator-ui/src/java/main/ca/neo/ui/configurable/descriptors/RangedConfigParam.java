package ca.neo.ui.configurable.descriptors;

import ca.neo.ui.configurable.Property;

/**
 * A Config Descriptor which can have a confined integer range
 * 
 * @author Shu Wu
 */
public abstract class RangedConfigParam extends Property {
	/**
	 * Whether to check the range of the Integer value
	 */
	private boolean checkRange = false;

	private int min, max;

	public RangedConfigParam(String name, Object defaultValue) {
		super(name, defaultValue);
	}

	public RangedConfigParam(String name) {
		super(name);
	}

	/**
	 * @param name
	 *            Name of the Config Descriptor
	 * @param defaultValue
	 *            default value
	 * @param min
	 *            Min value
	 * @param max
	 *            Max value
	 */
	public RangedConfigParam(String name, int defaultValue, int min, int max) {
		super(name + " (" + min + " to " + max + ")", defaultValue);
		this.min = min;
		this.max = max;
		checkRange = true;
	}

	/**
	 * @return Max value
	 */
	public int getMax() {
		return max;
	}

	/**
	 * @return Min value
	 */
	public int getMin() {
		return min;
	}

	/**
	 * @return Whether range checking is enabled
	 */
	public boolean isCheckRange() {
		return checkRange;
	}
}

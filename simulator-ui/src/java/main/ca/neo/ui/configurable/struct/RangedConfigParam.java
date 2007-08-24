package ca.neo.ui.configurable.struct;

import ca.neo.ui.configurable.ConfigParamDescriptor;

public abstract class RangedConfigParam extends ConfigParamDescriptor {
	private boolean checkRange = false;

	private int min, max;

	public RangedConfigParam(String name) {
		super(name);
	}

	public RangedConfigParam(String name, int min, int max) {
		super(name + " (" + min + " to " + max + ")");
		this.min = min;
		this.max = max;
		checkRange = true;
	}

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}

	public boolean isCheckRange() {
		return checkRange;
	}
}

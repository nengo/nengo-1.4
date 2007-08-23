package ca.neo.ui.configurable.struct;

import ca.neo.ui.configurable.inputPanels.IntegerInputPanel;

public class PTInt extends PropDescriptor {

	private static final long serialVersionUID = 1L;

	private boolean checkRange = false;

	private int min, max;

	public PTInt(String name) {
		super(name);
	}

	public PTInt(String name, int min, int max) {
		super(name + " (" + min + " to " + max + ")");
		this.min = min;
		this.max = max;
		checkRange = true;
	}

	@Override
	public IntegerInputPanel createInputPanel() {
		return new IntegerInputPanel(this);
	}

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}

	@Override
	public Class<Integer> getTypeClass() {
		return Integer.class;
	}

	@Override
	public String getTypeName() {
		return "Integer";
	}

	public boolean isCheckRange() {
		return checkRange;
	}

}

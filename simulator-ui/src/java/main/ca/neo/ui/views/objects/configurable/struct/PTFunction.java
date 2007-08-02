package ca.neo.ui.views.objects.configurable.struct;

import ca.neo.math.Function;
import ca.neo.ui.models.functions.FunctionInputPanel;
import ca.neo.ui.views.objects.configurable.PropertyInputPanel;

public class PTFunction extends PropDescriptor {

	private static final long serialVersionUID = 1L;

	public PTFunction(String name) {
		super(name);
	}

	@Override
	public PropertyInputPanel createInputPanel() {
		return new FunctionInputPanel(this);
	}

	@Override
	public Class<Function> getTypeClass() {
		return Function.class;
	}

	@Override
	public String getTypeName() {
		return "Function";
	}

}

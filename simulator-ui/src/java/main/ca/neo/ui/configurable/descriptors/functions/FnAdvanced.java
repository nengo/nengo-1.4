package ca.neo.ui.configurable.descriptors.functions;

import java.awt.Dialog;

import ca.neo.config.ConfigUtil;
import ca.neo.config.ui.NewConfigurableDialog;
import ca.neo.math.Function;

public class FnAdvanced implements ConfigurableFunction {
	private Class<? extends Function> type;
	private Function myFunction;

	public FnAdvanced(Class<? extends Function> type) {
		super();
		this.type = type;
	}

	public Function configureFunction(Dialog parent) {
		if (myFunction == null) {
			myFunction = (Function) NewConfigurableDialog.showDialog(parent, type, type);
		} else {
			ConfigUtil.configure(parent, myFunction);
		}
		return myFunction;
	}

	public Class<? extends Function> getFunctionType() {
		return type;
	}

	public void setFunction(Function function) {
		if (function != null) {
			if (type.isInstance(function)) {
				myFunction = function;
			}
		} else {
			myFunction = null;
		}
	}

	@Override
	public String toString() {
		return "~" + type.getSimpleName();
	}

	public Function getFunction() {
		return myFunction;
	}
}

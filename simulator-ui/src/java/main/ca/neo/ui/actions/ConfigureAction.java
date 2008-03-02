package ca.neo.ui.actions;

import ca.neo.ui.NengoGraphics;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;

public class ConfigureAction extends StandardAction {

	private static final long serialVersionUID = 1L;

	private Object model;

	public ConfigureAction(Object model) {
		super("Configure");
		init(model);
	}

	public ConfigureAction(String actionName, Object model) {
		super("Configure", actionName);
		init(model);
	}

	private void init(Object model) {
		this.model = model;
	}

	@Override
	protected void action() throws ActionException {
		NengoGraphics.getInstance().configureObject(model);
	}
}
package ca.neo.ui.models;

import ca.neo.ui.actions.ConfigureAction;
import ca.shu.ui.lib.objects.models.ModelObject;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;

/**
 * UI Wrapper for a NEO Node Model
 * 
 * @author Shu
 */
public abstract class UINeoModel extends ModelObject {
	public UINeoModel(Object model) {
		super(model);
	}

	protected final void afterModelCreated() {
		/*
		 * Remove this funciton after
		 */
	}

	@Override
	protected void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);
		menu.addAction(new ConfigureAction("Configure", getModel()));
	}

	@Override
	public void altClicked() {
		(new ConfigureAction("Configure", getModel())).doAction();
	}
}

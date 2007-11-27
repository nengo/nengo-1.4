package ca.neo.ui.actions;

import javax.swing.JOptionPane;

import ca.neo.ui.models.UIModel;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.UIEnvironment;

/**
 * Action for removing this UI Wrapper and the model
 * 
 * @author Shu Wu
 */
public class RemoveModelAction extends StandardAction {

	private static final long serialVersionUID = 1L;
	UIModel modelToRemove;

	public RemoveModelAction(String actionName, UIModel modelToRemove) {
		super("Remove " + modelToRemove.getTypeName(), actionName);
		this.modelToRemove = modelToRemove;
	}

	@Override
	protected void action() throws ActionException {
		int response = JOptionPane.showConfirmDialog(UIEnvironment
				.getInstance(),
				"Once an object has been removed, it cannot be undone.",
				"Are you sure?", JOptionPane.YES_NO_OPTION);
		if (response == 0) {
			modelToRemove.destroy();
			modelToRemove = null;
		} else {
			throw new ActionException("Action cancelled by user", false);
		}
	}

}
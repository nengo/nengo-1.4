package ca.neo.ui.actions;

import javax.swing.JOptionPane;

import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.models.ModelObject;
import ca.shu.ui.lib.util.UIEnvironment;

/**
 * Action for removing this UI Wrapper and the model
 * 
 * @author Shu Wu
 */
public class RemoveModelAction extends StandardAction {

	private static final long serialVersionUID = 1L;
	ModelObject modelToRemove;

	public RemoveModelAction(String actionName, ModelObject modelToRemove) {
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
			modelToRemove.destroyModel();
			modelToRemove = null;
		} else {
			throw new ActionException("Action cancelled by user", false);
		}
	}

}
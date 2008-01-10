package ca.neo.ui.actions;

import java.util.Collection;

import javax.swing.JOptionPane;

import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.models.ModelObject;
import ca.shu.ui.lib.util.UIEnvironment;

/**
 * Action for removing a collection of UI Wrappers and their models
 * 
 * @author Shu Wu
 */
public class RemoveModelsAction extends StandardAction {

	private static final long serialVersionUID = 1L;
	private Collection<ModelObject> modelsToRemove;
	private String typeName;

	public RemoveModelsAction(Collection<ModelObject> modelsToRemove,
			String typeName) {
		super("Remove " + typeName + "s");
		this.modelsToRemove = modelsToRemove;
		this.typeName = typeName;
	}

	@Override
	protected void action() throws ActionException {
		int response = JOptionPane.showConfirmDialog(UIEnvironment
				.getInstance(), "Once these " + typeName
				+ "s have been removed, it cannot be undone.", "Are you sure?",
				JOptionPane.YES_NO_OPTION);
		if (response == 0) {
			for (ModelObject model : modelsToRemove) {
				model.destroy();
			}
		} else {
			throw new ActionException("Action cancelled by user", false);
		}
	}

}
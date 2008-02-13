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
	private Collection<ModelObject> objectsToRemove;
	private String typeName;
	private boolean showWarning;

	public RemoveModelsAction(Collection<ModelObject> objectsToRemove) {
		this(objectsToRemove, "Objects", false);
	}

	public RemoveModelsAction(Collection<ModelObject> objectsToRemove, String typeName,
			boolean showWarning) {
		super("Remove " + typeName + "s");
		this.objectsToRemove = objectsToRemove;
		this.typeName = typeName;
		this.showWarning = showWarning;
	}

	@Override
	protected void action() throws ActionException {
		boolean remove = true;
		if (showWarning) {
			int response = JOptionPane.showConfirmDialog(UIEnvironment.getInstance(), "Once these "
					+ typeName + "s have been removed, it cannot be undone.", "Are you sure?",
					JOptionPane.YES_NO_OPTION);

			if (response != 0) {
				remove = false;
			}
		}
		if (remove) {
			try {
				for (ModelObject model : objectsToRemove) {
					model.destroyModel();
				}
			} catch (Exception e) {
				throw new ActionException("Could not remove all objects: " + e.getMessage());
			}
		} else {
			throw new ActionException("Action cancelled by user", false);
		}
	}
}
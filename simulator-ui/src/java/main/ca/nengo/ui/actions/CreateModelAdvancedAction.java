package ca.nengo.ui.actions;

import ca.nengo.config.ui.NewConfigurableDialog;
import ca.nengo.model.Node;
import ca.nengo.ui.models.NodeContainer;
import ca.nengo.ui.models.UINeoNode;
import ca.nengo.ui.models.NodeContainer.ContainerException;
import ca.nengo.ui.models.nodes.UINodeViewable;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.util.UIEnvironment;

public class CreateModelAdvancedAction extends StandardAction {
	private Class<?> objType;
	private NodeContainer container;

	public CreateModelAdvancedAction(NodeContainer container, Class<?> objType) {
		super(objType.getSimpleName());
		this.objType = objType;
		this.container = container;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void action() throws ActionException {
		Object obj = NewConfigurableDialog.showDialog(UIEnvironment.getInstance(), objType, null);
		if (obj == null) {
			throw new UserCancelledException();
		} else if (obj instanceof Node) {
			Node node = (Node) obj;
			try {
				CreateModelAction.ensureNonConflictingName(node, container);
				UINeoNode nodeUI = container.addNodeModel(node);
				if (nodeUI instanceof UINodeViewable) {
					((UINodeViewable) (nodeUI)).openViewer();
				}
			} catch (ContainerException e) {
				throw new ActionException(e.getMessage(), e);
			}
		} else {
			throw new ActionException("Sorry we do not support adding that type of object yet");
		}

	}
}
package ca.neo.ui.actions;

import ca.neo.config.ui.NewConfigurableDialog;
import ca.neo.model.Node;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.INodeContainer.ContainerException;
import ca.neo.ui.models.nodes.NodeContainer;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.util.UIEnvironment;

public class CreateModelAdvancedAction extends StandardAction {
	private Class<?> objType;
	private INodeContainer container;

	public CreateModelAdvancedAction(INodeContainer container, Class<?> objType) {
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
			try {
				UINeoNode nodeUI = container.addNodeModel((Node) obj);
				if (nodeUI instanceof NodeContainer) {
					((NodeContainer) (nodeUI)).openViewer();
				}
			} catch (ContainerException e) {
				throw new ActionException(e.getMessage());
			}
		} else {
			throw new ActionException(
					"Sorry we do not support adding that type of object into NeoGraphics yet");
		}

	}
}
package ca.neo.ui.actions;

import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.nodes.PNodeContainer;
import ca.neo.ui.views.objects.configurable.managers.UserConfig;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;

public class CreateModelAction extends ReversableAction {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	Class nc;
	INodeContainer nodeContainer;

	PNeoNode nodeAdded;

	/**
	 * 
	 * 
	 * @param nodeContainer
	 *            The container to which the created node should be added to
	 * @param nodeUIType
	 *            Type of Node to be create, such as PNetwork
	 */
	@SuppressWarnings("unchecked")
	public CreateModelAction(INodeContainer nodeContainer, Class nodeUIType) {
		this(nodeUIType.getSimpleName(), nodeContainer, nodeUIType);
	}

	/**
	 * @param nodeContainer
	 *            The container to which the created node should be added to
	 * @param nodeUIType
	 *            Type of Node to be create, such as PNetwork
	 * 
	 */
	@SuppressWarnings("unchecked")
	public CreateModelAction(String actionName, INodeContainer nodeContainer,
			Class nodeUIType) {
		super("Create new " + nodeUIType.getSimpleName(), actionName);
		this.nodeContainer = nodeContainer;
		this.nc = nodeUIType;
	}

	@Override
	protected void action() throws ActionException {

		PNeoNode nodeProxy = null;
		try {

			nodeProxy = (PNeoNode) nc.newInstance();
			UserConfig config = new UserConfig(nodeProxy);

			if (!config.isCancelled()) {
				nodeContainer.addNeoNode(nodeProxy);

			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (nodeProxy == null) {
			throw new ActionException("Could not create node", false);
		} else {
			nodeAdded = nodeProxy;
		}
	}

	@Override
	protected void undo() throws ActionException {
		nodeAdded.destroy();

	}
}

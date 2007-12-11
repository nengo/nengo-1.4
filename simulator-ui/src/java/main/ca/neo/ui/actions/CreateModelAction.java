package ca.neo.ui.actions;

import javax.swing.SwingUtilities;

import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.managers.UserTemplateConfigurer;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.UINeoNode;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;

/**
 * Creates a new NEO model
 * 
 * @author Shu
 */
public class CreateModelAction extends ReversableAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @param nodeUIType
	 *            Class type of the model to be instantiated
	 * @return Type name of the given model
	 */
	@SuppressWarnings("unchecked")
	private static String getModelName(Class nodeUIType) {
		UINeoNode nodeProxy;
		try {
			nodeProxy = (UINeoNode) nodeUIType.newInstance();
			return nodeProxy.getTypeName();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return "unable to retrieve name";
	}

	/**
	 * Container to which the created node shall be added
	 */
	private INodeContainer container;

	/**
	 * The created node
	 */
	private UINeoNode nodeCreated;

	/**
	 * Type of node to be created
	 */
	@SuppressWarnings("unchecked")
	private Class nodeType;

	/**
	 * @param nodeContainer
	 *            The container to which the created node should be added to
	 * @param nodeUIType
	 *            Type of Node to be create, such as PNetwork
	 */
	@SuppressWarnings("unchecked")
	public CreateModelAction(INodeContainer nodeContainer, Class nodeUIType) {
		this(getModelName(nodeUIType), nodeContainer, nodeUIType);
	}

	/**
	 * @param nodeContainer
	 *            The container to which the created node should be added to
	 * @param nodeUIType
	 *            Type of Node to be create, such as PNetwork
	 */
	@SuppressWarnings("unchecked")
	public CreateModelAction(String actionName, INodeContainer nodeContainer,
			Class nodeUIType) {
		super("Create new " + nodeUIType.getSimpleName(), actionName);
		this.container = nodeContainer;
		this.nodeType = nodeUIType;
		setBlocking(false);
	}

	@Override
	protected void action() throws ActionException {
		try {
			UINeoNode nodeProxy = (UINeoNode) nodeType.newInstance();
			UserTemplateConfigurer config = new UserTemplateConfigurer(
					nodeProxy);
			try {
				config.configureAndWait();
				nodeCreated = nodeProxy;
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						container.addNeoNode(nodeCreated);

					}
				});
			} catch (ConfigException e) {

				e.defaultHandleBehavior();
				nodeProxy.destroy();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void undo() throws ActionException {

		if (nodeCreated != null) {
			nodeCreated.destroy();
		}

	}
}

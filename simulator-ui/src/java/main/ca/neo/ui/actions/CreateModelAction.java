package ca.neo.ui.actions;

import javax.swing.SwingUtilities;

import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.managers.UserTemplateConfigurer;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.PNeoNode;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;

/**
 * Creates a new NEO model
 * 
 * @author Shu
 * 
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
		PNeoNode nodeProxy;
		try {
			nodeProxy = (PNeoNode) nodeUIType.newInstance();
			return nodeProxy.getTypeName();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return "unable to retrieve name";
	}

	/**
	 * Type of node to be created
	 */
	@SuppressWarnings("unchecked")
	private Class nodeType;

	/**
	 * The created node
	 */
	private PNeoNode nodeCreated;

	/**
	 * Container to which the created node shall be added
	 */
	private INodeContainer container;

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
		this(getModelName(nodeUIType), nodeContainer, nodeUIType);
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
		this.container = nodeContainer;
		this.nodeType = nodeUIType;
	}

	@Override
	protected void action() throws ActionException {

		(new Thread() {
			@Override
			public void run() {

				try {
					PNeoNode nodeProxy = (PNeoNode) nodeType.newInstance();
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

						e.defaultHandledBehavior();
						nodeProxy.destroy();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	@Override
	protected void undo() throws ActionException {
		if (nodeCreated != null) {
			nodeCreated.destroy();
		}

	}
}

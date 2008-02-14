package ca.neo.ui.actions;

import javax.swing.SwingUtilities;

import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.managers.UserTemplateConfigurer;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.UINeoNode;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.exceptions.UIException;

/**
 * Creates a new NEO model
 * 
 * @author Shu
 */
public class CreateModelAction extends ReversableAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Gets the model type name and throws an exception if the model type is not
	 * supported
	 * 
	 * @param nodeUIType
	 *            Class type of the model to be instantiated
	 * @return Type name of the given model
	 */
	@SuppressWarnings("unchecked")
	private static String getModelName(Class nodeUIType) throws UIException {
		UINeoNode nodeProxy;
		try {
			nodeProxy = (UINeoNode) nodeUIType.newInstance();
			return nodeProxy.getTypeName();
		} catch (InstantiationException e) {
			throw new UIException(
					"Can't get model type name because default constructor is missing");
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
	private Class<?> nodeType;

	/**
	 * @param nodeContainer
	 *            The container to which the created node should be added to
	 * @param nodeUIType
	 *            Type of Node to be create, such as PNetwork
	 */
	public CreateModelAction(INodeContainer nodeContainer, Class<?> nodeUIType) throws UIException {
		this(getModelName(nodeUIType), nodeContainer, nodeUIType);
	}

	/**
	 * @param nodeContainer
	 *            The container to which the created node should be added to
	 * @param nodeUIType
	 *            Type of Node to be create, such as PNetwork
	 */
	@SuppressWarnings("unchecked")
	public CreateModelAction(String modelTypeName, INodeContainer nodeContainer, Class nodeUIType) {
		super("Create new " + modelTypeName, modelTypeName, false);
		this.container = nodeContainer;
		this.nodeType = nodeUIType;
	}

	@Override
	protected void action() throws ActionException {
		try {
			UINeoNode nodeProxy = (UINeoNode) nodeType.newInstance();
			UserTemplateConfigurer config = new UserTemplateConfigurer(nodeProxy);
			try {
				config.configureAndWait();
				nodeCreated = nodeProxy;
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						container.addNodeModel(nodeCreated.getModel());

					}
				});
			} catch (ConfigException e) {

				e.defaultHandleBehavior();
				nodeProxy.destroy();
			}

		} catch (Exception e) {
			throw new ActionException(e.getMessage());
		}
	}

	@Override
	protected void undo() throws ActionException {

		if (nodeCreated != null) {
			nodeCreated.destroy();
		}

	}
}

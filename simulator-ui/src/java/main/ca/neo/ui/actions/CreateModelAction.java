package ca.neo.ui.actions;

import javax.swing.SwingUtilities;

import ca.neo.model.Node;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.INodeContainer.ContainerException;
import ca.neo.ui.models.constructors.AbstractConstructable;
import ca.neo.ui.models.constructors.ConstructableNode;
import ca.neo.ui.models.constructors.ModelFactory;
import ca.neo.ui.models.nodes.NodeContainer;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.util.UserMessages;

/**
 * Creates a new NEO model
 * 
 * @author Shu
 */
public class CreateModelAction extends ReversableAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Container to which the created node shall be added
	 */
	private INodeContainer container;

	/**
	 * The created node
	 */
	private UINeoNode nodeCreated;

	/**
	 * Node constructable
	 */
	private AbstractConstructable constructable;

	/**
	 * @param nodeContainer
	 *            The container to which the created node should be added to
	 * @param nodeUIType
	 *            Type of Node to be create, such as PNetwork
	 */
	public CreateModelAction(INodeContainer nodeContainer, ConstructableNode constructable) {
		this(constructable.getTypeName(), nodeContainer, constructable);
	}

	/**
	 * @param nodeContainer
	 *            The container to which the created node should be added to
	 * @param nodeUIType
	 *            Type of Node to be create, such as PNetwork
	 */
	@SuppressWarnings("unchecked")
	public CreateModelAction(String modelTypeName, INodeContainer nodeContainer,
			AbstractConstructable constructable) {
		super("Create new " + modelTypeName, modelTypeName, false);
		this.container = nodeContainer;
		this.constructable = constructable;
	}

	@Override
	protected void action() throws ActionException {
		try {

			Object model = ModelFactory.constructModel(constructable);

			if (model instanceof Node) {
				nodeCreated = UINeoNode.createNodeUI((Node) model);
			} else {
				throw new ActionException("Unsupported model type");
			}

			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					try {
						UINeoNode nodeUI = container.addNodeModel(nodeCreated.getModel());
						if (nodeUI instanceof NodeContainer) {
							((NodeContainer) (nodeUI)).openViewer();
						}
					} catch (ContainerException e) {
						UserMessages.showWarning("Could not add node" + e.getMessage());
					}

				}
			});

		} catch (ConfigException e) {
			e.defaultHandleBehavior();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ActionException(e.getMessage(), e);
		}
	}

	@Override
	protected void undo() throws ActionException {

		if (nodeCreated != null) {
			nodeCreated.destroy();
		}

	}
}

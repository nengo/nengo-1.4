package ca.nengo.ui.actions;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ca.nengo.model.Node;
import ca.nengo.model.StructuralException;
import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.models.NodeContainer;
import ca.nengo.ui.models.UINeoNode;
import ca.nengo.ui.models.NodeContainer.ContainerException;
import ca.nengo.ui.models.constructors.AbstractConstructable;
import ca.nengo.ui.models.constructors.ConstructableNode;
import ca.nengo.ui.models.constructors.ModelFactory;
import ca.nengo.ui.models.nodes.UINodeViewable;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.UserMessages;

/**
 * Creates a new NEO model
 * 
 * @author Shu
 */
public class CreateModelAction extends ReversableAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Prompts the user to select a non-conflicting name
	 * 
	 * @param node
	 * @param container
	 * @throws UserCancelledException
	 *             If the user cancels
	 */
	public static void ensureNonConflictingName(Node node, NodeContainer container)
			throws UserCancelledException {
		String originalName = node.getName();
		String newName = node.getName();
		int i = 0;

		while (container.getNodeModel(newName) != null) {
			// Avoid duplicate names
			while (container.getNodeModel(newName) != null) {
				i++;
				newName = originalName + " (" + i + ")";

			}
			newName = JOptionPane.showInputDialog(UIEnvironment.getInstance(),
					"Node already exists, please enter a new name", newName);

			if (newName == null || newName.equals("")) {
				throw new UserCancelledException();
			}
		}

		try {
			node.setName(newName);
		} catch (StructuralException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Node constructable
	 */
	private AbstractConstructable constructable;

	/**
	 * Container to which the created node shall be added
	 */
	private NodeContainer container;

	/**
	 * @param nodeContainer
	 *            The container to which the created node should be added to
	 * @param nodeUIType
	 *            Type of Node to be create, such as PNetwork
	 */
	public CreateModelAction(NodeContainer nodeContainer, ConstructableNode constructable) {
		this(constructable.getTypeName(), nodeContainer, constructable);
	}

	/**
	 * @param nodeContainer
	 *            The container to which the created node should be added to
	 * @param nodeUIType
	 *            Type of Node to be create, such as PNetwork
	 */
	@SuppressWarnings("unchecked")
	public CreateModelAction(String modelTypeName, NodeContainer nodeContainer,
			AbstractConstructable constructable) {
		super("Create new " + modelTypeName, modelTypeName, false);
		this.container = nodeContainer;
		this.constructable = constructable;
	}

	private UINeoNode nodeCreated;

	@Override
	protected void action() throws ActionException {
		try {

			Object model = ModelFactory.constructModel(constructable);
			if (model instanceof Node) {
				final Node node = (Node) model;

				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						try {
							ensureNonConflictingName(node, container);
							try {
								nodeCreated = container.addNodeModel(node);
								if (nodeCreated instanceof UINodeViewable) {
									((UINodeViewable) (nodeCreated)).openViewer();
								}
							} catch (ContainerException e) {
								UserMessages.showWarning("Could not add node: " + e.getMessage());
							}
						} catch (UserCancelledException e) {
							e.defaultHandleBehavior();
						}

					}
				});

			} else {
				throw new ActionException("Can not add model of the type: "
						+ model.getClass().getSimpleName());
			}

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

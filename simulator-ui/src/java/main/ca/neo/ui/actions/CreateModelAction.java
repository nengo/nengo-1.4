package ca.neo.ui.actions;

import javax.swing.SwingUtilities;

import ca.neo.ui.configurable.managers.UserTemplateConfig;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.PNeoNode;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;

public class CreateModelAction extends ReversableAction {

	private static final long serialVersionUID = 1L;

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
	@SuppressWarnings("unchecked")
	Class nc;

	PNeoNode nodeAdded;

	INodeContainer nodeContainer;

	PNeoNode nodeProxy;

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
		this.nodeContainer = nodeContainer;
		this.nc = nodeUIType;
	}

	@Override
	protected void action() throws ActionException {

		(new Thread() {
			public void run() {
				nodeProxy = null;
				try {

					nodeProxy = (PNeoNode) nc.newInstance();
					UserTemplateConfig config = new UserTemplateConfig(nodeProxy);
					config.configureAndWait();

					if (nodeProxy.isConfigured()) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								nodeContainer.addNeoNode(nodeProxy);
							}
						});

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

				if (nodeProxy != null) {

					nodeAdded = nodeProxy;
				}
			}
		}).start();

	}

	@Override
	protected void undo() throws ActionException {
		if (nodeAdded != null) {
			nodeAdded.destroy();
		}

	}
}

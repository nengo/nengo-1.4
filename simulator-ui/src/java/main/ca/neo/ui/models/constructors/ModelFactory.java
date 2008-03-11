package ca.neo.ui.models.constructors;

import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.managers.UserTemplateConfigurer;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.UINeoNode;

public class ModelFactory {

	public static ConstructableNode[] getNodeConstructables(INodeContainer container) {
		return new ConstructableNode[] { new CNetwork(container), new CNEFEnsemble(container),
				new CFunctionInput(container) };
	}

	public static Object constructModel(AbstractConstructable configurable) throws ConfigException {
		return constructModel(null, configurable);
	}

	/**
	 * @param nodeParent
	 *            Used for animation purposes only
	 * @param configurable
	 * @return
	 * @throws ConfigException
	 */
	public static Object constructModel(UINeoNode nodeParent, AbstractConstructable configurable)
			throws ConfigException {

		if (nodeParent != null) {
			nodeParent.setModelBusy(true);
		}
		try {
			UserTemplateConfigurer config = new UserTemplateConfigurer(configurable);
			config.configureAndWait();
		} finally {
			if (nodeParent != null) {
				nodeParent.setModelBusy(false);
			}
		}

		return configurable.getModel();
	}
}

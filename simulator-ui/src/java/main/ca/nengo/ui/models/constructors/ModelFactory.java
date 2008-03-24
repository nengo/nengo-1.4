package ca.nengo.ui.models.constructors;

import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.managers.UserTemplateConfigurer;
import ca.nengo.ui.models.NodeContainer;
import ca.nengo.ui.models.UINeoNode;

public class ModelFactory {

	public static ConstructableNode[] getNodeConstructables(NodeContainer container) {
		return new ConstructableNode[] { new CNetwork(), new CNEFEnsemble(), new CFunctionInput() };
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

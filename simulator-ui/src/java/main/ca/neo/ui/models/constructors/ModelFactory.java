package ca.neo.ui.models.constructors;

import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.managers.UserTemplateConfigurer;
import ca.neo.ui.models.UINeoNode;

public class ModelFactory {

	public static Constructable[] getNodeConstructables() {
		return new Constructable[] { new CNetwork(), new CNEFEnsemble(), new CFunctionInput() };
	}

	public static Object constructNode(Constructable configurable) throws ConfigException {
		return constructNode(null, configurable);
	}

	/**
	 * @param nodeParent
	 *            Used for animation purposes only
	 * @param configurable
	 * @return
	 * @throws ConfigException
	 */
	public static Object constructNode(UINeoNode nodeParent, Constructable configurable)
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

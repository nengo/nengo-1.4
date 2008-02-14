package ca.neo.ui.models.constructors;

import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.managers.UserTemplateConfigurer;

public class ModelFactory {

	public static Constructable[] getNodeConstructables() {
		return new Constructable[] { new CNetwork(), new CNEFEnsemble(), new CFunctionInput() };
	}

	public static Object constructNode(Constructable configurable) throws ConfigException {
		UserTemplateConfigurer config = new UserTemplateConfigurer(configurable);

		config.configureAndWait();

		return configurable.getModel();
	}
}

package ca.neo.ui.models;

import java.util.Collection;

import ca.neo.ui.actions.AddProbesAction;
import ca.neo.ui.actions.RemoveModelsAction;
import ca.shu.ui.lib.objects.models.ModelObject;
import ca.shu.ui.lib.util.menus.AbstractMenuBuilder;

/**
 * Contains static members which reveal what sort of Nodes can be created by the
 * UI
 * 
 * @author Shu
 */
public class UIModels {

	public static void constructMenuForModels(AbstractMenuBuilder menuBuilder,
			Class<? extends ModelObject> modelType, String typeName,
			Collection<ModelObject> homogeneousModels) {

		menuBuilder.addAction(new RemoveModelsAction(homogeneousModels,
				typeName, true));

		if (UINeoNode.class.isAssignableFrom(modelType)) {

			menuBuilder.addAction(new AddProbesAction(homogeneousModels));
		}

	}

}

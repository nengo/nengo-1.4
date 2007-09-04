package ca.neo.ui.models;

import java.util.Collection;

import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.impl.FunctionInput;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.neuron.Neuron;
import ca.neo.ui.actions.AddProbesAction;
import ca.neo.ui.actions.RemoveModelsAction;
import ca.neo.ui.models.nodes.UIFunctionInput;
import ca.neo.ui.models.nodes.UINEFEnsemble;
import ca.neo.ui.models.nodes.UINetwork;
import ca.neo.ui.models.nodes.UINeuron;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.menus.AbstractMenuBuilder;

/**
 * Contains static members which reveal what sort of Nodes can be created by the
 * UI
 * 
 * @author Shu
 */
public class UIModels {

	@SuppressWarnings("unchecked")
	public static final Class[] FUNCTION_TYPES = { UIFunctionInput.class };

	@SuppressWarnings("unchecked")
	public static final Class[] NODE_CONTAINER_TYPES = { UINetwork.class,
			UINEFEnsemble.class, };

	@SuppressWarnings("unchecked")
	public static final Class[] NODE_TYPES = { UINeuron.class };

	/**
	 * Creates a UI Wrapper around a given NEO Model. Note: not all models are
	 * supported.
	 * 
	 * @param node
	 *            NEO model
	 * @return UI Wrapper around the NEO model node
	 */
	public static UINeoNode createUIFromModel(Node node) {
		if (node instanceof NEFEnsemble) {
			return new UINEFEnsemble((NEFEnsemble) node);
		} else if (node instanceof FunctionInput) {

			return new UIFunctionInput((FunctionInput) node);
		} else if (node instanceof Network) {
			return new UINetwork((Network) node);

		} else if (node instanceof Neuron) {
			return new UINeuron(node);
		} else {
			UserMessages.showError("Unsupported node type");
			return null;
		}

	}

	public static void constructMenuForModels(AbstractMenuBuilder menuBuilder,
			Class<? extends UIModel> modelType, String typeName,
			Collection<UIModel> homogeneousModels) {

		menuBuilder.addAction(new RemoveModelsAction(homogeneousModels,
				typeName));

		if (UINeoNode.class.isAssignableFrom(modelType)) {

			menuBuilder.addAction(new AddProbesAction(homogeneousModels));
		}

	}

}

package ca.neo.ui.models;

import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.impl.FunctionInput;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.neuron.Neuron;
import ca.neo.ui.models.nodes.PFunctionInput;
import ca.neo.ui.models.nodes.PNEFEnsemble;
import ca.neo.ui.models.nodes.PNetwork;
import ca.neo.ui.models.nodes.PNeuron;
import ca.shu.ui.lib.util.Util;

/**
 * Contains static members which reveal what sort of Nodes can be created by the
 * UI
 * 
 * @author Shu
 * 
 */
public class PModelClasses {

	@SuppressWarnings("unchecked")
	public static final Class[] FUNCTION_TYPES = { PFunctionInput.class };

	@SuppressWarnings("unchecked")
	public static final Class[] NODE_CONTAINER_TYPES = { PNetwork.class,
			PNEFEnsemble.class, };

	@SuppressWarnings("unchecked")
	public static final Class[] NODE_TYPES = { PNeuron.class };

	public static PNeoNode createUIFromModel(Node node) {
		if (node instanceof NEFEnsemble) {
			return new PNEFEnsemble((NEFEnsemble) node);
		} else if (node instanceof FunctionInput) {

			return new PFunctionInput((FunctionInput) node);
		} else if (node instanceof Network) {
			return new PNetwork((Network) node);

		} else if (node instanceof Neuron) {
			return new PNeuron(node);
		} else {
			Util.UserError("Unsupported node type");
			return null;
		}

	}

}

package ca.neo.ui.models.wrappers;

import ca.neo.model.Termination;
import ca.neo.ui.models.PModelConfigurable;
import ca.neo.ui.models.PModelNode;

/**
 * TODO: pull up methods from PDecodedTermination
 */
public abstract class PTermination extends PModelConfigurable {

	PModelNode nodeParent;

	public PTermination(PModelNode nodeParent) {
		super();
		this.nodeParent = nodeParent;
	}

	@Override
	public void removeModel() {
		// TODO Auto-generated method stub
		super.removeModel();
		nodeParent.removeWidget(this);
	}

	public Termination getModelTermination() {
		return (Termination) getModel();
	}

}

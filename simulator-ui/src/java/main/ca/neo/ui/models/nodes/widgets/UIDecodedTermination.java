package ca.neo.ui.models.nodes.widgets;

import ca.neo.model.Termination;
import ca.neo.ui.models.nodes.UINEFEnsemble;

/**
 * UI Wrapper for a Decoded Termination
 * 
 * @author Shu Wu
 */
public class UIDecodedTermination extends UITermination {

	private static final long serialVersionUID = 1L;

	private static final String typeName = "Decoded Termination";

	public UIDecodedTermination(UINEFEnsemble ensembleProxy, Termination term) {
		super(ensembleProxy, term);
		setName(term.getName());
	}

	@Override
	protected void prepareToDestroyModel() {
		getNodeParent().getModel().removeDecodedTermination(getModel().getName());
		showPopupMessage("decoded termination removed from ensemble");
		super.prepareToDestroyModel();
	}

	@Override
	public UINEFEnsemble getNodeParent() {
		return (UINEFEnsemble) super.getNodeParent();
	}

	@Override
	public String getTypeName() {
		return typeName;
	}

}

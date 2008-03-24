package ca.nengo.ui.models.nodes.widgets;

import ca.nengo.model.nef.NEFEnsemble;
import ca.nengo.model.nef.impl.DecodedTermination;
import ca.nengo.ui.models.UINeoNode;
import ca.shu.ui.lib.util.Util;

/**
 * UI Wrapper for a Decoded Termination
 * 
 * @author Shu Wu
 */
public class UIDecodedTermination extends UITermination {

	private static final long serialVersionUID = 1L;

	public static final String typeName = "Decoded Termination";

	protected UIDecodedTermination(UINeoNode ensembleProxy, DecodedTermination term) {
		super(ensembleProxy, term);
		setName(term.getName());
	}

	@Override
	public String getTypeName() {
		return typeName;
	}

	@Override
	protected void destroyTerminationModel() {
		if (getModel().getNode() instanceof NEFEnsemble) {
			((NEFEnsemble) getModel().getNode()).removeDecodedTermination(getModel().getName());
			showPopupMessage("decoded termination removed from ensemble");
		} else {
			Util.Assert(false, "Decoded Termination not attached to NEFEnsemble");
		}
	}

}

package ca.neo.ui.models.nodes.widgets;

import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.impl.DecodedOrigin;
import ca.neo.ui.models.UINeoNode;
import ca.shu.ui.lib.util.Util;

/**
 * UI Wrapper for a Decoded Termination
 * 
 * @author Shu Wu
 */
public class UIDecodedOrigin extends UIOrigin {

	private static final long serialVersionUID = 1L;

	public static final String typeName = "Decoded Origin";

	protected UIDecodedOrigin(UINeoNode ensembleProxy, DecodedOrigin origin) {
		super(ensembleProxy, origin);
		setName(origin.getName());
	}

	protected int getInputDimensions() {
		return getModel().getDimensions();
	}

	@Override
	public String getTypeName() {
		return typeName;
	}

	@Override
	protected void destroyOriginModel() {
		if (getModel().getNode() instanceof NEFEnsemble) {
			((NEFEnsemble) (getModel().getNode())).removeDecodedTermination(getModel().getName());
			showPopupMessage("decoded termination removed from ensemble");
		} else {
			Util.Assert(false, "Decoded Origin not attached to NEFEnsemble");
		}
	}
}

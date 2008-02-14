package ca.neo.ui.models.nodes.widgets;

import ca.neo.model.Origin;
import ca.neo.ui.models.nodes.UINEFEnsemble;

/**
 * UI Wrapper for a Decoded Termination
 * 
 * @author Shu Wu
 */
public class UIDecodedOrigin extends UIOrigin {

	private static final long serialVersionUID = 1L;

	private static final String typeName = "Decoded Origin";
	private int inputDimensions;

	public UIDecodedOrigin(UINEFEnsemble ensembleProxy, Origin origin) {
		super(ensembleProxy, origin);
		setName(origin.getName());
		this.inputDimensions = ensembleProxy.getModel().getDimension();
	}

	protected int getInputDimensions() {
		return inputDimensions;
	}

	@Override
	protected void prepareForDestroy() {
		if (isModelExists()) {
			getNodeParent().getModel().removeDecodedTermination(getModel().getName());
			showPopupMessage("decoded termination removed from ensemble");
		}

		super.prepareForDestroy();
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

package ca.neo.ui.models.nodes.widgets;

import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.ui.configurable.ConfigParam;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.descriptors.CBoolean;
import ca.neo.ui.configurable.descriptors.CFloat;
import ca.neo.ui.configurable.descriptors.CString;
import ca.neo.ui.configurable.descriptors.CTerminationWeights;
import ca.neo.ui.models.nodes.UINEFEnsemble;

/**
 * UI Wrapper for a Decoded Termination
 * 
 * @author Shu Wu
 */
public class UIDecodedTermination extends UITermination {

	private static final ConfigParamDescriptor pIsModulatory = new CBoolean(
			"Is Modulatory");

	private static final ConfigParamDescriptor pName = new CString("Name");

	private static final ConfigParamDescriptor pTauPSC = new CFloat("tauPSC");

	private static final long serialVersionUID = 1L;

	private static final String typeName = "Decoded Termination";

	private ConfigParamDescriptor pTransformMatrix;

	public UIDecodedTermination(UINEFEnsemble ensembleProxy) {
		super(ensembleProxy);

	}

	@Override
	protected Object configureModel(ConfigParam configuredProperties) {
		Termination term = null;

		try {
			term = getNodeParent().getModel().addDecodedTermination(
					(String) configuredProperties.getProperty(pName),
					(float[][]) configuredProperties
							.getProperty(pTransformMatrix),
					(Float) configuredProperties.getProperty(pTauPSC),
					(Boolean) configuredProperties.getProperty(pIsModulatory));

			getNodeParent().popupTransientMsg(
					"New decoded termination added to ensemble");

			setName(term.getName());
		} catch (StructuralException e) {
			e.printStackTrace();
		}

		return term;
	}

	@Override
	protected void prepareForDestroy() {
		getNodeParent().getModel().removeDecodedTermination(
				getModel().getName());
		popupTransientMsg("decoded termination removed from ensemble");

		super.prepareForDestroy();
	}

	@Override
	public ConfigParamDescriptor[] getConfigSchema() {
		pTransformMatrix = new CTerminationWeights("Weights", getNodeParent()
				.getModel().getDimension());

		ConfigParamDescriptor[] zProperties = { pName, pTransformMatrix,
				pTauPSC, pIsModulatory };
		return zProperties;

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

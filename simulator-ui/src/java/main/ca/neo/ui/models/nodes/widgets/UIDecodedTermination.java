package ca.neo.ui.models.nodes.widgets;

import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.nef.impl.DecodedTermination;
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
 * 
 */
public class UIDecodedTermination extends UITermination {

	private static final ConfigParamDescriptor pIsModulatory = new CBoolean(
			"Is Modulatory");

	private static final ConfigParamDescriptor pName = new CString("Name");

	private static final ConfigParamDescriptor pTauPSC = new CFloat("tauPSC");

	private static final long serialVersionUID = 1L;

	private static final String typeName = "Decoded Termination";

	private UINEFEnsemble ensembleProxy;

	private ConfigParamDescriptor pTransformMatrix;

	public UIDecodedTermination(UINEFEnsemble ensembleProxy) {
		super(ensembleProxy);

		init(ensembleProxy);
	}

	public UIDecodedTermination(UINEFEnsemble ensembleProxy,
			DecodedTermination term) {
		super(ensembleProxy, term);

		init(ensembleProxy);
	}

	private void init(UINEFEnsemble ensembleProxy) {
		this.ensembleProxy = ensembleProxy;

	}

	@Override
	protected Object configureModel(ConfigParam configuredProperties) {
		Termination term = null;

		try {
			term = ensembleProxy.getModel().addDecodedTermination(
					(String) configuredProperties.getProperty(pName),
					(float[][]) configuredProperties
							.getProperty(pTransformMatrix),
					(Float) configuredProperties.getProperty(pTauPSC),
					(Boolean) configuredProperties.getProperty(pIsModulatory));

			ensembleProxy
					.popupTransientMsg("New decoded termination added to ensemble");

			setName(term.getName());
		} catch (StructuralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return term;
	}

	@Override
	protected void prepareForDestroy() {
		ensembleProxy.getModel().removeDecodedTermination(getModel().getName());
		popupTransientMsg("decoded termination removed from ensemble");

		super.prepareForDestroy();
	}

	@Override
	public ConfigParamDescriptor[] getConfigSchema() {
		pTransformMatrix = new CTerminationWeights("Weights", ensembleProxy
				.getModel().getDimension());

		ConfigParamDescriptor[] zProperties = { pName, pTransformMatrix,
				pTauPSC, pIsModulatory };
		return zProperties;

	}

	@Override
	public String getTypeName() {
		return typeName;
	}

}

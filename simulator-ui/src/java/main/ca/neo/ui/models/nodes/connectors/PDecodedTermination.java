package ca.neo.ui.models.nodes.connectors;

import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.nef.impl.DecodedTermination;
import ca.neo.ui.configurable.ConfigParam;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.struct.PTBoolean;
import ca.neo.ui.configurable.struct.PTFloat;
import ca.neo.ui.configurable.struct.PTString;
import ca.neo.ui.configurable.struct.PTTerminationWeights;
import ca.neo.ui.models.nodes.PNEFEnsemble;

public class PDecodedTermination extends PTermination {

	private static final long serialVersionUID = 1L;

	static final ConfigParamDescriptor pIsModulatory = new PTBoolean("Is Modulatory");

	static final ConfigParamDescriptor pName = new PTString("Name");

	static final ConfigParamDescriptor pTauPSC = new PTFloat("tauPSC");

	static final String typeName = "Decoded Termination";

	PNEFEnsemble ensembleProxy;

	ConfigParamDescriptor pTransformMatrix;

	public PDecodedTermination(PNEFEnsemble ensembleProxy) {
		super(ensembleProxy);

		init(ensembleProxy);
	}

	public PDecodedTermination(PNEFEnsemble ensembleProxy,
			DecodedTermination term) {
		super(ensembleProxy, term);

		init(ensembleProxy);
	}

	@Override
	public ConfigParamDescriptor[] getConfigSchema() {
		pTransformMatrix = new PTTerminationWeights("Weights", ensembleProxy
				.getModel().getDimension());

		ConfigParamDescriptor[] zProperties = { pName, pTransformMatrix, pTauPSC,
				pIsModulatory };
		return zProperties;

	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return typeName;
	}

	private void init(PNEFEnsemble ensembleProxy) {
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

}

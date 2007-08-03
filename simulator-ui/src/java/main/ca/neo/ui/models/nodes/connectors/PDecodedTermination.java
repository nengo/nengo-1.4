package ca.neo.ui.models.nodes.connectors;

import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.nef.impl.DecodedTermination;
import ca.neo.ui.models.PModel;
import ca.neo.ui.models.icons.IconWrapper;
import ca.neo.ui.models.nodes.PNEFEnsemble;
import ca.neo.ui.views.objects.configurable.managers.PropertySet;
import ca.neo.ui.views.objects.configurable.struct.PTBoolean;
import ca.neo.ui.views.objects.configurable.struct.PTFloat;
import ca.neo.ui.views.objects.configurable.struct.PTString;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;
import ca.shu.ui.lib.objects.lines.LineIn;

public class PDecodedTermination extends PTermination {

	private static final long serialVersionUID = 1L;

	static final PropDescriptor pIsModulatory = new PTBoolean("Is Modulatory");

	static final PropDescriptor pName = new PTString("Name");

	static final PropDescriptor pTauPSC = new PTFloat("tau");

	static final PropDescriptor[] zProperties = { pName, pTauPSC, pIsModulatory };

	PNEFEnsemble ensembleProxy;

	// public PDecodedTermination(PNEFEnsemble ensembleProxy, String name,
	// float tauPSC, boolean isModulatory) {
	// super(ensembleProxy);
	// setProperty(pName, name);
	// setProperty(pTauPSC, tauPSC);
	// setProperty(pIsModulatory, isModulatory);
	//
	//		
	//		
	// init(ensembleProxy);
	//		
	//
	// }

	public PDecodedTermination(PNEFEnsemble ensembleProxy,
			DecodedTermination term) {
		super(ensembleProxy, term);

		init(ensembleProxy);
	}

	public PDecodedTermination(PNEFEnsemble ensembleProxy) {
		super(ensembleProxy);

		init(ensembleProxy);
	}

	private void init(PNEFEnsemble ensembleProxy) {
		this.ensembleProxy = ensembleProxy;
		
	}

	@Override
	public PropDescriptor[] getConfigSchema() {
		// TODO Auto-generated method stub
		return zProperties;

	}

	@Override
	protected Object configureModel(PropertySet configuredProperties) {
		Termination term = null;

		try {
			term = ensembleProxy.getModel().addDecodedTermination(
					(String) configuredProperties.getProperty(pName),
					new float[][] { new float[] { (Float) configuredProperties
							.getProperty(pTauPSC) } },
					(Float) configuredProperties.getProperty(pTauPSC),
					(Boolean) configuredProperties.getProperty(pIsModulatory));

			setName(term.getName());
		} catch (StructuralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return term;
	}

	static final String typeName = "Decoded Termination";

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return typeName;
	}

}


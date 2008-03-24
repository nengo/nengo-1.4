package ca.nengo.ui.models.constructors;

import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;
import ca.nengo.model.nef.NEFEnsemble;
import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.ConfigResult;
import ca.nengo.ui.configurable.ConfigSchema;
import ca.nengo.ui.configurable.ConfigSchemaImpl;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.descriptors.PBoolean;
import ca.nengo.ui.configurable.descriptors.PFloat;
import ca.nengo.ui.configurable.descriptors.PString;
import ca.nengo.ui.configurable.descriptors.PTerminationWeights;
import ca.nengo.ui.models.nodes.widgets.UIDecodedTermination;

public class CDecodedTermination extends AbstractConstructable {
	private static final Property pIsModulatory = new PBoolean("Is Modulatory");

	private static final Property pName = new PString("Name");

	private static final Property pTauPSC = new PFloat("tauPSC");
	private NEFEnsemble nefEnsembleParent;

	private Property pTransformMatrix;

	public CDecodedTermination(NEFEnsemble nefEnsembleParent) {
		super();
		this.nefEnsembleParent = nefEnsembleParent;
	}

	@Override
	protected Object configureModel(ConfigResult configuredProperties) throws ConfigException {

		// make sure the name isn't a duplicate
		String name = (String) configuredProperties.getValue(pName);
		Termination oldTerm;
		try {
			oldTerm = nefEnsembleParent.getTermination(name);
		} catch (StructuralException e) {
			oldTerm = null;
		}
		if (oldTerm != null) {
			throw new ConfigException("A termination with the name '" + name + "' already exists");
		}

		Termination term = null;
		try {
			term = nefEnsembleParent.addDecodedTermination((String) configuredProperties
					.getValue(pName), (float[][]) configuredProperties
					.getValue(pTransformMatrix), (Float) configuredProperties
					.getValue(pTauPSC), (Boolean) configuredProperties
					.getValue(pIsModulatory));

		} catch (StructuralException e) {
			e.printStackTrace();
		}

		return term;
	}

	@Override
	public ConfigSchema getSchema() {
		pTransformMatrix = new PTerminationWeights("Weights", nefEnsembleParent.getDimension());

		Property[] zProperties = { pName, pTransformMatrix, pTauPSC, pIsModulatory };
		return new ConfigSchemaImpl(zProperties);

	}

	public String getTypeName() {
		return UIDecodedTermination.typeName;
	}

}

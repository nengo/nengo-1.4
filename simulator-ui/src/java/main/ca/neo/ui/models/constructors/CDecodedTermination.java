package ca.neo.ui.models.constructors;

import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.ConfigSchemaImpl;
import ca.neo.ui.configurable.ConfigSchema;
import ca.neo.ui.configurable.Property;
import ca.neo.ui.configurable.ConfigResult;
import ca.neo.ui.configurable.descriptors.PBoolean;
import ca.neo.ui.configurable.descriptors.PFloat;
import ca.neo.ui.configurable.descriptors.PString;
import ca.neo.ui.configurable.descriptors.PTerminationWeights;
import ca.neo.ui.models.nodes.widgets.UIDecodedTermination;

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

package ca.neo.ui.models.constructors;

import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.descriptors.PBoolean;
import ca.neo.ui.configurable.descriptors.PFloat;
import ca.neo.ui.configurable.descriptors.PString;
import ca.neo.ui.configurable.descriptors.PTerminationWeights;
import ca.neo.ui.models.nodes.widgets.UIDecodedTermination;

public class CDecodedTermination extends AbstractConstructable {
	private static final PropertyDescriptor pIsModulatory = new PBoolean("Is Modulatory");

	private static final PropertyDescriptor pName = new PString("Name");

	private static final PropertyDescriptor pTauPSC = new PFloat("tauPSC");
	private NEFEnsemble nefEnsembleParent;

	private PropertyDescriptor pTransformMatrix;

	public CDecodedTermination(NEFEnsemble nefEnsembleParent) {
		super();
		this.nefEnsembleParent = nefEnsembleParent;
	}

	@Override
	protected Object configureModel(PropertySet configuredProperties) {
		Termination term = null;

		try {
			term = nefEnsembleParent.addDecodedTermination((String) configuredProperties
					.getProperty(pName), (float[][]) configuredProperties
					.getProperty(pTransformMatrix), (Float) configuredProperties
					.getProperty(pTauPSC), (Boolean) configuredProperties
					.getProperty(pIsModulatory));

		} catch (StructuralException e) {
			e.printStackTrace();
		}

		return term;
	}

	@Override
	public PropertyDescriptor[] getConfigSchema() {
		pTransformMatrix = new PTerminationWeights("Weights", nefEnsembleParent.getDimension());

		PropertyDescriptor[] zProperties = { pName, pTransformMatrix, pTauPSC, pIsModulatory };
		return zProperties;

	}

	public String getTypeName() {
		return UIDecodedTermination.typeName;
	}

}

package ca.neo.ui.models.nodes.widgets;

import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.descriptors.PBoolean;
import ca.neo.ui.configurable.descriptors.PFloat;
import ca.neo.ui.configurable.descriptors.PString;
import ca.neo.ui.configurable.descriptors.PTerminationWeights;
import ca.neo.ui.models.nodes.UINEFEnsemble;

/**
 * UI Wrapper for a Decoded Termination
 * 
 * @author Shu Wu
 */
public class UIDecodedTermination extends UITermination {

	private static final PropertyDescriptor pIsModulatory = new PBoolean(
			"Is Modulatory");

	private static final PropertyDescriptor pName = new PString("Name");

	private static final PropertyDescriptor pTauPSC = new PFloat("tauPSC");

	private static final long serialVersionUID = 1L;

	private static final String typeName = "Decoded Termination";

	private PropertyDescriptor pTransformMatrix;

	public UIDecodedTermination(UINEFEnsemble ensembleProxy) {
		super(ensembleProxy);

	}

	@Override
	protected Object configureModel(PropertySet configuredProperties) {
		Termination term = null;

		try {
			term = getNodeParent().getModel().addDecodedTermination(
					(String) configuredProperties.getProperty(pName),
					(float[][]) configuredProperties
							.getProperty(pTransformMatrix),
					(Float) configuredProperties.getProperty(pTauPSC),
					(Boolean) configuredProperties.getProperty(pIsModulatory));

			getNodeParent().showPopupMessage(
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
		showPopupMessage("decoded termination removed from ensemble");

		super.prepareForDestroy();
	}

	@Override
	public PropertyDescriptor[] getConfigSchema() {
		pTransformMatrix = new PTerminationWeights("Weights", getNodeParent()
				.getModel().getDimension());

		PropertyDescriptor[] zProperties = { pName, pTransformMatrix,
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

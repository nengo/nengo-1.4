package ca.nengo.ui.configurable.descriptors;

import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.panels.TerminationWeightsInputPanel;

/**
 * Config Descriptor for Termination Weights Matrix
 * 
 * @author Shu Wu
 * 
 */
public class PTerminationWeights extends Property {

	private static final long serialVersionUID = 1L;
	/**
	 * Dimensions of the ensemble the Termination weights belong to
	 */
	private final int ensembleDimensions;

	public PTerminationWeights(String name, int dimensions) {
		super(name);
		this.ensembleDimensions = dimensions;

	}

	@Override
	protected TerminationWeightsInputPanel createInputPanel() {
		return new TerminationWeightsInputPanel(this);

	}

	/**
	 * @return Dimensions of the Ensemble the termination weights belong to
	 */
	public int getEnsembleDimensions() {
		return ensembleDimensions;
	}

	@Override
	public Class<float[][]> getTypeClass() {
		return float[][].class;
	}

	@Override
	public String getTypeName() {
		return "Coupling Matrix";
	}

}

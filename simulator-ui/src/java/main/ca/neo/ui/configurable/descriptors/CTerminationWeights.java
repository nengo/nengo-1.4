package ca.neo.ui.configurable.descriptors;

import ca.neo.ui.configurable.ConfigParamDescriptor;

/**
 * Config Descriptor for Termination Weights Matrix
 * 
 * @author Shu Wu
 * 
 */
public class CTerminationWeights extends ConfigParamDescriptor {

	private static final long serialVersionUID = 1L;
	/**
	 * Dimensions of the ensemble the Termination weights belong to
	 */
	private final int ensembleDimensions;

	public CTerminationWeights(String name, int dimensions) {
		super(name);
		this.ensembleDimensions = dimensions;

	}

	@Override
	public TerminationWeightsInputPanel createInputPanel() {
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

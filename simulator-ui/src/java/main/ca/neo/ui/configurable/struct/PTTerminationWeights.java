package ca.neo.ui.configurable.struct;

import ca.neo.ui.configurable.ConfigParamDescriptor;

public class PTTerminationWeights extends ConfigParamDescriptor {

	private static final long serialVersionUID = 1L;
	private final int ensembleDimensions;

	public PTTerminationWeights(String name, int dimensions) {
		super(name);
		this.ensembleDimensions = dimensions;

	}

	@Override
	public TerminationWeightsInputPanel createInputPanel() {
		return new TerminationWeightsInputPanel(this);

	}

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


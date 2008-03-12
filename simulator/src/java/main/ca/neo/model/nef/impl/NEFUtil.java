/*
 * Created on 12-Mar-08
 */
package ca.neo.model.nef.impl;

import ca.neo.model.RealOutput;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFNode;
import ca.neo.util.MU;

/**
 * Utility methods for related to Neural Engineering Framework. 
 * 
 * @author Bryan Tripp
 */
public class NEFUtil {

	/**
	 * Calculates an instantaneous input-output mapping for an ensemble. 
	 * 
	 * @param origin The origin from which to take the output (must belong to an NEFEnsemble)
	 * @param input Set of inputs directly into the ensemble (not through termination mapping/dynamics) 
	 * @param mode SimulationMode in which to calculate the mapping; must be DIRECT or CONSTANT_RATE
	 * @return Outputs from the given Origin for given inputs
	 */
	public static float[][] getOutput(DecodedOrigin origin, float[][] input, SimulationMode mode) {
		float[][] output = null;
		
		try {
			if ( !(origin.getNode() instanceof NEFEnsemble) ) {
				throw new RuntimeException("This calculation can only be performed with origins that belong to NEFEnsembles.");
			}
			
			NEFEnsemble ensemble = (NEFEnsemble) origin.getNode();
			float[][] encoders = ensemble.getEncoders();

			output = new float[input.length][];

			NEFNode[] nodes = (NEFNode[]) ensemble.getNodes();
			
			SimulationMode oldMode = ensemble.getMode();			
			ensemble.setMode(mode);
			for (int i = 0; i < input.length; i++) {
				if (mode.equals(SimulationMode.CONSTANT_RATE)) {
					for (int j = 0; j < nodes.length; j++) {
						float radialInput = 0;
						if (ensemble instanceof NEFEnsembleImpl) {
							NEFEnsembleImpl impl = (NEFEnsembleImpl) ensemble;
							radialInput = impl.getRadialInput(input[i], j);
						} else {
							radialInput = MU.prod(input[i], encoders[j]);
						}
						((NEFNode) nodes[j]).setRadialInput(radialInput);
						nodes[j].run(0f, 0f);					
					}
					origin.run(null, 0f, 1f);
					output[i] = ((RealOutput) origin.getValues()).getValues();					
				} else if (mode.equals(SimulationMode.DIRECT)) {
					origin.run(input[i], 0f, 1f);
					output[i] = ((RealOutput) origin.getValues()).getValues();					
				} else {
					throw new SimulationException("Instantaneous input-output mapping can only be done in DIRECT or CONSTANT_RATE simulation mode");
				}				
			}
			ensemble.setMode(oldMode);
			
		} catch (SimulationException e) {
			throw new RuntimeException("Can't plot origin error", e);
		}
		
		return output;
	}
	
}

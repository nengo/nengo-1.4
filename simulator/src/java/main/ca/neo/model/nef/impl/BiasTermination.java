/*
 * Created on 24-Apr-07
 */
package ca.neo.model.nef.impl;

import ca.neo.dynamics.Integrator;
import ca.neo.dynamics.LinearSystem;
import ca.neo.model.Node;
import ca.neo.model.StructuralException;

public class BiasTermination extends DecodedTermination {

	private static final long serialVersionUID = 1L;
	
	private float[] myBiasEncoders;
	private String myBaseName;
	
	public BiasTermination(Node node, String name, String baseName, LinearSystem dynamics, Integrator integrator, float[] biasEncoders, boolean interneurons) throws StructuralException {
		super(node, name, new float[][]{new float[]{interneurons ? -1 : 1}}, dynamics, integrator);
		myBiasEncoders = biasEncoders;
		myBaseName = baseName;
	}
	
	public String getBaseTerminationName() {
		return myBaseName;
	}
	
	public float[] getBiasEncoders() {
		return myBiasEncoders;
	}
	
}

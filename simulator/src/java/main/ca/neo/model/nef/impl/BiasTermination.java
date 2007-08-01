/*
 * Created on 24-Apr-07
 */
package ca.neo.model.nef.impl;

import ca.neo.dynamics.Integrator;
import ca.neo.dynamics.LinearSystem;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Node;
import ca.neo.model.RealOutput;
import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.model.impl.RealOutputImpl;

public class BiasTermination extends DecodedTermination {

	private static final long serialVersionUID = 1L;
	
	private float[] myBiasEncoders;
	private String myBaseName;
	private float myStaticBias;
	
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
	
	public void setStaticBias(float bias) {
		myStaticBias = bias;
	}

	public void setValues(InstantaneousOutput values) throws SimulationException {
		RealOutput ro = (RealOutput) values;		
		super.setValues(new RealOutputImpl(new float[]{ro.getValues()[0]+myStaticBias}, ro.getUnits(), ro.getTime()));
	}

}

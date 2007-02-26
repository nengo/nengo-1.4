/*
 * Created on 20-Feb-07
 */
package ca.neo.model.nef;

import ca.neo.math.ApproximatorFactory;
import ca.neo.model.impl.NodeFactory;
import ca.neo.util.VectorGenerator;

public interface NEFEnsembleFactory {

	public NodeFactory getNodeFactory();
	
	public void setNodeFactory(NodeFactory factory);
	
	public VectorGenerator getEncoderFactory();
	
	public void setEncoderFactory(VectorGenerator factory);
	
	public VectorGenerator getEvalPointFactory();
	
	public void setEvalPointFactory(VectorGenerator factory);
	
	public ApproximatorFactory getApproximatorFactory();
	
	public void setApproximatorFactory(ApproximatorFactory factory);	
	
}

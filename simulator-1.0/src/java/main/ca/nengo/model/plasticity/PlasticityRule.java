/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "PlasticityRule.java". Description: 
"Specifies how the termination weights of an NEFEnsemble are modified depending 
  on presynaptic and postsynaptic state.
  
  TODO: the setters introduce state in a way that may not be necessary -- remove them or clone in EnsembleImpl.setPlasticityRule(...)
  
  @author Bryan Tripp"

The Initial Developer of the Original Code is Bryan Tripp & Centre for Theoretical Neuroscience, University of Waterloo. Copyright (C) 2006-2008. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of the GNU 
Public License license (the GPL License), in which case the provisions of GPL 
License are applicable  instead of those above. If you wish to allow use of your 
version of this file only under the terms of the GPL License and not to allow 
others to use your version of this file under the MPL, indicate your decision 
by deleting the provisions above and replace  them with the notice and other 
provisions required by the GPL License.  If you do not delete the provisions above,
a recipient may use your version of this file under either the MPL or the GPL License.
*/

/*
 * Created on 29-Jan-2007
 */
package ca.nengo.model.plasticity;

import java.io.Serializable;

import ca.nengo.model.InstantaneousOutput;

/**
 * Specifies how the termination weights of an NEFEnsemble are modified depending 
 * on presynaptic and postsynaptic state.
 * 
 * TODO: the setters introduce state in a way that may not be necessary -- remove them or clone in EnsembleImpl.setPlasticityRule(...)
 * 
 * @author Bryan Tripp
 */
public interface PlasticityRule extends Serializable, Cloneable {
	
	/**
	 * Provides potentially modulatory input to the rule.
	 *   
	 * Note that although modulatory input will most commonly be used here, an NEFEnsemble 
	 * will provide outputs of all Terminations via this method regardless of whether a 
	 * Termination has property Termination.MODULATORY=="true". 
	 * 
	 * @param name The name of a DecodedTermination onto the ensemble
	 * @param state The present value of output from the named Termination (may differ 
	 * 		from its input in terms of dynamics and dimension)
	 * @param time Simulation time at which state arrives at site of plasticity 
	 */
	public void setTerminationState(String name, InstantaneousOutput state, float time);
	
	/**
	 * Provides state or functional output, which may serve as an indication of 
	 * postsynaptic activity (used in Hebbian learning). 
	 *  
	 * @param name The name of a DecodedOrigin from the ensemble 
	 * @param state The present value of output from the named Origin 
	 * @param time Simulation time at which state arrives at site of plasticity 
	 */
	public void setOriginState(String name, InstantaneousOutput state, float time);
	
	/**
	 * @param transform The present transformation matrix of a Termination
	 * @param input The present input to the Termination 
	 * @param time Simulation time at which input arrives at site of plasticity
	 * @return The rate of change of each element in the transform (units per second) if input is RealOutput, 
	 * 		otherwise the increment of each element in the transform
	 */
	public float[][] getDerivative(float[][] transform, InstantaneousOutput input, float time); 
	
	public PlasticityRule clone() throws CloneNotSupportedException;
	
}

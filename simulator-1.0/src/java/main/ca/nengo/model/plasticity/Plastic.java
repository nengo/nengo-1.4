/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "Plastic.java". Description: 
"Something is Plastic if it contains one or more terminations that can be 
  modified (over simulation time) on the basis of a PlasticityRule.
    
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
 * Created on 15-Mar-07
 */
package ca.nengo.model.plasticity;

import ca.nengo.model.StructuralException;

/**
 * Something is <code>Plastic</code> if it contains one or more terminations that can be 
 * modified (over simulation time) on the basis of a PlasticityRule.
 *   
 * @author Bryan Tripp
 */
public interface Plastic {

	/**
	 * @param terminationName Name of termination to which plasticity rule applies.
	 * @return A rule that defines how the termination's transformation matrix elements change during simulation
	 * @throws StructuralException if the named Termination does not exist
	 */
	public PlasticityRule getPlasticityRule(String terminationName) throws StructuralException;
	
	/**
	 * @param terminationName Name of termination to which plasticity rule applies.  
	 * @param rule A rule that defines how the termination's transformation matrix elements change during simulation
	 * @throws StructuralException if the named Termination does not exist
	 */
	public void setPlasticityRule(String terminationName, PlasticityRule rule) throws StructuralException;

	/**
	 * @return Period after which plasticity rules are evaluated (defaults to every time step).
	 */
	public float getPlasticityInterval();
	
	/**
	 * @param time Period after which plasticity rules are evaluated (defaults to every time step).  
	 */
	public void setPlasticityInterval(float time);
	
	/**
	 * @return Names of Terminations for which plasticity rules can be set 
	 */
	public String[] getPlasticityRuleNames();
	
}

/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "CDecodedTermination.java". Description: 
""

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

package ca.nengo.ui.configurable.models;

import java.util.Map;

import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;
import ca.nengo.model.nef.NEFEnsemble;
import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.properties.PBoolean;
import ca.nengo.ui.configurable.properties.PFloat;
import ca.nengo.ui.configurable.properties.PTerminationWeights;
import ca.nengo.ui.models.nodes.widgets.UIDecodedTermination;

public class CDecodedTermination extends CProjection {
	private static final Property pModulatory = new PBoolean(
			"Modulatory",
			"Is the termination modulatory? Modulatory terminations effect the" +
			"populations in ways other than imparting current.",
			false);
	private static final Property pTauPSC = new PFloat(
			"tauPSC [s]",
			"Post-synaptic time constant, in seconds",
			0.01f,
			0,
			Float.MAX_VALUE);
	
	private NEFEnsemble nefEnsembleParent;

	private Property pTransformMatrix;

	public CDecodedTermination(NEFEnsemble nefEnsembleParent) {
		this.nefEnsembleParent = nefEnsembleParent;
		pName.setDescription("Name of the termination (must be unique)");
		pName.setDefaultValue("My Termination");
	}

	@Override public Property[] getSchema() {
		pTransformMatrix = new PTerminationWeights("Transformation weights",
				"A matrix that defines how the dimensions coming into the " +
				"termination are mapped to the ensemble's dimensions.",
				nefEnsembleParent.getDimension());

		Property[] zProperties = { pName, pTransformMatrix, pTauPSC, pModulatory };
		return zProperties;

	}

	public String getTypeName() {
		return UIDecodedTermination.typeName;
	}

	@Override protected boolean isNameAvailable(String name) {
		try {
			return nefEnsembleParent.getTermination(name) == null;
		} catch (StructuralException e) {
			return false;
		}
	}

	@Override
	protected Object createModel(Map<Property, Object> configuredProperties, String uniqueName) throws ConfigException {
		Termination term = null;
		try {
			term = nefEnsembleParent.addDecodedTermination(uniqueName,
					(float[][]) configuredProperties.get(pTransformMatrix), (Float) configuredProperties
							.get(pTauPSC), (Boolean) configuredProperties.get(pModulatory));

		} catch (StructuralException e) {
			e.printStackTrace();
		}

		return term;
	}

}

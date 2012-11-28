/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "PFunction.java". Description:
"Config Descriptor for Functions

  @author Shu Wu"

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

package ca.nengo.ui.configurable.properties;

import java.util.Vector;

import ca.nengo.config.ClassRegistry;
import ca.nengo.math.Function;
import ca.nengo.math.impl.FourierFunction;
import ca.nengo.math.impl.GaussianPDF;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.functions.ConfigurableFunction;
import ca.nengo.ui.configurable.functions.FnAdvanced;
import ca.nengo.ui.configurable.functions.FnConstant;
import ca.nengo.ui.configurable.functions.FnCustom;
import ca.nengo.ui.configurable.functions.FnReflective;
import ca.nengo.ui.configurable.panels.FunctionPanel;

/**
 * Config Descriptor for Functions
 * 
 * @author Shu Wu
 */
public class PFunction extends Property {

    private static final long serialVersionUID = 1L;

    private int myInputDimension;

    /**
     * @param name Name of the function
     * @param inputDimension Input dimensions
     * @param isInputDimensionEditable Can dimensionality be changed?
     * @param defaultValue Default function
     */
    public PFunction(String name, String description, Function defaultValue, int inputDimension) {
        super(name, description, defaultValue);
        this.myInputDimension = inputDimension;
    }

    @SuppressWarnings("unchecked")
	private ConfigurableFunction[] createConfigurableFunctions() {
        Vector<ConfigurableFunction> functions = new Vector<ConfigurableFunction>();

        functions.add(new FnConstant(myInputDimension));
        functions.add(new FnCustom(myInputDimension, false));

        if (myInputDimension == 1) {
            functions.add(new FnReflective(FourierFunction.class,
            		new Property[] {
            			new PFloat("Fundamental [Hz]",
            					"The smallest frequency represented, in Hertz", 0.5f),
				 		new PFloat("Cutoff [Hz]",
				 				"The largest frequency represented, in Hertz", 30), 
				 		new PFloat("RMS",
				 				"Root-mean-square amplitude of the signal", 0.5f), 
				 		new PLong("Seed",
				 				"Seed for the random number generator", 0)
            		}));
            functions.add(new FnReflective(GaussianPDF.class,
                    new Property[] {
            			new PFloat("Mean",
            					"Mean of the Gaussian distribution", 0),
            			new PFloat("Variance",
            					"Variance of the Gaussian disribution", 0.1f),
            			new PFloat("Peak",
            					"Maximum value of the Gaussian distribution (at the mode)", 1)
            		}));
        }

        for (Class<?> type : ClassRegistry.getInstance().getImplementations(Function.class)) {
            if (Function.class.isAssignableFrom(type)) {
                functions.add(new FnAdvanced((Class<? extends Function>) type));
            }
        }

        return functions.toArray(new ConfigurableFunction[0]);
    }

    @Override protected FunctionPanel createInputPanel() {
        ConfigurableFunction[] functions = createConfigurableFunctions();
        return new FunctionPanel(this, functions);
    }

    @Override public Class<Function> getTypeClass() {
        return Function.class;
    }

    public int getInputDimension() {
        return myInputDimension;
    }

}

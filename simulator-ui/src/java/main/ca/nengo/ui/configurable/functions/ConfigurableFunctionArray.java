/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "ConfigurableFunction.java". Description:
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

package ca.nengo.ui.configurable.functions;

import java.util.Map;

import ca.nengo.math.Function;
import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.IConfigurable;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.properties.PFunction;

/**
 * Configurable object which creates an array of functions
 * 
 * @author Shu Wu
 */
public class ConfigurableFunctionArray implements IConfigurable {

    /**
     * Number of functions to be created
     */
    private int outputDimension;

    /**
     * Dimensions of the functions to be created
     */
    private int inputDimension;

    /**
     * Array of functions to be created
     */
    private Function[] myFunctions;
    private Function[] defaultValues;

    /**
     * @param inputDimension TODO
     * @param outputDimension Number of functions to create
     * @param defaultValues TODO
     */
    public ConfigurableFunctionArray(int inputDimension, int outputDimension,
            Function[] defaultValues) {
        this.defaultValues = defaultValues;
        init(inputDimension, outputDimension);
    }

    /**
     * Initializes this instance
     * 
     * @param outputDimension
     *            number of functions to create
     */
    private void init(int inputDimension, int outputDimension) {
        this.inputDimension = inputDimension;
        this.outputDimension = outputDimension;
    }

    /**
     * @see ca.nengo.ui.configurable.IConfigurable#completeConfiguration(ca.nengo.ui.configurable.ConfigParam)
     */
    public void completeConfiguration(Map<Property, Object> properties) {
    	Property[] props = getSchema();
        myFunctions = new Function[outputDimension];
        for (int i = 0; i < outputDimension; i++) {
            myFunctions[i] = ((Function) properties.get(props[i]));
        }

    }

    /**
     * @see ca.nengo.ui.configurable.IConfigurable#getSchema()
     */
    public Property[] getSchema() {
        Property[] props = new Property[outputDimension];

        for (int i = 0; i < outputDimension; i++) {

            Function defaultValue = null;

            if (defaultValues != null && i < defaultValues.length && defaultValues[i] != null) {
                defaultValue = defaultValues[i];

            }
            PFunction function = new PFunction("Function " + i,
            		"The function to use for dimension " + i,
            		defaultValue, inputDimension);

            props[i] = function;
        }

        return props;
    }

    /**
     * @return Functions created
     */
    public Function[] getFunctions() {
        return myFunctions;
    }

    /**
     * @see ca.nengo.ui.configurable.IConfigurable#getTypeName()
     */
    public String getTypeName() {
        return outputDimension + "x Functions";
    }

    public void preConfiguration(Map<Property, Object> props) throws ConfigException {
        // do nothing
    }

    public String getDescription() {
        return getTypeName();
    }
	public String getExtendedDescription() {
		return null;
	}

}

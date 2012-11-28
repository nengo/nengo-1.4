/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "AbstractFn.java". Description:
"@author User"

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

import java.awt.Dialog;
import java.util.Map;

import ca.nengo.math.Function;
import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.IConfigurable;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.managers.UserConfigurer;
import ca.nengo.ui.lib.util.UserMessages;

/**
 * Describes how to configure a function through the IConfigurable interface.
 * 
 * @author Shu Wu
 */
public abstract class AbstractFn implements IConfigurable, ConfigurableFunction {

    private UserConfigurer configurer;
    private Function function;
    private Class<? extends Function> functionType;

    /**
     * @param typeName
     * @param functionType
     */
    public AbstractFn(Class<? extends Function> functionType) {
        this.functionType = functionType;
    }

    protected abstract Function createFunction(Map<Property, Object> props) throws ConfigException;

    /**
     * Creates the function through reflection of its constructor and
     * passing the user parameters to it
     *      
     * @see ca.nengo.ui.configurable.IConfigurable#completeConfiguration(ca.nengo.ui.configurable.ConfigParam)
     */
    public void completeConfiguration(Map<Property, Object> props) throws ConfigException {
        try {
            Function function = createFunction(props);
            setFunction(function);
        } catch (Exception e) {
            throw new ConfigException("Error creating function");
        }
    }

    public Function configureFunction(Dialog parent) {
        if (parent != null) {
            if (configurer == null) {
                configurer = new UserConfigurer(this, parent);
            }
            try {
                configurer.configureAndWait();
                return getFunction();
            } catch (ConfigException e) {
                e.defaultHandleBehavior();
            }
        } else {
            UserMessages.showError("Could not attach properties dialog");
        }
        return null;
    }

    public String getDescription() {
        return getTypeName();
    }

    /**
     * @return The function created
     */
    public Function getFunction() {
        return function;
    }

    public Class<? extends Function> getFunctionType() {
        return functionType;
    }

    /**
     * @see ca.nengo.ui.configurable.IConfigurable#getTypeName()
     */
    public String getTypeName() {
        return functionType.getSimpleName();
    }

    public void preConfiguration(Map<Property, Object> props) throws ConfigException {
        // do nothing
    }

    /**
     * @param function Function wrapper
     */
    public final void setFunction(Function function) {
        if (function != null && !getFunctionType().isInstance(function)) {
        	throw new IllegalArgumentException("Unexpected function type");
        }
        
        this.function = function;
    }

    @Override public String toString() {
        return getTypeName();
    }

    public String getExtendedDescription() {
		return null;
	}

}
/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "FnCustom.java". Description:
"Property descriptor for a function expression.

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

package ca.nengo.ui.configurable.functions;

import java.awt.Dialog;
import java.util.Map;

import ca.nengo.math.Function;
import ca.nengo.math.impl.DefaultFunctionInterpreter;
import ca.nengo.math.impl.PostfixFunction;
import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.managers.InterpreterFunctionConfigurer;
import ca.nengo.ui.configurable.properties.PInt;
import ca.nengo.ui.configurable.properties.PString;

/**
 * Lets the user type in a function to create
 * 
 * @author Shu Wu
 */
public class FnCustom extends AbstractFn {

    private static final String DIMENSION_STR = "Input Dimensions";
    private static final String DIMENSION_DESC = "The number of input dimensions to the function";
    private static final String EXPRESSION_STR = "Expression";
    private static final String EXPRESSION_DESC = "Expression to evaluate. " + 
    	"Refer to ensemble values as \"x#\", where # is the dimension index " + 
    	"(e.g., x0*x0 returns the square of the first dimension).<br>" +
    	"(Full documentation: <a href=\"http://nengo.ca/docs/html/tutorial3.html\">http://nengo.ca/docs/html/tutorial3.html</a>)";
    private static DefaultFunctionInterpreter interpreter = new DefaultFunctionInterpreter();

    private int myInputDimensions;
    private Property pExpression;
    private Property pDimensions;
    InterpreterFunctionConfigurer configurer;
    boolean isInputDimEditable;

    public FnCustom(int inputDimensions, boolean isInputDimEditable) {
        super(PostfixFunction.class);
        this.myInputDimensions = inputDimensions;
        this.isInputDimEditable = isInputDimEditable;
    }

    private Function parseFunction(Map<Property, Object> props) throws ConfigException {
        String expression = (String) props.get(pExpression);
        int dimensions = (Integer) props.get(pDimensions);

        Function function;
        try {
            function = interpreter.parse(expression, dimensions);
        } catch (Exception e) {
            throw new ConfigException(e.getMessage());
        }
        return function;
    }

    @Override protected Function createFunction(Map<Property, Object> props) throws ConfigException {
        return parseFunction(props);
    }

    @Override public Function configureFunction(Dialog parent) {
        if (configurer == null) {
            configurer = new InterpreterFunctionConfigurer(this, parent, interpreter);
        }
        try {
            configurer.configureAndWait();
            return getFunction();
        } catch (ConfigException e) {
            e.defaultHandleBehavior();
        }
        return null;
    }

    public Property[] getSchema() {
        String expression = null;
        int dim = myInputDimensions;

        PostfixFunction function = getFunction();

        if (function != null) {
            expression = function.getExpression();

            if (isInputDimEditable) {
                dim = function.getDimension();
            }
        }
        
        pExpression = new PString(EXPRESSION_STR, EXPRESSION_DESC, expression);
        pDimensions = new PInt(DIMENSION_STR, DIMENSION_DESC, dim);
        pDimensions.setEditable(isInputDimEditable);

        return new Property[] { pExpression, pDimensions };
    }

    @Override public PostfixFunction getFunction() {
        return (PostfixFunction) super.getFunction();
    }

    @Override public void preConfiguration(Map<Property, Object> props) throws ConfigException {
        parseFunction(props);
    }
}
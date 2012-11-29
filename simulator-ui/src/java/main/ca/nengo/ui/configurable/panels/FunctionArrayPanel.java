/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "FunctionArrayPanel.java". Description:
"Input panel for entering an Array of Functions

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

package ca.nengo.ui.configurable.panels;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ca.nengo.math.Function;
import ca.nengo.math.impl.ConstantFunction;
import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.PropertyInputPanel;
import ca.nengo.ui.configurable.functions.ConfigurableFunctionArray;
import ca.nengo.ui.configurable.managers.UserConfigurer;
import ca.nengo.ui.configurable.properties.PFunctionArray;
import ca.nengo.ui.lib.util.UserMessages;
import ca.nengo.ui.lib.util.Util;

/**
 * Input panel for entering an Array of Functions
 * 
 * @author Shu Wu
 */
public class FunctionArrayPanel extends PropertyInputPanel {

    private Function[] myFunctions;

    /**
     * Text field component for entering the dimensions of the function array
     */
    private JTextField tf;
    private int numFunctions;

    /**
     * @param property TODO
     * @param numFunctions TODO
     */
    public FunctionArrayPanel(PFunctionArray property, int numFunctions) {
        super(property);
        this.numFunctions = numFunctions;
        
        JLabel dimensions = new JLabel("Output Dimensions: ");
        tf = new JTextField(10);
        add(dimensions);
        add(tf);

        JButton configureFunction = new JButton(new EditFunctions());
        add(configureFunction);
    }

    /**
     * Edits the Function Array using a child dialog
     */
    protected void editFunctionArray() {
        if (!isOutputDimensionsSet()) {
            UserMessages.showWarning("Output dimensions not set");
            return;
        }

        Container parent = getJPanel().getParent();
        while (parent != null) {
            if (parent instanceof JDialog) {
                break;
            }
            parent = parent.getParent();
        }

        if (parent != null && parent instanceof JDialog) {
            ConfigurableFunctionArray configurableFunctions = new ConfigurableFunctionArray(
                    getInputDimension(), getOutputDimension(), getValue());

            UserConfigurer config = new UserConfigurer(configurableFunctions, parent);
            try {
                config.configureAndWait();
                setValue(configurableFunctions.getFunctions());
            } catch (ConfigException e) {
                e.defaultHandleBehavior();
            }

        } else {
            UserMessages.showError("Could not attach properties dialog");
        }

    }

    @Override public PFunctionArray getDescriptor() {
        return (PFunctionArray) super.getDescriptor();
    }

    public int getOutputDimension() {
        return Integer.parseInt(tf.getText());
    }

    @Override public Function[] getValue() {
        return myFunctions;
    }

    /**
     * @return True if Function Array dimensions has been set
     */
    public boolean isOutputDimensionsSet() {
        String textValue = tf.getText();

        if (textValue == null || textValue.compareTo("") == 0) {
            return false;
        }

        try {
            getOutputDimension();
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    @Override public boolean isValueSet() {
    	if (!isOutputDimensionsSet()) {
    		return false;
    	}
    	
        if (myFunctions != null && (myFunctions.length == getOutputDimension())) {
            return true;
        } else {
            setStatusMsg("Functions not set");
        }
        
        if (myFunctionsWr == null || myFunctionsWr.length != getOutputDimension()) {
            myFunctionsWr = new Function[getOutputDimension()];
            for (int i=0; i<getOutputDimension(); i++) {
                myFunctions[i] = new ConstantFunction(numFunctions, 0.0f);
            }
            return true;
        }

        return false;
    }

    /**
     * @param dimensions
     *            Dimensions of the function array
     */
    public void setDimensions(int dimensions) {
        tf.setText(dimensions + "");

    }

    @Override public void setValue(Object value) {
        Function[] functions = (Function[]) value;

        for (Function function : functions) {
            if (function.getDimension() != getInputDimension()) {
                Util.debugMsg("Saved functions are of a different dimension, they can't be used");
                return;
            }
        }

        if (value != null) {
            myFunctions = functions;
            setDimensions(myFunctions.length);
            setStatusMsg("");
        } else {

        }
    }

    /**
     * Edit Functions Action
     * 
     * @author Shu Wu
     */
    class EditFunctions extends AbstractAction {

        private static final long serialVersionUID = 1L;

        public EditFunctions() {
            super("Set Functions");
        }

        public void actionPerformed(ActionEvent e) {
            editFunctionArray();

        }

    }

    public int getInputDimension() {
        return numFunctions;
    }
}

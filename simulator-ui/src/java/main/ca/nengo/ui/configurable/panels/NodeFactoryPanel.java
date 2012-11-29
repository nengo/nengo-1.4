/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "NodeFactoryPanel.java". Description:
"Input Panel for selecting and configuring a Node Factory

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

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;

import ca.nengo.model.impl.NodeFactory;
import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.PropertyInputPanel;
import ca.nengo.ui.configurable.models.ModelFactory;
import ca.nengo.ui.configurable.nodes.AbstractNode;
import ca.nengo.ui.configurable.nodes.CALIFNeuronFactory;
import ca.nengo.ui.configurable.nodes.CLIFNeuronFactory;
import ca.nengo.ui.configurable.nodes.CLinearNeuronFactory;
import ca.nengo.ui.configurable.nodes.CSigmoidNeuronFactory;
import ca.nengo.ui.configurable.nodes.CSpikingNeuronFactory;
import ca.nengo.ui.lib.util.UserMessages;

/**
 * Input Panel for selecting and configuring a Node Factory
 * 
 * @author Shu Wu
 */
public class NodeFactoryPanel extends PropertyInputPanel {

    private static final AbstractNode[] nodeFactories = new AbstractNode[] {
        new CLinearNeuronFactory(), new CSigmoidNeuronFactory(), new CLIFNeuronFactory(),
        new CALIFNeuronFactory(), new CSpikingNeuronFactory() };

    private JComboBox factorySelector;
    private NodeFactory myNodeFactory;
    private AbstractNode selectedItem;

    public NodeFactoryPanel(Property property) {
        super(property);
        
        factorySelector = new JComboBox(nodeFactories);
        add(factorySelector);

        // Reset value if the combo box selection has changed
        factorySelector.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (factorySelector.getSelectedItem() != selectedItem) {
                    setValue(null);
                }
            }

        });

        JButton configureBtn = new JButton(new AbstractAction("Set") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent arg0) {
                configureNodeFactory();
            }
        });

        add(configureBtn);
    }

    private void configureNodeFactory() {
        selectedItem = (AbstractNode) factorySelector.getSelectedItem();

        try {
            NodeFactory model = (NodeFactory) ModelFactory.constructModel(selectedItem);
            setValue(model);
        } catch (ConfigException e) {
            e.defaultHandleBehavior();
        } catch (Exception e) {
            UserMessages.showError("Could not configure Node Factory: " + e.getMessage());
        }
    }

    @Override public Object getValue() {
        return myNodeFactory;
    }

    @Override public boolean isValueSet() {
        if (myNodeFactory != null) {
            return true;
        } else {
            setStatusMsg("Node Factory must be set");
            return false;
        }
    }

    @Override public void setValue(Object value) {
        if (value == null) {
            myNodeFactory = null;
            return;
        }

        if (value instanceof NodeFactory) {
            myNodeFactory = (NodeFactory) value;
            setStatusMsg("");

            /*
             * Update the combo box selector with the selected Node Factory
             */
            boolean foundComboItem = false;
            for (AbstractNode nodeFactoryItem : nodeFactories) {

                if (nodeFactoryItem.getType().isInstance(myNodeFactory)) {
                    selectedItem = nodeFactoryItem;
                    factorySelector.setSelectedItem(selectedItem);
                    foundComboItem = true;
                    break;
                }
            }
            if (!foundComboItem) {
                throw new IllegalArgumentException("Unsupported Node Factory");
            }

        } else {
            throw new IllegalArgumentException("Value is not a Node Factory");
        }
    }
}

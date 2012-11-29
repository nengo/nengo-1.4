/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "CNEFEnsemble.java". Description:
"Advanced properties, these may not necessarily be configued, so"

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

import ca.nengo.math.ApproximatorFactory;
import ca.nengo.math.impl.WeightedCostApproximator;
import ca.nengo.model.Node;
import ca.nengo.model.StructuralException;
import ca.nengo.model.impl.NodeFactory;
import ca.nengo.model.nef.NEFEnsemble;
import ca.nengo.model.nef.NEFEnsembleFactory;
import ca.nengo.model.nef.impl.NEFEnsembleFactoryImpl;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.Sign;
import ca.nengo.ui.configurable.properties.PApproximator;
import ca.nengo.ui.configurable.properties.PEncodingDistribution;
import ca.nengo.ui.configurable.properties.PFloat;
import ca.nengo.ui.configurable.properties.PInt;
import ca.nengo.ui.configurable.properties.PNodeFactory;
import ca.nengo.ui.configurable.properties.PSign;
import ca.nengo.ui.models.nodes.UINEFEnsemble;
import ca.nengo.util.VectorGenerator;
import ca.nengo.util.impl.RandomHypersphereVG;
import ca.nengo.util.impl.Rectifier;

public class CNEFEnsemble extends CNode {
    private static final Property pApproximator = new PApproximator("Decoding Sign",
    		"Limit the decoders to be all positive or all negative");
    private static final Property pDim = new PInt("Dimensions",
    		"The number of dimensions that this ensemble represents",
    		1, 1, Integer.MAX_VALUE);
    private static final Property pEncodingDistribution = new PEncodingDistribution("Encoding Distribution",
    		"Distribution of encoders, ranging from uniformly chosen (default) to all encoders aligned to an axis");
    private static final Property pEncodingSign = new PSign("Encoding Sign",
    		"Limit the encoders to be all positive or all negative");
    private static final Property pNodeFactory = new PNodeFactory("Node Factory",
    		"The type of neuron that this ensemble is made up of. " +
    		"See online documentation for adding custom neuron models.");
    private static final Property pNumOfNodes = new PInt("Number of Nodes",
    		"Number of neurons in the ensemble", 50, 1, Integer.MAX_VALUE);
    private static final Property pRadius = new PFloat("Radius",
    		"Largest magnitude that can be accurately represented by the ensemble",
    		1, 0, Float.MAX_VALUE);
    private static final Property pNoise = new PFloat("Noise",
    		"Expected ratio of the noise amplitude to the signal amplitude to use when solving for decoders",
    		0.1f, 0, 1);

    private static final Property[] zSchema = new Property[]
    		{pNumOfNodes, pDim, pNodeFactory, pRadius, pApproximator,
    	     pEncodingDistribution, pEncodingSign, pNoise};

    public CNEFEnsemble() {
        pName.setDescription("Name of the ensemble");
        pName.setDefaultValue("My Ensemble");
        pApproximator.setAdvanced(true);
        pEncodingDistribution.setAdvanced(true);
        pEncodingSign.setAdvanced(true);
        pNoise.setAdvanced(true);
    }

    protected Node createNode(Map<Property, Object> prop, String name) {
        try {

            NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();
            Integer numOfNeurons = (Integer) prop.get(pNumOfNodes);
            Integer dimensions = (Integer) prop.get(pDim);

            /*
             * Advanced properties, these may not necessarily be configued, so
             */
            ApproximatorFactory approxFactory = (ApproximatorFactory) prop.get(pApproximator);
            NodeFactory nodeFactory = (NodeFactory) prop.get(pNodeFactory);
            Sign encodingSign = (Sign) prop.get(pEncodingSign);
            Float encodingDistribution = (Float) prop.get(pEncodingDistribution);
            Float radius = (Float) prop.get(pRadius);
            Float noise = (Float) prop.get(pNoise);

            if (nodeFactory != null) {
                ef.setNodeFactory(nodeFactory);
            }

            if (approxFactory != null) {
                ef.setApproximatorFactory(approxFactory);
            }
            if (noise != null) {
                ApproximatorFactory f=ef.getApproximatorFactory();
                if (f instanceof WeightedCostApproximator.Factory) {
                    ((WeightedCostApproximator.Factory)f).setNoise(noise);
                }
            }

            if (encodingSign != null) {
                if (encodingDistribution == null) {
                    encodingDistribution = 0f;
                }
                VectorGenerator vectorGen = new RandomHypersphereVG(true, 1, encodingDistribution);
                if (encodingSign == Sign.Positive) {
                    vectorGen = new Rectifier(vectorGen, true);
                } else if (encodingSign == Sign.Negative) {
                    vectorGen = new Rectifier(vectorGen, false);
                }
                ef.setEncoderFactory(vectorGen);
            }

            if (radius==null) {
                NEFEnsemble ensemble = ef.make(name, numOfNeurons, dimensions);
                return ensemble;
            } else {
                float[] radii=new float[dimensions];
                for (int i=0; i<dimensions; i++) {
                    radii[i]=radius.floatValue();
                }
                NEFEnsemble ensemble = ef.make(name, numOfNeurons, radii);
                return ensemble;
            }
        } catch (StructuralException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override public Property[] getNodeSchema() {
        return zSchema;
    }

    public String getTypeName() {
        return UINEFEnsemble.typeName;
    }

}
/*
 * Created on 12-Feb-2007
 */
package ca.neo.model.microcircuit;

import ca.neo.model.Node;

/**
 * <p>An interconnected group of neurons, typically localized together and numbering between 
 * two and a few thousand.</p>
 * 
 * <p>Microcircuits are an intermediate level of organization, between neurons and systems. 
 * Like neurons, multiple microcircuits can be organized into Ensembles. A subcortical nucleus 
 * can be modelled as a microcircuit or as an ensemble of microcircuits. A cortical column 
 * can be modelled as a microcircuit.</p>
 * 
 * <p>Microcircuits have the following benefits. 1) Like neurons models they can be persisted 
 * and reused in multiple models. 2) They can expose to the rest of a network parameters that are 
 * meaningful to the operation of the microcircuit while encapsulating internal details. 3) They 
 * can run with a different time step than the rest of the network.
 *  
 * @author Bryan Tripp
 */
public interface Microcircuit extends Node {

}

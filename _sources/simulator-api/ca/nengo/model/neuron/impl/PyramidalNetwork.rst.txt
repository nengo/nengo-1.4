.. java:import:: java.util Random

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math.impl DefaultFunctionInterpreter

.. java:import:: ca.nengo.math.impl IndicatorPDF

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl EnsembleImpl

.. java:import:: ca.nengo.model.impl NetworkImpl

.. java:import:: ca.nengo.model.impl NodeFactory

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef.impl NEFEnsembleFactoryImpl

.. java:import:: ca.nengo.model.nef.impl NEFEnsembleImpl

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.model.neuron SpikeGenerator

.. java:import:: ca.nengo.model.neuron SynapticIntegrator

.. java:import:: ca.nengo.model.neuron.impl RateFunctionSpikeGenerator.PoiraziDendriteSigmoidFactory

.. java:import:: ca.nengo.util MU

PyramidalNetwork
================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public class PyramidalNetwork extends NetworkImpl

   Non Linear Network This network is a model of Pyramidal Cells found in the central nervous system These cells contain an active dendritic tree with functional computation occuring within the dendrites themselves. The implementation chosen involves creating a network of Ensembles(dendrites and cell bodies) such that one ensemble of "dendrites" projects to a specific termination in the "soma" ensemble with weights chosen in such a way that only one node of the soma is given an input from a specific dendritic branch.

   :author: Albert Mallia

Constructors
------------
PyramidalNetwork
^^^^^^^^^^^^^^^^

.. java:constructor:: public PyramidalNetwork(String name, int dim, int size, IndicatorPDF dendriteRange, String f, boolean oneDim, boolean LIFDendrites, boolean spikingLIFDendrites) throws StructuralException
   :outertype: PyramidalNetwork

   :param name: Name of the network
   :param dim: Dimensions of the network
   :param size: Number of pyramidal neurons in the network
   :param dendriteRange: Range of dendrites per neuron
   :param f: function to be calculated at the dendrites
   :param oneDim: whether or not terminations to the network are unidimensional or multidimensional
   :param LIFDendrites: Use LIF dendrites?
   :param spikingLIFDendrites: Use spiking LIF dendrites?
   :throws StructuralException: if name isn't unique

PyramidalNetwork
^^^^^^^^^^^^^^^^

.. java:constructor:: public PyramidalNetwork(String name, int dim, int size, IndicatorPDF dendriteRange, String f, boolean oneDim) throws StructuralException
   :outertype: PyramidalNetwork

   :param name: Name of the network
   :param dim: Dimensions of the network
   :param size: Number of pyramidal neurons in the network
   :param dendriteRange: Range of dendrites per neuron
   :param f: function to be calculated at the dendrites
   :param oneDim: whether or not terminations to the network are unidimensional or multidimensional
   :throws StructuralException: if name is taken

PyramidalNetwork
^^^^^^^^^^^^^^^^

.. java:constructor:: public PyramidalNetwork(String name, int dim, int size, IndicatorPDF dendriteRange, String f) throws StructuralException
   :outertype: PyramidalNetwork

   :param name: Name of the network
   :param dim: Dimensions of the network
   :param size: Number of pyramidal neurons in the network
   :param dendriteRange: Range of dendrites per neuron
   :param f: function to be calculated at the dendrites
   :throws StructuralException: if name isn't unique

PyramidalNetwork
^^^^^^^^^^^^^^^^

.. java:constructor:: public PyramidalNetwork(String name, int dim, int size, IndicatorPDF dendriteRange) throws StructuralException
   :outertype: PyramidalNetwork

   :param name: Name of the network
   :param dim: Dimensions of the network
   :param size: Number of pyramidal neurons in the network
   :param dendriteRange: Range of dendrites per neuron
   :throws StructuralException: if name isn't unique

PyramidalNetwork
^^^^^^^^^^^^^^^^

.. java:constructor:: public PyramidalNetwork(String name, int dim, int size) throws StructuralException
   :outertype: PyramidalNetwork

   Gives a default subunit size of 100

   :param name: Name of the network
   :param dim: Dimensions of the network
   :param size: Number of pyramidal neurons in the network
   :throws StructuralException: if name isn't unique

PyramidalNetwork
^^^^^^^^^^^^^^^^

.. java:constructor:: public PyramidalNetwork(String name, int dim) throws StructuralException
   :outertype: PyramidalNetwork

   Gives a default number of 20 neurons and 100 dendrites per neuron

   :param name: Name of the network
   :param dim: Dimensions of the network
   :throws StructuralException: if name isn't unique

PyramidalNetwork
^^^^^^^^^^^^^^^^

.. java:constructor:: public PyramidalNetwork(String name) throws StructuralException
   :outertype: PyramidalNetwork

   Gives a default of 1 dimension

   :param name: Name of the network
   :throws StructuralException: if name isn't unique

PyramidalNetwork
^^^^^^^^^^^^^^^^

.. java:constructor:: public PyramidalNetwork() throws StructuralException
   :outertype: PyramidalNetwork

   Default constructor

   :throws StructuralException: if name isn't unique

Methods
-------
addDecodedTermination
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void addDecodedTermination(String name, float[][] transform, float tauPSC, boolean modulatory) throws StructuralException
   :outertype: PyramidalNetwork

   Adds a standard decoded termination to the network

   :param name: Name of the termination
   :param transform: Weight matrix for the termination
   :param tauPSC: PSC time constant
   :param modulatory: Modulatory?
   :throws StructuralException: if termination already exists

addOneDimTermination
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void addOneDimTermination(String name, int dimension, float transform) throws StructuralException
   :outertype: PyramidalNetwork

   Adds a one dimension termination to the network This allows the user to specify which dimension the input value should be stored in as opposed to sending in a weight matrix to do so A multiplier transform is also expected

   :param name: Name of the termination
   :param dimension: Dimension for input to be stored in
   :param transform: Transform for input value
   :throws StructuralException: if termination exists

addOneDimTermination
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void addOneDimTermination(String name, int dimension) throws StructuralException
   :outertype: PyramidalNetwork

   Default one dimension termination with no transform Sets a default transform of 1

   :param name: Name of the termination
   :param dimension: Dimension input values are to be stored in
   :throws StructuralException: if termination exists

createFunctionOriginDendrites
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void createFunctionOriginDendrites() throws StructuralException
   :outertype: PyramidalNetwork

   Creates an origin at the dendrite level with a user specified function The value calculated at the dendrites is then transferred to the soma ensemble

   :throws StructuralException: if decoded origin already exists

getDendrites
^^^^^^^^^^^^

.. java:method:: public NEFEnsemble getDendrites(int index)
   :outertype: PyramidalNetwork

   Mainly used for testing purposes when trying to find proper scale values

   :param index: index number of dendritic tree
   :return: Dendritic ensemble at given index number

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: PyramidalNetwork

getOrigin
^^^^^^^^^

.. java:method:: public Origin getOrigin() throws StructuralException
   :outertype: PyramidalNetwork

   :throws StructuralException: if origin doesn't exist
   :return: Exposed network origin X (from the soma)

getRange
^^^^^^^^

.. java:method:: public float getRange(int index)
   :outertype: PyramidalNetwork

   For testing

   :param index: dendrite ensemble for which range is being returned
   :return: the range of scale values for a particular dendrite ensemble

getScales
^^^^^^^^^

.. java:method:: public float[] getScales(int index)
   :outertype: PyramidalNetwork

   Gets the scale values for a particular dendritic ensemble

   :param index: index number for dendritic ensemble
   :return: returns the scale value for each node in the ensemble

getSoma
^^^^^^^

.. java:method:: public NEFEnsemble getSoma()
   :outertype: PyramidalNetwork

   :return: Soma ensemble

makeNetwork
^^^^^^^^^^^

.. java:method:: public void makeNetwork() throws StructuralException
   :outertype: PyramidalNetwork

   Creates nodes and calls methods to make all origins, terminations, and projections

   :throws StructuralException: if name isn't unique


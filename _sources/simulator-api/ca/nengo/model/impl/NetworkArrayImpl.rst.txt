.. java:import:: java.util ArrayList

.. java:import:: java.util HashMap

.. java:import:: java.util LinkedHashMap

.. java:import:: java.util Map

.. java:import:: java.util Properties

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model PreciseSpikeOutput

.. java:import:: ca.nengo.model RealOutput

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SpikeOutput

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.nef.impl DecodedTermination

.. java:import:: ca.nengo.model.nef.impl NEFEnsembleImpl

.. java:import:: ca.nengo.model.nef.impl DecodedOrigin

.. java:import:: ca.nengo.model.plasticity.impl PESTermination

.. java:import:: ca.nengo.model.plasticity.impl PlasticEnsembleImpl

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

NetworkArrayImpl
================

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class NetworkArrayImpl extends NetworkImpl

   Default implementation of Network Array.

   :author: Xuan Choo, Daniel Rasmussen

Constructors
------------
NetworkArrayImpl
^^^^^^^^^^^^^^^^

.. java:constructor:: public NetworkArrayImpl(String name, NEFEnsembleImpl[] nodes) throws StructuralException
   :outertype: NetworkArrayImpl

   Create a network holding an array of nodes. An 'X' Origin is automatically created which concatenates the values of each internal element's 'X' Origin. This object is meant to be created using :func:`nef.Network.make_array()`, allowing for the efficient creation of neural groups that can represent large vectors. For example, the following code creates a NetworkArray consisting of 50 ensembles of 1000 neurons, each of which represents 10 dimensions, resulting in a total of 500 dimensions represented:: net=nef.Network('Example Array') A=net.make_array('A',neurons=1000,length=50,dimensions=10,quick=True) The resulting NetworkArray object can be treated like a normal ensemble, except for the fact that when computing nonlinear functions, you cannot use values from different ensembles in the computation, as per NEF theory.

   :param name: The name of the NetworkArray to create
   :param nodes: The ca.nengo.model.nef.NEFEnsemble nodes to combine together
   :throws StructuralException:

Methods
-------
addDecodedOrigin
^^^^^^^^^^^^^^^^

.. java:method:: public Origin addDecodedOrigin(String name, Function[] functions, String nodeOrigin) throws StructuralException
   :outertype: NetworkArrayImpl

   Create a new Origin. A new origin is created on each of the ensembles, and these are grouped together to create an output. This method uses the same signature as ca.nengo.model.nef.NEFEnsemble.addDecodedOrigin()

   :param name: The name of the newly created origin
   :param functions: A list of ca.nengo.math.Function objects to approximate at this origin
   :param nodeOrigin: Name of the base Origin to use to build this function approximation (this will always be 'AXON' for spike-based synapses)
   :throws StructuralException:
   :return: Origin that encapsulates all of the internal node origins

addDecodedOrigin
^^^^^^^^^^^^^^^^

.. java:method:: public Origin addDecodedOrigin(String name, Function[] functions, String nodeOrigin, boolean splitFunctions) throws StructuralException
   :outertype: NetworkArrayImpl

   Create a new origin by splitting the given functions across the nodes. This method uses the same signature as ca.nengo.model.nef.NEFEnsemble.addDecodedOrigin()

   :param name: The name of the newly created origin
   :param functions: A list of ca.nengo.math.Function objects to approximate at this origin
   :param nodeOrigin: Name of the base Origin to use to build this function approximation (this will always be 'AXON' for spike-based synapses)
   :param splitFunctions: True if the functions should be split across the nodes, otherwise this behaves the same as the default addDecodedOrigin
   :throws StructuralException:
   :return: Origin that encapsulates all of the internal node origins

addDecodedTermination
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Termination addDecodedTermination(String name, float[][] matrix, float tauPSC) throws StructuralException
   :outertype: NetworkArrayImpl

   Create a new decoded termination. A new termination is created on each of the ensembles, which are then grouped together.

   :param name: The name of the newly created termination
   :param matrix: Transformation matrix which defines a linear map on incoming information, onto the space of vectors that can be represented by this NetworkArray. The first dimension is taken as matrix columns, and must have the same length as the Origin that will be connected to this Termination. The second dimension is taken as matrix rows, and must have the same length as the encoders of this NEFEnsemble.
   :param tauPSC: Post-synaptic time constant
   :param modulatory: Boolean value that is False for normal connections, True for modulatory connections (which adjust neural properties rather than the input current)
   :throws StructuralException:
   :return: Termination that encapsulates all of the internal node terminations

addDecodedTermination
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Termination addDecodedTermination(String name, float[][] matrix, float tauPSC, boolean modulatory) throws StructuralException
   :outertype: NetworkArrayImpl

addIndexTermination
^^^^^^^^^^^^^^^^^^^

.. java:method:: public Termination addIndexTermination(String name, float[][] matrix, float tauPSC) throws StructuralException
   :outertype: NetworkArrayImpl

   Create a new termination. A new termination is created on the specified ensembles, which are then grouped together. This termination does not use NEF-style encoders; instead, the matrix is the actual connection weight matrix. Often used for adding an inhibitory connection that can turn off selected ensembles within the array (by setting *matrix* to be all -10, for example).

   :param string: name: the name of the newly created origin
   :param matrix: synaptic connection weight matrix (NxM where M is the total number of neurons in the ensembles to be connected)
   :param float: tauPSC: post-synaptic time constant
   :param boolean: isModulatory: False for normal connections, True for modulatory connections (which adjust neural properties rather than the input current)
   :param index: The indexes of the ensembles to connect to. If set to None, this function behaves exactly like addTermination().
   :return: the new termination

addIndexTermination
^^^^^^^^^^^^^^^^^^^

.. java:method:: public Termination addIndexTermination(String name, float[][] matrix, float tauPSC, boolean isModulatory) throws StructuralException
   :outertype: NetworkArrayImpl

addIndexTermination
^^^^^^^^^^^^^^^^^^^

.. java:method:: public Termination addIndexTermination(String name, float[][] matrix, float tauPSC, int[] index) throws StructuralException
   :outertype: NetworkArrayImpl

addIndexTermination
^^^^^^^^^^^^^^^^^^^

.. java:method:: public Termination addIndexTermination(String name, float[][] matrix, float tauPSC, boolean isModulatory, int[] index) throws StructuralException
   :outertype: NetworkArrayImpl

addNode
^^^^^^^

.. java:method:: public void addNode(Node node) throws StructuralException
   :outertype: NetworkArrayImpl

   **See also:** :java:ref:`ca.nengo.model.Network.addNode(ca.nengo.model.Node)`

addPlasticTermination
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Termination addPlasticTermination(String name, float[][] weights, float tauPSC, float[][] decoders) throws StructuralException
   :outertype: NetworkArrayImpl

addPlasticTermination
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Termination addPlasticTermination(String name, float[][] weights, float tauPSC, float[][] decoders, WeightFunc weightFunc) throws StructuralException
   :outertype: NetworkArrayImpl

   Create a new plastic termination. A new termination is created on each of the ensembles, which are then grouped together.

   :param name: The name of the newly created PES termination
   :param weights: Synaptic connection weight matrix (NxM where N is the total number of neurons in the NetworkArray)
   :param tauPSC: Post-synaptic time constant (which adjust neural properties rather than the input current)
   :param weightFunc: object wrapping a function that consumes a weight matrix and returns a modified weight matrix
   :throws StructuralException:
   :return: Termination that encapsulates all of the internal node terminations

addTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination addTermination(String name, float[][] matrix, float tauPSC) throws StructuralException
   :outertype: NetworkArrayImpl

   Create a new termination. A new termination is created on each of the ensembles, which are then grouped together. This termination does not use NEF-style encoders; instead, the matrix is the actual connection weight matrix. Often used for adding an inhibitory connection that can turn off the whole array (by setting *matrix* to be all -10, for example).

   :param name: The name of the newly created termination
   :param weights: Synaptic connection weight matrix (NxM where N is the total number of neurons in the NetworkArray)
   :param tauPSC: Post-synaptic time constant
   :param modulatory: Boolean value that is False for normal connections, True for modulatory connections (which adjust neural properties rather than the input current)
   :throws StructuralException:
   :return: Termination that encapsulates all of the internal node terminations

addTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination addTermination(String name, float[][] weights, float tauPSC, boolean modulatory) throws StructuralException
   :outertype: NetworkArrayImpl

addTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination addTermination(String name, float[][][] matrix, float tauPSC) throws StructuralException
   :outertype: NetworkArrayImpl

   Create a new termination. A new termination is created on each of the ensembles, which are then grouped together. This termination does not use NEF-style encoders; instead, the matrix is the actual connection weight matrix. Often used for adding an inhibitory connection that can turn off the whole array (by setting *matrix* to be all -10, for example).

   :param name: The name of the newly created termination
   :param weights: Synaptic connection weight matrix (LxNxM where L is the number of nodes in the array, N is the number of neurons in each node, and M is the dimensionality of each node)
   :param tauPSC: Post-synaptic time constant
   :param modulatory: Boolean value that is False for normal connections, True for modulatory connections (which adjust neural properties rather than the input current)
   :throws StructuralException:
   :return: Termination that encapsulates all of the internal node terminations

addTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination addTermination(String name, float[][][] weights, float tauPSC, boolean modulatory) throws StructuralException
   :outertype: NetworkArrayImpl

clone
^^^^^

.. java:method:: @Override public NetworkArrayImpl clone() throws CloneNotSupportedException
   :outertype: NetworkArrayImpl

createEnsembleOrigin
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void createEnsembleOrigin(String name) throws StructuralException
   :outertype: NetworkArrayImpl

   Create an Origin that concatenates the values of internal Origins.

   :param name: The name of the Origin to create. Each internal node must already have an Origin with that name.
   :throws StructuralException:

exposeAxons
^^^^^^^^^^^

.. java:method:: public void exposeAxons() throws StructuralException
   :outertype: NetworkArrayImpl

   Exposes the AXON terminations of each ensemble in the network.

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: NetworkArrayImpl

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsemble.getDimension()`

getEncoders
^^^^^^^^^^^

.. java:method:: public float[][] getEncoders()
   :outertype: NetworkArrayImpl

   Returns the encoders for the whole network array (the encoders of each population within the array concatenated together).

   :return: encoders of each neuron in the network array

getHistory
^^^^^^^^^^

.. java:method:: public TimeSeries getHistory(String stateName) throws SimulationException
   :outertype: NetworkArrayImpl

   **See also:** :java:ref:`ca.nengo.model.Probeable.getHistory(java.lang.String)`

getNeurons
^^^^^^^^^^

.. java:method:: public int getNeurons()
   :outertype: NetworkArrayImpl

getNodeDimension
^^^^^^^^^^^^^^^^

.. java:method:: public int[] getNodeDimension()
   :outertype: NetworkArrayImpl

getNodes
^^^^^^^^

.. java:method:: public Node[] getNodes()
   :outertype: NetworkArrayImpl

   Gets the nodes in the proper order from the network array. The NetworkImpl version of this function relies on the nodeMap object which is sometimes out of order.

   :return: the nodes in this network array

getTerminations
^^^^^^^^^^^^^^^

.. java:method:: public Termination[] getTerminations()
   :outertype: NetworkArrayImpl

   **See also:** :java:ref:`ca.nengo.model.Network.getTerminations()`

learn
^^^^^

.. java:method:: public void learn(String learnTerm, String modTerm, float rate)
   :outertype: NetworkArrayImpl

   Sets learning parameters on learned terminations in the array.

   :param learnTerm: name of the learned termination
   :param modTerm: name of the modulatory termination
   :param rate: learning rate

learn
^^^^^

.. java:method:: public void learn(String learnTerm, String modTerm, float rate, boolean oja)
   :outertype: NetworkArrayImpl

   Sets learning parameters on learned terminations in the array.

   :param learnTerm: name of the learned termination
   :param modTerm: name of the modulatory termination
   :param rate: learning rate
   :param oja: whether or not to use Oja smoothing

listStates
^^^^^^^^^^

.. java:method:: public Properties listStates()
   :outertype: NetworkArrayImpl

   **See also:** :java:ref:`ca.nengo.model.Probeable.listStates()`

releaseMemory
^^^^^^^^^^^^^

.. java:method:: public void releaseMemory()
   :outertype: NetworkArrayImpl

   Releases memory of all ensembles in the network.

setLearning
^^^^^^^^^^^

.. java:method:: public void setLearning(boolean learn)
   :outertype: NetworkArrayImpl

   Sets learning on/off for all ensembles in the network.

   :param learn: true if the ensembles are learning, else false

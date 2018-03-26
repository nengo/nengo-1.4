.. java:import:: ca.nengo.math ApproximatorFactory

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model.impl NodeFactory

.. java:import:: ca.nengo.util VectorGenerator

NEFEnsembleFactory
==================

.. java:package:: ca.nengo.model.nef
   :noindex:

.. java:type:: public interface NEFEnsembleFactory

   Provides a convenient and configurable way to create NEFEnsembles.

   :author: Bryan Tripp

Methods
-------
getApproximatorFactory
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public ApproximatorFactory getApproximatorFactory()
   :outertype: NEFEnsembleFactory

   :return: The factory that creates LinearApproximators used in decoding ensemble output

getEncoderFactory
^^^^^^^^^^^^^^^^^

.. java:method:: public VectorGenerator getEncoderFactory()
   :outertype: NEFEnsembleFactory

   :return: The VectorGenerator used to create encoding vectors that are associated with each Node in a new Ensemble

getEvalPointFactory
^^^^^^^^^^^^^^^^^^^

.. java:method:: public VectorGenerator getEvalPointFactory()
   :outertype: NEFEnsembleFactory

   :return: The VectorGenerator used to generate the vector states at which decoding functions are evaluated

getNodeFactory
^^^^^^^^^^^^^^

.. java:method:: public NodeFactory getNodeFactory()
   :outertype: NEFEnsembleFactory

   :return: The NodeFactory used to create Nodes that make up new Ensembles

make
^^^^

.. java:method:: public NEFEnsemble make(String name, int n, int dim) throws StructuralException
   :outertype: NEFEnsembleFactory

   :param name: Name of the NEFEnsemble
   :param n: Number of neurons in the ensemble
   :param dim: Dimension of the ensemble.
   :throws StructuralException: if there is any error attempting to create the ensemble
   :return: NEFEnsemble containing Neurons generated with the default NeuronFactory

make
^^^^

.. java:method:: public NEFEnsemble make(String name, int n, float[] radii) throws StructuralException
   :outertype: NEFEnsembleFactory

   :param name: Name of the NEFEnsemble
   :param n: Number of neurons in the ensemble
   :param radii: Radius of encoded region in each dimension
   :throws StructuralException: if there is any error attempting to create the ensemble
   :return: NEFEnsemble containing Neurons generated with the default NeuronFactory

make
^^^^

.. java:method:: public NEFEnsemble make(String name, int n, float[] radii, String storageName, boolean overwrite) throws StructuralException
   :outertype: NEFEnsembleFactory

   Loads an NEFEnsemble, or creates and saves it.

   :param name: Name of the NEFEnsemble
   :param n: Number of neurons in the ensemble
   :param radii: Radius of encoded region in each dimension.
   :param storageName: Name for storage (eg filename, db key; may have to be more fully qualified than name param, if ensembles belonging to multiple networks are stored in the same place)
   :param overwrite: If false, loads the ensemble if it can be found in storage. If true, creates a new ensemble regardless and overwrites any existing ensemble.
   :throws StructuralException: if there is any error attempting to create the ensemble
   :return: Either new NEFEnsemble generated according to specs and with default NeuronFactory, or a previously-created ensemble loaded from storage

make
^^^^

.. java:method:: public NEFEnsemble make(String name, int n, int dim, String storageName, boolean overwrite) throws StructuralException
   :outertype: NEFEnsembleFactory

   Loads an NEFEnsemble, or creates and saves it.

   :param name: Name of the NEFEnsemble
   :param n: Number of neurons in the ensemble
   :param dim: Dimension of the ensemble.
   :param storageName: Name for storage (eg filename, db key; may have to be more fully qualified than name param, if ensembles belonging to multiple networks are stored in the same place)
   :param overwrite: If false, loads the ensemble if it can be found in storage. If true, creates a new ensemble regardless and overwrites any existing ensemble.
   :throws StructuralException: if there is any error attempting to create the ensemble
   :return: Either new NEFEnsemble generated according to specs and with default NeuronFactory, or a previously-created ensemble loaded from storage

setApproximatorFactory
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setApproximatorFactory(ApproximatorFactory factory)
   :outertype: NEFEnsembleFactory

   :param factory: A factory for creating the LinearApproximators used in decoding ensemble output

setEncoderFactory
^^^^^^^^^^^^^^^^^

.. java:method:: public void setEncoderFactory(VectorGenerator factory)
   :outertype: NEFEnsembleFactory

   :param factory: A VectorGenerator to be used to create encoding vectors that are associated with each Node in a new Ensemble

setEvalPointFactory
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setEvalPointFactory(VectorGenerator factory)
   :outertype: NEFEnsembleFactory

   :param factory: A VectorGenerator to be used to generate the vector states at which decoding functions are evaluated

setNodeFactory
^^^^^^^^^^^^^^

.. java:method:: public void setNodeFactory(NodeFactory factory)
   :outertype: NEFEnsembleFactory

   :param factory: NodeFactory to be used to create Nodes that make up new Ensembles


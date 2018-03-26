.. java:import:: java.io File

.. java:import:: java.io IOException

.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.io FileManager

.. java:import:: ca.nengo.math ApproximatorFactory

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math.impl IdentityFunction

.. java:import:: ca.nengo.math.impl IndicatorPDF

.. java:import:: ca.nengo.math.impl WeightedCostApproximator

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model.impl NodeFactory

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef NEFEnsembleFactory

.. java:import:: ca.nengo.model.nef NEFNode

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.model.neuron.impl LIFNeuronFactory

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util VectorGenerator

.. java:import:: ca.nengo.util VisiblyMutableUtils

.. java:import:: ca.nengo.util.impl RandomHypersphereVG

NEFEnsembleFactoryImpl
======================

.. java:package:: ca.nengo.model.nef.impl
   :noindex:

.. java:type:: public class NEFEnsembleFactoryImpl implements NEFEnsembleFactory, java.io.Serializable

   Default implementation of NEFEnsembleFactory.

   :author: Bryan Tripp

Constructors
------------
NEFEnsembleFactoryImpl
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public NEFEnsembleFactoryImpl()
   :outertype: NEFEnsembleFactoryImpl

   Default constructor. Sets up factories.

Methods
-------
addDefaultOrigins
^^^^^^^^^^^^^^^^^

.. java:method:: protected void addDefaultOrigins(NEFEnsemble ensemble) throws StructuralException
   :outertype: NEFEnsembleFactoryImpl

   Adds standard decoded Origins to the given NEFEnsemble This method is exposed so that it can be over-ridden to change behaviour.

   :param ensemble: A new NEFEnsemble
   :throws StructuralException:

beQuiet
^^^^^^^

.. java:method:: public void beQuiet()
   :outertype: NEFEnsembleFactoryImpl

   Stops the factory from printing out information to console during make process.

construct
^^^^^^^^^

.. java:method:: protected NEFEnsemble construct(String name, NEFNode[] nodes, float[][] encoders, ApproximatorFactory af, float[][] evalPoints, float[] radii) throws StructuralException
   :outertype: NEFEnsembleFactoryImpl

   This method is exposed so that it can be over-ridden to change behaviour.

   :param name: Name of new Ensemble
   :param nodes: Nodes that make up Ensemble
   :param encoders: Encoding vector for each Node
   :param af: Factory that produces LinearApproximators for decoding Ensemble output
   :param evalPoints: States at which Node output is evaluated for decoding purposes
   :param radii: Radius of encoded area in each dimension
   :throws StructuralException:
   :return: New NEFEnsemble with given parameters

getApproximatorFactory
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public ApproximatorFactory getApproximatorFactory()
   :outertype: NEFEnsembleFactoryImpl

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsembleFactory.getApproximatorFactory()`

getDatabase
^^^^^^^^^^^

.. java:method:: public File getDatabase()
   :outertype: NEFEnsembleFactoryImpl

   :return: Directory for saving / loading ensembles

getEncoderFactory
^^^^^^^^^^^^^^^^^

.. java:method:: public VectorGenerator getEncoderFactory()
   :outertype: NEFEnsembleFactoryImpl

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsembleFactory.getEncoderFactory()`

getEvalPointFactory
^^^^^^^^^^^^^^^^^^^

.. java:method:: public VectorGenerator getEvalPointFactory()
   :outertype: NEFEnsembleFactoryImpl

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsembleFactory.getEvalPointFactory()`

getNodeFactory
^^^^^^^^^^^^^^

.. java:method:: public NodeFactory getNodeFactory()
   :outertype: NEFEnsembleFactoryImpl

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsembleFactory.getNodeFactory()`

getNumEvalPoints
^^^^^^^^^^^^^^^^

.. java:method:: protected int getNumEvalPoints(int dim)
   :outertype: NEFEnsembleFactoryImpl

   This method is exposed so that it can be over-ridden to change behaviour.

   :param dim: the dimension of the state represented by an Ensemble
   :return: The number of points at which to approximate decoded functions

make
^^^^

.. java:method:: public NEFEnsemble make(String name, int n, int dim) throws StructuralException
   :outertype: NEFEnsembleFactoryImpl

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsembleFactory.make(java.lang.String,int,int)`

make
^^^^

.. java:method:: public NEFEnsemble make(String name, int n, float[] radii) throws StructuralException
   :outertype: NEFEnsembleFactoryImpl

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsembleFactory.make(java.lang.String,int,float[])`

make
^^^^

.. java:method:: public NEFEnsemble make(String name, int n, int dim, String storageName, boolean overwrite) throws StructuralException
   :outertype: NEFEnsembleFactoryImpl

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsembleFactory.make(java.lang.String,int,int,java.lang.String,boolean)`

make
^^^^

.. java:method:: public NEFEnsemble make(String name, int n, float[] radii, String storageName, boolean overwrite) throws StructuralException
   :outertype: NEFEnsembleFactoryImpl

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsembleFactory.make(java.lang.String,int,int,java.lang.String,boolean)`

setApproximatorFactory
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setApproximatorFactory(ApproximatorFactory factory)
   :outertype: NEFEnsembleFactoryImpl

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsembleFactory.setApproximatorFactory(ca.nengo.math.ApproximatorFactory)`

setDatabase
^^^^^^^^^^^

.. java:method:: public void setDatabase(File database)
   :outertype: NEFEnsembleFactoryImpl

   :param database: New directory for saving / loading ensembles

setEncoderFactory
^^^^^^^^^^^^^^^^^

.. java:method:: public void setEncoderFactory(VectorGenerator factory)
   :outertype: NEFEnsembleFactoryImpl

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsembleFactory.setEncoderFactory(ca.nengo.util.VectorGenerator)`

setEvalPointFactory
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setEvalPointFactory(VectorGenerator factory)
   :outertype: NEFEnsembleFactoryImpl

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsembleFactory.setEvalPointFactory(ca.nengo.util.VectorGenerator)`

setNodeFactory
^^^^^^^^^^^^^^

.. java:method:: public void setNodeFactory(NodeFactory factory)
   :outertype: NEFEnsembleFactoryImpl

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsembleFactory.setNodeFactory(ca.nengo.model.impl.NodeFactory)`


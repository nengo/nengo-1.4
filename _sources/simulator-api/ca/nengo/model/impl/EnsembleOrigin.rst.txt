.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model PreciseSpikeOutput

.. java:import:: ca.nengo.model RealOutput

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SpikeOutput

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

EnsembleOrigin
==============

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class EnsembleOrigin implements Origin

   An Origin that is composed of the Origins of multiple Nodes. The dimension of this Origin equals the number of Nodes. All the Nodes must produce the same type of output (RealOutput or SpikeOutput) with the same Unit at the same time (these things can change in subsequent time steps, but they must change together for all Nodes).

   :author: Bryan Tripp

Constructors
------------
EnsembleOrigin
^^^^^^^^^^^^^^

.. java:constructor:: public EnsembleOrigin(Node node, String name, Origin[] nodeOrigins)
   :outertype: EnsembleOrigin

   :param node: The parent Node
   :param name: Name of this Origin
   :param nodeOrigins: Origins on individual Nodes that are combined to make this Origin. Each of these is expected to have dimension 1, but this is not enforced. Other dimensions are ignored.

Methods
-------
clone
^^^^^

.. java:method:: @Override public EnsembleOrigin clone() throws CloneNotSupportedException
   :outertype: EnsembleOrigin

   Note: the clone references the same copies of the underlying node origins. This will work if the intent is to duplicate an EnsembleOrigin on the same Ensemble. More work is needed if this clone is part of an Ensemble clone, since the cloned EnsembleOrigin should then reference the new node origins, which we don't have access to here.

clone
^^^^^

.. java:method:: public EnsembleOrigin clone(Node node) throws CloneNotSupportedException
   :outertype: EnsembleOrigin

getDimensions
^^^^^^^^^^^^^

.. java:method:: public int getDimensions()
   :outertype: EnsembleOrigin

   **See also:** :java:ref:`ca.nengo.model.Origin.getDimensions()`

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: EnsembleOrigin

   **See also:** :java:ref:`ca.nengo.model.Origin.getName()`

getNode
^^^^^^^

.. java:method:: public Node getNode()
   :outertype: EnsembleOrigin

   **See also:** :java:ref:`ca.nengo.model.Origin.getNode()`

getNodeOrigins
^^^^^^^^^^^^^^

.. java:method:: public Origin[] getNodeOrigins()
   :outertype: EnsembleOrigin

   :return: Array with all of the underlying node origins

getRequiredOnCPU
^^^^^^^^^^^^^^^^

.. java:method:: public boolean getRequiredOnCPU()
   :outertype: EnsembleOrigin

getValues
^^^^^^^^^

.. java:method:: public InstantaneousOutput getValues() throws SimulationException
   :outertype: EnsembleOrigin

   :return: A composite of the first-dimensional outputs of all the Node Origins that make up the EnsembleOrigin. Node Origins should normally have dimension 1, but this isn't enforced here. All Node Origins must have the same units, and must output the same type of InstantaneousOuput (ie either SpikeOutput or RealOutput), otherwise an exception is thrown.

   **See also:** :java:ref:`ca.nengo.model.Origin.getValues()`

setRequiredOnCPU
^^^^^^^^^^^^^^^^

.. java:method:: public void setRequiredOnCPU(boolean val)
   :outertype: EnsembleOrigin

setValues
^^^^^^^^^

.. java:method:: public void setValues(InstantaneousOutput values)
   :outertype: EnsembleOrigin


.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

ProbeableOrigin
===============

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class ProbeableOrigin implements Origin

   An Origin that obtains output from an underlying Probeable object.

   As an example of use, suppose a Neuron has a SynapticIntegrator with a complex dendritic morphology, and that it is desired to model a gap junction between one of these dendrites and a dendrite on another Neuron. If the SynapticIntegrator can provide gap-junctional Origins, there is no problem. But it might not (for example the implementor of the SynapticIntegrator may not have anticipated this usage). However, if the SynapticIntegrator is Probeable and can be probed for the appropriate state variables, eg ion concentrations in the compartment of interest, then this class (ProbeableOrigin) provides a convenient way to model an Origin that outputs the probed information.

   For a Neuron, if multi-dimensional state is to be output, it is generally better to create multiple one-dimensional Outputs than to creat one multi-dimensional Output. Reasons for this include the following:

   ..

   * As with all Origins, all the output values at a given instant have the same units. So, if you want to output states with different units, you must use separate Origins.
   * Ensembles may combine identically-named Ouputs of different Neurons into a single Ensemble-level Output (with the same dimension as the number of Neurons that have that Output). This doesn't work well with multi-dimensional Neuron Outputs. So, if your Neurons will be grouped into an Ensemble, it's better to stick with 1-D Outputs. The other option (which seems more convoluted) is to make sure that each Neuron's n-D Output has a distinct name (ie distinct from the names of the correspoding Outputs of other Neurons in the same Ensemble). Incorporating a number into the name is one way to do this.

   :author: Bryan Tripp

Constructors
------------
ProbeableOrigin
^^^^^^^^^^^^^^^

.. java:constructor:: public ProbeableOrigin(Node node, Probeable probeable, String state, int dimension, String name) throws StructuralException
   :outertype: ProbeableOrigin

   :param node: The parent node
   :param probeable: The Probeable from which to obtain state variables to output
   :param state: State variable to output
   :param dimension: Index of the dimension of the specified state variable that is to be output
   :param name: Name of this Origin
   :throws StructuralException: if there is a problem running an initial Probeable.getHistory() to ascertain the units.

Methods
-------
clone
^^^^^

.. java:method:: @Override public ProbeableOrigin clone() throws CloneNotSupportedException
   :outertype: ProbeableOrigin

clone
^^^^^

.. java:method:: public ProbeableOrigin clone(Node node) throws CloneNotSupportedException
   :outertype: ProbeableOrigin

getDimensions
^^^^^^^^^^^^^

.. java:method:: public int getDimensions()
   :outertype: ProbeableOrigin

   :return: 1

   **See also:** :java:ref:`ca.nengo.model.Origin.getDimensions()`

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: ProbeableOrigin

   **See also:** :java:ref:`ca.nengo.model.Origin.getName()`

getNode
^^^^^^^

.. java:method:: public Node getNode()
   :outertype: ProbeableOrigin

   **See also:** :java:ref:`ca.nengo.model.Origin.getNode()`

getRequiredOnCPU
^^^^^^^^^^^^^^^^

.. java:method:: public boolean getRequiredOnCPU()
   :outertype: ProbeableOrigin

getValues
^^^^^^^^^

.. java:method:: public InstantaneousOutput getValues() throws SimulationException
   :outertype: ProbeableOrigin

   :return: The final value in the TimeSeries for the state variable that is retrieved from the underlying Probeable

setRequiredOnCPU
^^^^^^^^^^^^^^^^

.. java:method:: public void setRequiredOnCPU(boolean val)
   :outertype: ProbeableOrigin

setValues
^^^^^^^^^

.. java:method:: public void setValues(InstantaneousOutput values)
   :outertype: ProbeableOrigin


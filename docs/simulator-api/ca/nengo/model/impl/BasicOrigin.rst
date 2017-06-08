.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.config ConfigUtil

.. java:import:: ca.nengo.config Configurable

.. java:import:: ca.nengo.config Configuration

.. java:import:: ca.nengo.config Property

.. java:import:: ca.nengo.config.impl ConfigurationImpl

.. java:import:: ca.nengo.config.impl SingleValuedPropertyImpl

.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Noise

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model Resettable

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model Units

BasicOrigin
===========

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class BasicOrigin implements Origin, Noise.Noisy, Resettable, Configurable

   A generic implementation of Origin. Nodes that contain an Origin of this type should call one of the setValues() methods with every Node.run(...).

   :author: Bryan Tripp

Constructors
------------
BasicOrigin
^^^^^^^^^^^

.. java:constructor:: public BasicOrigin()
   :outertype: BasicOrigin

   Dummy default, necessary for object "ArrayOrigin" in jython code TODO: Still necessary?

BasicOrigin
^^^^^^^^^^^

.. java:constructor:: public BasicOrigin(Node node, String name, int dimension, Units units)
   :outertype: BasicOrigin

   :param node: The parent Node
   :param name: Name of origin
   :param dimension: Dimension of output of this Origin
   :param units: The output units

Methods
-------
clone
^^^^^

.. java:method:: @Override public BasicOrigin clone() throws CloneNotSupportedException
   :outertype: BasicOrigin

clone
^^^^^

.. java:method:: public BasicOrigin clone(Node node) throws CloneNotSupportedException
   :outertype: BasicOrigin

getConfiguration
^^^^^^^^^^^^^^^^

.. java:method:: public Configuration getConfiguration()
   :outertype: BasicOrigin

   **See also:** :java:ref:`ca.nengo.config.Configurable.getConfiguration()`

getDimensions
^^^^^^^^^^^^^

.. java:method:: public int getDimensions()
   :outertype: BasicOrigin

   **See also:** :java:ref:`ca.nengo.model.Origin.getDimensions()`

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: BasicOrigin

   **See also:** :java:ref:`ca.nengo.model.Origin.getName()`

getNode
^^^^^^^

.. java:method:: public Node getNode()
   :outertype: BasicOrigin

   **See also:** :java:ref:`ca.nengo.model.Origin.getNode()`

getNoise
^^^^^^^^

.. java:method:: public Noise getNoise()
   :outertype: BasicOrigin

   **See also:** :java:ref:`ca.nengo.model.Noise.Noisy.getNoise()`

getRequiredOnCPU
^^^^^^^^^^^^^^^^

.. java:method:: public boolean getRequiredOnCPU()
   :outertype: BasicOrigin

getUnits
^^^^^^^^

.. java:method:: public Units getUnits()
   :outertype: BasicOrigin

   :return: Units used by this origin

getValues
^^^^^^^^^

.. java:method:: public InstantaneousOutput getValues() throws SimulationException
   :outertype: BasicOrigin

   **See also:** :java:ref:`ca.nengo.model.Origin.getValues()`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: BasicOrigin

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

setDimensions
^^^^^^^^^^^^^

.. java:method:: public void setDimensions(int dim)
   :outertype: BasicOrigin

   :param dim: Origin dimensionality

setName
^^^^^^^

.. java:method:: public void setName(String name)
   :outertype: BasicOrigin

   :param name: Origin name

setNoise
^^^^^^^^

.. java:method:: public void setNoise(Noise noise)
   :outertype: BasicOrigin

   Note that noise is only applied to RealOutput.

   **See also:** :java:ref:`ca.nengo.model.Noise.Noisy.setNoise(ca.nengo.model.Noise)`

setRequiredOnCPU
^^^^^^^^^^^^^^^^

.. java:method:: public void setRequiredOnCPU(boolean val)
   :outertype: BasicOrigin

setUnits
^^^^^^^^

.. java:method:: public void setUnits(Units units)
   :outertype: BasicOrigin

   :param units: Units used by this origin

setValues
^^^^^^^^^

.. java:method:: public void setValues(float startTime, float endTime, float[] values)
   :outertype: BasicOrigin

   This method is normally called by the Node that contains this Origin, to set the input that is read by other nodes from getValues(). If the Noise model has been set, noise is applied to the given values.

   :param startTime: Start time of step for which outputs are being defined
   :param endTime: End time of step for which outputs are being defined
   :param values: Values underlying RealOutput that is to be output by this Origin in subsequent calls to getValues()

setValues
^^^^^^^^^

.. java:method:: public void setValues(InstantaneousOutput values)
   :outertype: BasicOrigin

   This method is normally called by the Node that contains this Origin, to set the input that is read by other nodes from getValues(). No noise is applied to the given values.

   :param values: Values to be output by this Origin in subsequent calls to getValues()


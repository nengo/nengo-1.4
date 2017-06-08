.. java:import:: java.io Serializable

InstantaneousOutput
===================

.. java:package:: ca.nengo.model
   :noindex:

.. java:type:: public interface InstantaneousOutput extends Serializable, Cloneable

   An output from an Origin at an instant in time. This is the medium we use to pass information around a neural circuit.

   Note that an Ensemble or Neuron may have multiple Origins and can therefore produce multiple outputs simultaneously. For example, one Origin of an Ensemble might produce spiking outputs, another the decoded estimates of variables it represents, and others decoded functions of these variables.

   Note that the methods for getting output values from an InstantaneousOuput are not defined here, but on subinterfaces.

   :author: Bryan Tripp

Methods
-------
clone
^^^^^

.. java:method:: public InstantaneousOutput clone() throws CloneNotSupportedException
   :outertype: InstantaneousOutput

   :throws CloneNotSupportedException: if clone can't be made
   :return: Valid clone

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: InstantaneousOutput

   :return: Dimension of output

getTime
^^^^^^^

.. java:method:: public float getTime()
   :outertype: InstantaneousOutput

   :return: Time at which output is produced.

getUnits
^^^^^^^^

.. java:method:: public Units getUnits()
   :outertype: InstantaneousOutput

   :return: Units in which output is expressed.


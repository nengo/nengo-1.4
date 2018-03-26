.. java:import:: ca.nengo.model SpikeOutput

.. java:import:: ca.nengo.model Units

SpikeOutputImpl
===============

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class SpikeOutputImpl implements SpikeOutput

   Default implementation of SpikeOutput.

   :author: Bryan Tripp

Constructors
------------
SpikeOutputImpl
^^^^^^^^^^^^^^^

.. java:constructor:: public SpikeOutputImpl(boolean[] values, Units units, float time)
   :outertype: SpikeOutputImpl

   :param values: @see #getValues()
   :param units: @see #getUnits()
   :param time: @see #getTime()

Methods
-------
clone
^^^^^

.. java:method:: @Override public SpikeOutput clone() throws CloneNotSupportedException
   :outertype: SpikeOutputImpl

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: SpikeOutputImpl

   **See also:** :java:ref:`ca.nengo.model.InstantaneousOutput.getDimension()`

getTime
^^^^^^^

.. java:method:: public float getTime()
   :outertype: SpikeOutputImpl

   **See also:** :java:ref:`ca.nengo.model.InstantaneousOutput.getTime()`

getUnits
^^^^^^^^

.. java:method:: public Units getUnits()
   :outertype: SpikeOutputImpl

   **See also:** :java:ref:`ca.nengo.model.InstantaneousOutput.getUnits()`

getValues
^^^^^^^^^

.. java:method:: public boolean[] getValues()
   :outertype: SpikeOutputImpl

   **See also:** :java:ref:`ca.nengo.model.SpikeOutput.getValues()`


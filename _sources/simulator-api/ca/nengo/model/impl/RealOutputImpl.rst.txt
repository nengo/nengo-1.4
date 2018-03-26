.. java:import:: ca.nengo.model RealOutput

.. java:import:: ca.nengo.model Units

RealOutputImpl
==============

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class RealOutputImpl implements RealOutput

   Default implementation of RealOutput.

   :author: Bryan Tripp

Constructors
------------
RealOutputImpl
^^^^^^^^^^^^^^

.. java:constructor:: public RealOutputImpl(float[] values, Units units, float time)
   :outertype: RealOutputImpl

   :param values: @see #getValues()
   :param units: @see #getUnits()
   :param time: @see #getTime()

Methods
-------
clone
^^^^^

.. java:method:: @Override public RealOutput clone() throws CloneNotSupportedException
   :outertype: RealOutputImpl

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: RealOutputImpl

   **See also:** :java:ref:`ca.nengo.model.InstantaneousOutput.getDimension()`

getTime
^^^^^^^

.. java:method:: public float getTime()
   :outertype: RealOutputImpl

   **See also:** :java:ref:`ca.nengo.model.InstantaneousOutput.getTime()`

getUnits
^^^^^^^^

.. java:method:: public Units getUnits()
   :outertype: RealOutputImpl

   **See also:** :java:ref:`ca.nengo.model.InstantaneousOutput.getUnits()`

getValues
^^^^^^^^^

.. java:method:: public float[] getValues()
   :outertype: RealOutputImpl

   **See also:** :java:ref:`ca.nengo.model.RealOutput.getValues()`


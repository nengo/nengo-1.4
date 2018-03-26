.. java:import:: ca.nengo.math Function

SineFunction
============

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class SineFunction implements Function

   Function wrapper for sin(omega x), where x is in radians and omega is the angular frequency. TODO: test

   :author: Bryan Tripp

Constructors
------------
SineFunction
^^^^^^^^^^^^

.. java:constructor:: public SineFunction()
   :outertype: SineFunction

   Uses default angular frequency of 2pi and amplitude of 1

SineFunction
^^^^^^^^^^^^

.. java:constructor:: public SineFunction(float omega)
   :outertype: SineFunction

   Uses default amplitude of 1.

   :param omega: Angular frequency

SineFunction
^^^^^^^^^^^^

.. java:constructor:: public SineFunction(float omega, float amplitude)
   :outertype: SineFunction

   :param omega: Angular frequency
   :param amplitude: Amplitude (peak value)

Methods
-------
clone
^^^^^

.. java:method:: @Override public Function clone() throws CloneNotSupportedException
   :outertype: SineFunction

getAmplitude
^^^^^^^^^^^^

.. java:method:: public float getAmplitude()
   :outertype: SineFunction

   :return: Amplitude (peak value)

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: SineFunction

   :return: 1

   **See also:** :java:ref:`ca.nengo.math.Function.getDimension()`

getOmega
^^^^^^^^

.. java:method:: public float getOmega()
   :outertype: SineFunction

   :return: Angular frequency

map
^^^

.. java:method:: public float map(float[] from)
   :outertype: SineFunction

   **See also:** :java:ref:`ca.nengo.math.Function.map(float[])`

multiMap
^^^^^^^^

.. java:method:: public float[] multiMap(float[][] from)
   :outertype: SineFunction

   **See also:** :java:ref:`ca.nengo.math.Function.multiMap(float[][])`

setAmplitude
^^^^^^^^^^^^

.. java:method:: public void setAmplitude(float amplitude)
   :outertype: SineFunction

   :param amplitude: Amplitude (peak value)

setOmega
^^^^^^^^

.. java:method:: public void setOmega(float omega)
   :outertype: SineFunction

   :param omega: Angular frequency


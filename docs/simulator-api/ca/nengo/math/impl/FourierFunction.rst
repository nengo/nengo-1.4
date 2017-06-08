.. java:import:: java.util Random

.. java:import:: ca.nengo.math Function

FourierFunction
===============

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class FourierFunction implements Function

   A Function that is composed of a finite number of sinusoids.

   :author: Bryan Tripp

Constructors
------------
FourierFunction
^^^^^^^^^^^^^^^

.. java:constructor:: public FourierFunction(float[] frequencies, float[] amplitudes, float[] phases)
   :outertype: FourierFunction

   Creates a 1-dimensional function composed of explicitly defined sinusoids.

   :param frequencies: Explicit list of frequencies of sinusoidal components of the function (Hz)
   :param amplitudes: The amplitude of each component
   :param phases: The phase lead of each component (from -.5 to .5)

FourierFunction
^^^^^^^^^^^^^^^

.. java:constructor:: public FourierFunction(float[][] frequencies, float[] amplitudes, float[][] phases)
   :outertype: FourierFunction

   Creates an n-dimensional function composed of explicitly defined sinusoids.

   :param frequencies: Lists of frequencies (length n; ith members define frequencies of ith component along each dimension)
   :param amplitudes: The amplitude of each component
   :param phases: Lists of phases (length n; ith members define phases of ith component along each dimension)

FourierFunction
^^^^^^^^^^^^^^^

.. java:constructor:: public FourierFunction(float fundamental, float cutoff, float rms, long seed, int type)
   :outertype: FourierFunction

   Creates a 1-dimensional band-limited noise function with specified parameters.

   :param fundamental: The fundamental frequency (Hz), i.e., frequency step size.
   :param cutoff: The high-frequency limit (Hz)
   :param rms: The root-mean-squared function amplitude
   :param seed: Random seed
   :param type: The type of noise: 0 = white; 1 = pink;

FourierFunction
^^^^^^^^^^^^^^^

.. java:constructor:: public FourierFunction(float fundamental, float cutoff, float rms, long seed)
   :outertype: FourierFunction

   Creates a 1-dimensional band-limited pink noise function with specified parameters.

   :param fundamental: The fundamental frequency (Hz), i.e., frequency step size.
   :param cutoff: The high-frequency limit (Hz)
   :param rms: The root-mean-squared function amplitude
   :param seed: Random seed

Methods
-------
clone
^^^^^

.. java:method:: @Override public Function clone() throws CloneNotSupportedException
   :outertype: FourierFunction

getAmplitudes
^^^^^^^^^^^^^

.. java:method:: public float[] getAmplitudes()
   :outertype: FourierFunction

   :return: The amplitude of each component

getComponents
^^^^^^^^^^^^^

.. java:method:: public int getComponents()
   :outertype: FourierFunction

   :return: Number of frequency components

getCutoff
^^^^^^^^^

.. java:method:: public float getCutoff()
   :outertype: FourierFunction

   :return: The cutoff frequency used to generate the function if it was provided.

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: FourierFunction

   **See also:** :java:ref:`ca.nengo.math.Function.getDimension()`

getFrequencies
^^^^^^^^^^^^^^

.. java:method:: public float[][] getFrequencies()
   :outertype: FourierFunction

   :return: Lists of frequencies (length n; ith members define frequencies of ith component along each dimension)

getFundamental
^^^^^^^^^^^^^^

.. java:method:: public float getFundamental()
   :outertype: FourierFunction

   :return: The fundamental frequency used to generate the function if it was provided.

getPhases
^^^^^^^^^

.. java:method:: public float[][] getPhases()
   :outertype: FourierFunction

   :return: Lists of phases (length n; ith members define phases of ith component along each dimension)

getRms
^^^^^^

.. java:method:: public float getRms()
   :outertype: FourierFunction

   :return: The rms amplitude used to generate the function if it was provided.

getSeed
^^^^^^^

.. java:method:: public long getSeed()
   :outertype: FourierFunction

   :return: The seed used to generate the function if it was provided.

map
^^^

.. java:method:: public float map(float[] from)
   :outertype: FourierFunction

   **See also:** :java:ref:`ca.nengo.math.Function.map(float[])`

multiMap
^^^^^^^^

.. java:method:: public float[] multiMap(float[][] from)
   :outertype: FourierFunction

   **See also:** :java:ref:`ca.nengo.math.Function.multiMap(float[][])`

setAmplitudes
^^^^^^^^^^^^^

.. java:method:: public void setAmplitudes(float[] amplitudes)
   :outertype: FourierFunction

   :param amplitudes: The amplitude of each component

setFrequencies
^^^^^^^^^^^^^^

.. java:method:: public void setFrequencies(float[][] frequencies)
   :outertype: FourierFunction

   :param frequencies: Lists of frequencies (length n; ith members define frequencies of ith component along each dimension)

setPhases
^^^^^^^^^

.. java:method:: public void setPhases(float[][] phases)
   :outertype: FourierFunction

   :param phases: Lists of phases (length n; ith members define phases of ith component along each dimension)


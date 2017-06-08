.. java:import:: java.io Serializable

Noise.Noisy
===========

.. java:package:: ca.nengo.model
   :noindex:

.. java:type:: public interface Noisy
   :outertype: Noise

   An object that implements this interface is subject to Noise.

   :author: Bryan Tripp

Methods
-------
getNoise
^^^^^^^^

.. java:method:: public Noise getNoise()
   :outertype: Noise.Noisy

   :return: Noise with which the object is to be corrupted

setNoise
^^^^^^^^

.. java:method:: public void setNoise(Noise noise)
   :outertype: Noise.Noisy

   :param noise: New noise model


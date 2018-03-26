.. java:import:: umontreal.iro.lecuyer.probdist NormalDist

SimpleFunctions.Fold
====================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public static class Fold extends AbstractFunction
   :outertype: SimpleFunctions

   Computes the image of x0 \in \mathbb{R} in the quotient group \mathbb{R}/\mathbb{Z}. The representative is in (-1/2,1/2].

   :author: Lloyd Elliott

Constructors
------------
Fold
^^^^

.. java:constructor:: public Fold()
   :outertype: SimpleFunctions.Fold

   Wrapper around (x0 - Math.ceil(x0-0.5))

Methods
-------
map
^^^

.. java:method:: @Override public float map(float[] from)
   :outertype: SimpleFunctions.Fold


.. java:import:: ca.nengo.math Function

Polynomial
==========

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class Polynomial extends AbstractFunction implements Function

   A one-dimensional polynomial Function. It is defined by a series of coefficients that must be given in the constructor.

   :author: Bryan Tripp

Constructors
------------
Polynomial
^^^^^^^^^^

.. java:constructor:: public Polynomial(float[] coefficients)
   :outertype: Polynomial

   :param coefficients: Coefficients [a0 a1 a2 ...] in polynomial y = a0 + a1x + a2x^2 + ...

Methods
-------
clone
^^^^^

.. java:method:: @Override public Function clone() throws CloneNotSupportedException
   :outertype: Polynomial

getCoefficients
^^^^^^^^^^^^^^^

.. java:method:: public float[] getCoefficients()
   :outertype: Polynomial

   :return: Coefficients [a0 a1 a2 ...] in polynomial y = a0 + a1x + a2x^2 + ...

getOrder
^^^^^^^^

.. java:method:: public int getOrder()
   :outertype: Polynomial

   :return: Polynomial order

map
^^^

.. java:method:: public float map(float[] from)
   :outertype: Polynomial

   **See also:** :java:ref:`ca.nengo.math.Function.map(float[])`

setCoefficients
^^^^^^^^^^^^^^^

.. java:method:: public void setCoefficients(float[] coefficients)
   :outertype: Polynomial

   :param coefficients: Coefficients [a0 a1 a2 ...] in polynomial y = a0 + a1x + a2x^2 + ...

setOrder
^^^^^^^^

.. java:method:: public void setOrder(int order)
   :outertype: Polynomial

   :param order: Polynomial order


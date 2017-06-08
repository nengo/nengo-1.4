.. java:import:: Jama Matrix

.. java:import:: ca.nengo.math CurveFitter

.. java:import:: ca.nengo.math Function

PolynomialCurveFitter
=====================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class PolynomialCurveFitter implements CurveFitter

   A least-squares polynomial CurveFitter.

   See http://mathworld.wolfram.com/LeastSquaresFittingPolynomial.html

   TODO: write proper tests

   :author: Bryan Tripp

Constructors
------------
PolynomialCurveFitter
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PolynomialCurveFitter(int order)
   :outertype: PolynomialCurveFitter

   :param order: Order of polynomials used to approximate example points

Methods
-------
clone
^^^^^

.. java:method:: @Override public CurveFitter clone() throws CloneNotSupportedException
   :outertype: PolynomialCurveFitter

fit
^^^

.. java:method:: public Function fit(float[] x, float[] y)
   :outertype: PolynomialCurveFitter

   **See also:** :java:ref:`ca.nengo.math.CurveFitter.fit(float[],float[])`

getOrder
^^^^^^^^

.. java:method:: public int getOrder()
   :outertype: PolynomialCurveFitter

   :return: Order of polynomials used to approximate points (eg 1 corresponds to linear approximation, 2 to quadratic, etc)

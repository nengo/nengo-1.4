CurveFitter
===========

.. java:package:: ca.nengo.math
   :noindex:

.. java:type:: public interface CurveFitter extends Cloneable

   Finds a Function that fits a set of a example points in some sense (e.g. least-squares). For example, least-squares polynomial approximation and spline interpolation are possibly implementations.

   :author: Bryan Tripp

Methods
-------
clone
^^^^^

.. java:method:: public CurveFitter clone() throws CloneNotSupportedException
   :outertype: CurveFitter

   :throws CloneNotSupportedException: is clone can't be made
   :return: Valid clone

fit
^^^

.. java:method:: public Function fit(float[] x, float[] y)
   :outertype: CurveFitter

   :param x: Example x points
   :param y: Example y points (must be same length as x)
   :return: A Function that approximates the mapping Y=f(X) exemplified by x and y.


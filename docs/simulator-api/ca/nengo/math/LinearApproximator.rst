.. java:import:: java.io Serializable

LinearApproximator
==================

.. java:package:: ca.nengo.math
   :noindex:

.. java:type:: public interface LinearApproximator extends Serializable, Cloneable

   Finds coefficients on a set of functions so that their linear combination approximates a target Function. In other words, finds a_i so that sum(a_i * f_i(x)) roughly equals t(x) in some sense, where f_i are the functions to be combined and t is a target function, both over some range of the variable x.

   Can be used to find decoding vectors and synaptic weights.

   :author: Bryan Tripp

Methods
-------
clone
^^^^^

.. java:method:: public LinearApproximator clone() throws CloneNotSupportedException
   :outertype: LinearApproximator

   :throws CloneNotSupportedException: if clone can't be made
   :return: Valid clone

findCoefficients
^^^^^^^^^^^^^^^^

.. java:method:: public float[] findCoefficients(Function target)
   :outertype: LinearApproximator

   Note: more information is needed than the arguments provide (for example the functions that are to be combined to estimate the target). These other data are object properties. This enables re-use of calculations based on these data, for estimating multiple functions.

   :param target: Function to approximate
   :return: coefficients on component functions which result in an approximation of the target

getEvalPoints
^^^^^^^^^^^^^

.. java:method:: public float[][] getEvalPoints()
   :outertype: LinearApproximator

   :return: Points at which target functions are evaluated. Each row (or float[]) corresponds to a single evaluation point. These points should usually be uniformly distributed, because the sum of error at these points is treated as an integral over the domain of interest.

getValues
^^^^^^^^^

.. java:method:: public float[][] getValues()
   :outertype: LinearApproximator

   :return: The values of component functions at the evaluation points. The first dimension makes up the list of functions, and the second the values of these functions at each evaluation point.


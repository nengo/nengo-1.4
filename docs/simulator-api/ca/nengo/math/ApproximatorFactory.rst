.. java:import:: java.io Serializable

ApproximatorFactory
===================

.. java:package:: ca.nengo.math
   :noindex:

.. java:type:: public interface ApproximatorFactory extends Serializable, Cloneable

   Produces LinearApproximators, which approximate Functions through a weighted sum of component functions. The component functions are given as lists of evaluation points and corresponding values.

   :author: Bryan Tripp

Methods
-------
clone
^^^^^

.. java:method:: public ApproximatorFactory clone() throws CloneNotSupportedException
   :outertype: ApproximatorFactory

   :throws CloneNotSupportedException: if clone can't be made
   :return: Valid clone

getApproximator
^^^^^^^^^^^^^^^

.. java:method:: public LinearApproximator getApproximator(float[][] evalPoints, float[][] values)
   :outertype: ApproximatorFactory

   :param evalPoints: Points at which component functions are evaluated. These should usually be uniformly distributed, because the sum of error at these points is treated as an integral over the domain of interest.
   :param values: The values of component functions at the evalPoints. The first dimension makes up the list of functions, and the second the values of these functions at each evaluation point.
   :return: A LinearApproximator that can be used to approximate new Functions as a wieghted sum of the given components.


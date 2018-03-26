FunctionBasis
=============

.. java:package:: ca.nengo.math
   :noindex:

.. java:type:: public interface FunctionBasis extends Function

   A list of orthogonal functions.

   Function bases are useful in function representation, because they make function representation equivalent to vector representation (see Eliasmith & Anderson, 2003). Essentially, functions in an orthogonal basis correspond to dimensions in a vector. Cosine tuning curves in a vector space are equivalent to inner-product tuning curves in the corresponding function space.

   Examples of orthogonal sets of functions include Fourier and wavelet bases.

   :author: Bryan Tripp

Methods
-------
getBasisDimension
^^^^^^^^^^^^^^^^^

.. java:method:: public int getBasisDimension()
   :outertype: FunctionBasis

   :return: Dimensionality of basis

getFunction
^^^^^^^^^^^

.. java:method:: public Function getFunction(int basisIndex)
   :outertype: FunctionBasis

   :param basisIndex: Dimension index
   :return: Basis function corresponding to given dimension

setCoefficients
^^^^^^^^^^^^^^^

.. java:method:: public void setCoefficients(float[] coefficients)
   :outertype: FunctionBasis

   :param coefficients: Coefficient for summing basis functions


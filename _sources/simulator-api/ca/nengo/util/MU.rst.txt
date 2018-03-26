.. java:import:: java.text NumberFormat

.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.math PDF

MU
==

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public class MU

   "Matrix Utilities". Utility methods related to matrices and vectors of floats. TODO: test

   :author: Bryan Tripp

Methods
-------
I
^

.. java:method:: public static float[][] I(int dimension)
   :outertype: MU

   :param dimension: # of rows/columns
   :return: Identity matrix of specified dimension

clone
^^^^^

.. java:method:: public static float[][] clone(float[][] matrix)
   :outertype: MU

   :param matrix: Any matrix
   :return: An identical but independent copy of the given matrix

clone
^^^^^

.. java:method:: public static float[][][] clone(float[][][] matrix)
   :outertype: MU

clone
^^^^^

.. java:method:: public static double[][] clone(double[][] matrix)
   :outertype: MU

   :param matrix: Any matrix
   :return: An identical but independent copy of the given matrix

convert
^^^^^^^

.. java:method:: public static double[][] convert(float[][] matrix)
   :outertype: MU

   :param matrix: Any float matrix
   :return: Duplicate with each element cast to double

convert
^^^^^^^

.. java:method:: public static double[] convert(float[] vector)
   :outertype: MU

   :param vector: Any float vector
   :return: Duplicate with each element cast to double

convert
^^^^^^^

.. java:method:: public static float[][] convert(double[][] matrix)
   :outertype: MU

   :param matrix: Any double matrix
   :return: Duplicate with each element cast to float

convert
^^^^^^^

.. java:method:: public static float[] convert(double[] vector)
   :outertype: MU

   :param vector: Any double vector
   :return: Duplicate with each element cast to float

copy
^^^^

.. java:method:: public static float[] copy(float[] vector, int start, int interval, int end)
   :outertype: MU

   :param vector: Vector to copy from
   :param start: Index in vector from which to start copying
   :param interval: Interval separating copied entries in source vector (ie skip over interval-1 entries)
   :param end: Index in vector at which copying ends
   :return: Values copied from source vector

copy
^^^^

.. java:method:: public static float[][] copy(float[][] matrix, int startRow, int startCol, int lengthRow, int lengthCol)
   :outertype: MU

   :param matrix: Matrix to copy from
   :param startRow: Row in matrix from which to start copying
   :param startCol: Col in matrix from which to start copying
   :param lengthRow: Number of rows to copy (set to a negative number to copy all the way to the end)
   :param lengthCol: Number of cols to copy (set to a negative number to copy all the way to the end)
   :return: Values copied from source vector

copyInto
^^^^^^^^

.. java:method:: public static void copyInto(float[][] src, float[][] dest, int destRowPos, int destColPos, int length)
   :outertype: MU

   Unlike System.arraycopy, this function copies the source matrix into the destination while preserving the original row length. It copies the full source.

   :param src: - source matrix
   :param dest: - destination matrix
   :param destRowPos: - starting target row
   :param destColPos: - starting target column position
   :param length: - number of rows to copy

diag
^^^^

.. java:method:: public static float[][] diag(float[] entries)
   :outertype: MU

   :param entries: A list of diagonal entries
   :return: A square diagonal matrix with given entries on the diagonal

diag
^^^^

.. java:method:: public static float[] diag(float[][] matrix)
   :outertype: MU

   :param matrix: Any matrix
   :return: Diagonal entries

difference
^^^^^^^^^^

.. java:method:: public static float[][] difference(float[][] A, float[][] B)
   :outertype: MU

   :param A: Any m x n matrix
   :param B: Any m x n matrix
   :return: The element-wise difference of the given matrices (A-B)

difference
^^^^^^^^^^

.. java:method:: public static float[] difference(float[] X, float[] Y)
   :outertype: MU

   :param X: Any vector
   :param Y: Any vector same length as vector X
   :return: X-Y (element-wise difference)

difference
^^^^^^^^^^

.. java:method:: public static float[] difference(float[] X)
   :outertype: MU

   :param X: Any vector
   :return: X(2:end) - X(1:end-1)

isMatrix
^^^^^^^^

.. java:method:: public static boolean isMatrix(float[][] matrix)
   :outertype: MU

   :param matrix: An array of arrays that is expected to be in matrix form
   :return: True if all "rows" (ie array elements) have the same length

makeVector
^^^^^^^^^^

.. java:method:: public static float[] makeVector(float start, float increment, float end)
   :outertype: MU

   :param start: Value of first element in vector
   :param increment: Increment between adjacent elements
   :param end: Value of last element in vector
   :return: A vector with elements evenly incremented from \ ``start``\  to \ ``end``\

max
^^^

.. java:method:: public static float max(float[] vector)
   :outertype: MU

   :param vector: Any vector
   :return: Minimum of elements

max
^^^

.. java:method:: public static float max(float[][] matrix)
   :outertype: MU

   :param matrix: Any matrix
   :return: Minimum of elements

mean
^^^^

.. java:method:: public static float mean(float[] vector)
   :outertype: MU

   :param vector: Any vector
   :return: Mean of vector elements

mean
^^^^

.. java:method:: public static float mean(float[][] matrix)
   :outertype: MU

   :param matrix: Any matrix
   :return: Mean of matrix elements

min
^^^

.. java:method:: public static float min(float[] vector)
   :outertype: MU

   :param vector: Any vector
   :return: Minimum of elements

min
^^^

.. java:method:: public static float min(float[][] matrix)
   :outertype: MU

   :param matrix: Any matrix
   :return: Minimum of elements

normalize
^^^^^^^^^

.. java:method:: public static float[] normalize(float[] vector)
   :outertype: MU

   :param vector: Any vector
   :return: The vector normalized to 2-norm of 1

outerprod
^^^^^^^^^

.. java:method:: public static float[][] outerprod(float[] A, float[] B)
   :outertype: MU

   :param A: Any vector
   :param B: Any vector
   :return: A*B (matrix with the outer product of A and B)

outerprod
^^^^^^^^^

.. java:method:: public static float[][] outerprod(float[] A, float[] B, float[][] result, int offset)
   :outertype: MU

   In-place outer product.

   :param A: Any vector
   :param B: Any vector
   :param result: the destination matrix
   :param offset: row in destination matrix to insert result
   :return: A*B (matrix with the outer product of A and B)

pnorm
^^^^^

.. java:method:: public static float pnorm(float[] vector, int p)
   :outertype: MU

   :param vector: Any vector
   :param p: Degree of p-norm (use -1 for infinity)
   :return: The p-norm of the vector

prod
^^^^

.. java:method:: public static float[] prod(float[] X, float a)
   :outertype: MU

   :param X: Any vector
   :param a: Any scalar
   :return: aX (each element of the vector multiplied by the scalar)

prod
^^^^

.. java:method:: public static float prod(float[] X, float[] Y)
   :outertype: MU

   :param X: Any vector
   :param Y: Any vector of the same length as X
   :return: X'Y

prod
^^^^

.. java:method:: public static float[] prod(float[][] A, float[] X)
   :outertype: MU

   :param A: Any matrix
   :param X: Any vector with the same number of elements as there are columns in A
   :return: AX

prod
^^^^

.. java:method:: public static float[][] prod(float[][] A, float[][] B)
   :outertype: MU

   :param A: Any m x n matrix
   :param B: Any n x p matrix
   :return: Product of matrices

prod
^^^^

.. java:method:: public static float[][] prod(float[][] A, float a)
   :outertype: MU

   :param A: Any matrix
   :param a: Any scalar
   :return: aA (each element of matrix multiplied by scalar)

prodElementwise
^^^^^^^^^^^^^^^

.. java:method:: public static float[] prodElementwise(float[] A, float[] B)
   :outertype: MU

   :param A: Any vector
   :param B: Any vector the same length as A
   :return: A(start:end) The identified subvector from A

prodElementwise
^^^^^^^^^^^^^^^

.. java:method:: public static float[][] prodElementwise(float[][] A, float[][] B)
   :outertype: MU

   :param A: Any matrix
   :param B: Any matrix the same dimensions as A
   :return: A .* B

random
^^^^^^

.. java:method:: public static float[][] random(int rows, int cols, PDF pdf)
   :outertype: MU

   :param rows: Number of rows in the requested matrix
   :param cols: Number of columns in the requested matrix
   :param pdf: One-dimensional PDF from which each element is drawn
   :return: Matrix with the given dimensions where each entry is randomly drawn from the given PDF

round
^^^^^

.. java:method:: public static int[] round(float[] vector)
   :outertype: MU

   :param vector: A vector
   :return: Elements rounded to nearest integer

shape
^^^^^

.. java:method:: public static float[][] shape(float[][] matrix, int rows, int cols)
   :outertype: MU

   :param matrix: An array of float arrays (normally a matrix but can have rows of different length)
   :param rows: Desired number of rows
   :param cols: Desired number of columns
   :return: Matrix with requested numbers of rows and columns drawn from the given matrix, and padded with zeros if there are not enough values in the original matrix

sum
^^^

.. java:method:: public static float[][] sum(float[][] A, float[][] B)
   :outertype: MU

   :param A: Any m x n matrix
   :param B: Any m x n matrix
   :return: The element-wise sum of the given matrices

sum
^^^

.. java:method:: public static float[] sum(float[] X, float[] Y)
   :outertype: MU

   :param X: Any vector
   :param Y: Any vector same length as vector X
   :return: X+Y (element-wise sum)

sum
^^^

.. java:method:: public static float sum(float[] vector)
   :outertype: MU

   :param vector: Any vector
   :return: Sum of elements

sumToIndex
^^^^^^^^^^

.. java:method:: public static float sumToIndex(float[] vector, int index)
   :outertype: MU

   :param vector: Any vector
   :param index: Index of last element to include in sum
   :return: Sum of elements

toString
^^^^^^^^

.. java:method:: public static String toString(float[][] matrix, int decimalPlaces)
   :outertype: MU

   TODO: handle exponential notation

   :param matrix: Any matrix
   :param decimalPlaces: number of decimal places to display for float values
   :return: String representation of matrix with one row per line

transpose
^^^^^^^^^

.. java:method:: public static float[][] transpose(float[] vector)
   :outertype: MU

   :param vector: Any vector
   :return: The transpose of the vector (i.e. a column vector instead of a row vector)

transpose
^^^^^^^^^

.. java:method:: public static float[][] transpose(float[][] matrix)
   :outertype: MU

   :param matrix: Any matrix
   :return: The transpose of the matrix

uniform
^^^^^^^

.. java:method:: public static float[][] uniform(int rows, int cols, float value)
   :outertype: MU

   :param rows: Number of rows in the requested matrix
   :param cols: Number of columns in the requested matrix
   :param value: Value of each element
   :return: Matrix with the given dimensions where each entry is the given value

variance
^^^^^^^^

.. java:method:: public static float variance(float[] vector, float mean)
   :outertype: MU

   :param vector: Any vector
   :param mean: Value around which to take variance, eg MU.mean(vector) or some pre-defined value
   :return: Bias-corrected variance of vector elements around the given values

zero
^^^^

.. java:method:: public static float[][] zero(int rows, int cols)
   :outertype: MU

   :param rows: Number of rows in the requested matrix
   :param cols: Number of columns in the requested matrix
   :return: Matrix of zeroes with the given dimensions


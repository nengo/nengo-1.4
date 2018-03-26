.. java:import:: java.util Random

PDFTools
========

.. java:package:: ca.nengo.math
   :noindex:

.. java:type:: public class PDFTools

   Convenience methods for using PDFs.

   :author: Bryan Tripp

Methods
-------
random
^^^^^^

.. java:method:: public static double random()
   :outertype: PDFTools

   Use this rather than Math.random(), to allow user to reproduce random results by setting the seed.

   :return: A random sample between 0 and 1

sampleBoolean
^^^^^^^^^^^^^

.. java:method:: public static boolean sampleBoolean(PDF pdf)
   :outertype: PDFTools

   Note: PDF treated as univariate (only first dimension considered).

   :param pdf: The PDF from which to sample
   :return: True iff sample from PDF is > 1

sampleFloat
^^^^^^^^^^^

.. java:method:: public static float sampleFloat(PDF pdf)
   :outertype: PDFTools

   Note: PDF treated as univariate (only first dimension considered).

   :param pdf: The PDF from which to sample
   :return: Sample from PDF (this is a convenience method for getting 1st dimension of sample() result)

sampleInt
^^^^^^^^^

.. java:method:: public static int sampleInt(PDF pdf)
   :outertype: PDFTools

   Note: PDF treated as univariate (only first dimension considered).

   :param pdf: The PDF from which to sample
   :return: Sample from PDF rounded to nearest integer

setSeed
^^^^^^^

.. java:method:: public static void setSeed(long seed)
   :outertype: PDFTools

   :param seed: New random seed for random()


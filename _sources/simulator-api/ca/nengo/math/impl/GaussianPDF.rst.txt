.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.math PDFTools

GaussianPDF
===========

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class GaussianPDF implements PDF

   Univariate Gaussian probability density function.

   :author: Bryan Tripp

Constructors
------------
GaussianPDF
^^^^^^^^^^^

.. java:constructor:: public GaussianPDF(float mean, float variance)
   :outertype: GaussianPDF

   :param mean: Mean of the distribution
   :param variance: Variance of the distribution

GaussianPDF
^^^^^^^^^^^

.. java:constructor:: public GaussianPDF(float mean, float variance, float peak)
   :outertype: GaussianPDF

   Constructs a scaled Gaussian with the given peak value.

   :param mean: Mean of the distribution
   :param variance: Variance of the distribution
   :param peak: Maximum value of scaled Gaussian

GaussianPDF
^^^^^^^^^^^

.. java:constructor:: public GaussianPDF()
   :outertype: GaussianPDF

   Instantiates with default mean=0 and variance=1

Methods
-------
clone
^^^^^

.. java:method:: @Override public PDF clone() throws CloneNotSupportedException
   :outertype: GaussianPDF

doSample
^^^^^^^^

.. java:method:: public static float[] doSample()
   :outertype: GaussianPDF

   This method is publically exposed because normal deviates are often needed, and static access allows the compiler to inline the call, which brings a small performance advantage.

   :return: Two random samples from a normal distribution (mean 0; variance 1)

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: GaussianPDF

   :return: 1

   **See also:** :java:ref:`ca.nengo.math.Function.getDimension()`

getMean
^^^^^^^

.. java:method:: public float getMean()
   :outertype: GaussianPDF

   :return: Mean of the distribution

getPeak
^^^^^^^

.. java:method:: public float getPeak()
   :outertype: GaussianPDF

   :return: Maximum value of scaled Gaussian

getScalePeakWithVariance
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean getScalePeakWithVariance()
   :outertype: GaussianPDF

   :return: If true, the peak of the distribution scales automatically so that the integral is 1

getVariance
^^^^^^^^^^^

.. java:method:: public float getVariance()
   :outertype: GaussianPDF

   :return: Variance of the distribution

map
^^^

.. java:method:: public float map(float[] from)
   :outertype: GaussianPDF

   **See also:** :java:ref:`ca.nengo.math.Function.map(float[])`

multiMap
^^^^^^^^

.. java:method:: public float[] multiMap(float[][] from)
   :outertype: GaussianPDF

   **See also:** :java:ref:`ca.nengo.math.Function.multiMap(float[][])`

sample
^^^^^^

.. java:method:: public float[] sample()
   :outertype: GaussianPDF

   **See also:** :java:ref:`ca.nengo.math.PDF.sample()`

setMean
^^^^^^^

.. java:method:: public void setMean(float mean)
   :outertype: GaussianPDF

   :param mean: Mean of the distribution

setPeak
^^^^^^^

.. java:method:: public void setPeak(float peak)
   :outertype: GaussianPDF

   :param peak: Maximum value of scaled Gaussian

setScalePeakWithVariance
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setScalePeakWithVariance(boolean scale)
   :outertype: GaussianPDF

   :param scale: If true, the peak of the distribution scales automatically so that the integral is 1

setVariance
^^^^^^^^^^^

.. java:method:: public void setVariance(float variance)
   :outertype: GaussianPDF

   :param variance: Variance of the distribution


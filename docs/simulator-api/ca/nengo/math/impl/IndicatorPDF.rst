.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.math PDFTools

IndicatorPDF
============

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class IndicatorPDF implements PDF

   Uniform probability between upper and lower limits, zero elsewhere.

   :author: Bryan Tripp

Constructors
------------
IndicatorPDF
^^^^^^^^^^^^

.. java:constructor:: public IndicatorPDF(float low, float high)
   :outertype: IndicatorPDF

   :param low: Lower limit of range of possible values
   :param high: Upper limit of range of possible values

IndicatorPDF
^^^^^^^^^^^^

.. java:constructor:: public IndicatorPDF(float exact)
   :outertype: IndicatorPDF

   :param exact: A value at which the PDF is infinity (zero at other values)

Methods
-------
clone
^^^^^

.. java:method:: @Override public PDF clone() throws CloneNotSupportedException
   :outertype: IndicatorPDF

getDensity
^^^^^^^^^^

.. java:method:: public float getDensity()
   :outertype: IndicatorPDF

   :return: Probability density between low and high limits

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: IndicatorPDF

   :return: 1

   **See also:** :java:ref:`ca.nengo.math.Function.getDimension()`

getHigh
^^^^^^^

.. java:method:: public float getHigh()
   :outertype: IndicatorPDF

   :return: Upper limit of range of possible values

getLow
^^^^^^

.. java:method:: public float getLow()
   :outertype: IndicatorPDF

   :return: Lower limit of range of possible values

map
^^^

.. java:method:: public float map(float[] from)
   :outertype: IndicatorPDF

   **See also:** :java:ref:`ca.nengo.math.Function.map(float[])`

multiMap
^^^^^^^^

.. java:method:: public float[] multiMap(float[][] from)
   :outertype: IndicatorPDF

   **See also:** :java:ref:`ca.nengo.math.Function.multiMap(float[][])`

sample
^^^^^^

.. java:method:: public float[] sample()
   :outertype: IndicatorPDF

   **See also:** :java:ref:`ca.nengo.math.PDF.sample()`

setHigh
^^^^^^^

.. java:method:: public void setHigh(float high)
   :outertype: IndicatorPDF

   :param high: Upper limit of range of possible values

setLow
^^^^^^

.. java:method:: public void setLow(float low)
   :outertype: IndicatorPDF

   :param low: Lower limit of range of possible values


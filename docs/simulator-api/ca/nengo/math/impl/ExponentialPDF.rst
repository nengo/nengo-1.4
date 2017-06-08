.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.math PDFTools

ExponentialPDF
==============

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class ExponentialPDF extends AbstractFunction implements PDF

   A one-dimensional exponential probability density function. TODO: unit tests TODO: generalize to any function with invertible integral (see numerical recipes in C chapter 7)

   :author: Bryan Tripp

Constructors
------------
ExponentialPDF
^^^^^^^^^^^^^^

.. java:constructor:: public ExponentialPDF(float tau)
   :outertype: ExponentialPDF

   :param tau: Rate parameter of exponential distribution

Methods
-------
clone
^^^^^

.. java:method:: @Override public PDF clone() throws CloneNotSupportedException
   :outertype: ExponentialPDF

getTau
^^^^^^

.. java:method:: public float getTau()
   :outertype: ExponentialPDF

   :return: Rate parameter of exponential distribution

map
^^^

.. java:method:: public float map(float[] from)
   :outertype: ExponentialPDF

   **See also:** :java:ref:`ca.nengo.math.impl.AbstractFunction.map(float[])`

sample
^^^^^^

.. java:method:: public float[] sample()
   :outertype: ExponentialPDF

   **See also:** :java:ref:`ca.nengo.math.PDF.sample()`

setTau
^^^^^^

.. java:method:: public void setTau(float tau)
   :outertype: ExponentialPDF

   :param tau: Rate parameter of exponential distribution


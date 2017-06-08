.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.math PDFTools

PoissonPDF
==========

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class PoissonPDF extends AbstractFunction implements PDF

   A Poisson distribution.

   :author: Bryan Tripp

Constructors
------------
PoissonPDF
^^^^^^^^^^

.. java:constructor:: public PoissonPDF(float rate)
   :outertype: PoissonPDF

   :param rate: The mean & variance of the distribution

Methods
-------
clone
^^^^^

.. java:method:: @Override public PDF clone() throws CloneNotSupportedException
   :outertype: PoissonPDF

map
^^^

.. java:method:: @Override public float map(float[] from)
   :outertype: PoissonPDF

   **See also:** :java:ref:`ca.nengo.math.impl.AbstractFunction.map(float[])`

sample
^^^^^^

.. java:method:: public float[] sample()
   :outertype: PoissonPDF

   **See also:** :java:ref:`ca.nengo.math.PDF.sample()`


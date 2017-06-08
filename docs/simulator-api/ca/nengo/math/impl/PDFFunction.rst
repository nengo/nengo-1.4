.. java:import:: ca.nengo.math PDF

PDFFunction
===========

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class PDFFunction extends AbstractFunction

   A Function that produces outputs drawn from a given distribution.

   :author: Daniel Rasmussen

Constructors
------------
PDFFunction
^^^^^^^^^^^

.. java:constructor:: public PDFFunction(PDF pdf)
   :outertype: PDFFunction

   :param signal: sequence defining output (each element is a (potentially) multidimensional output)
   :param dimension: Dimension of signal on which to base Function output

Methods
-------
getPDF
^^^^^^

.. java:method:: public PDF getPDF()
   :outertype: PDFFunction

   :return: TimeSeries from which to obtain Function of time

map
^^^

.. java:method:: public float map(float[] from)
   :outertype: PDFFunction

   **See also:** :java:ref:`ca.nengo.math.impl.AbstractFunction.map(float[])`

setPDF
^^^^^^

.. java:method:: public void setPDF(PDF pdf)
   :outertype: PDFFunction

   :param pdf: input PDF


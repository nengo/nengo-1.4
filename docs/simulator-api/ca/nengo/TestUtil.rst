.. java:import:: junit.framework TestCase

TestUtil
========

.. java:package:: ca.nengo
   :noindex:

.. java:type:: public class TestUtil

   Common utilities for unit tests.

   :author: Bryan Tripp

Methods
-------
assertClose
^^^^^^^^^^^

.. java:method:: public static void assertClose(float a, float b, float tolerance)
   :outertype: TestUtil

   Assertion that two float values are close to each other, within given tolerance.

   :param a: A float value
   :param b: Another float value
   :param tolerance: Maximum expected difference between them


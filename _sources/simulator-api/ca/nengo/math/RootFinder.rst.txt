RootFinder
==========

.. java:package:: ca.nengo.math
   :noindex:

.. java:type:: public interface RootFinder

   Finds a root of a function.

   :author: Bryan Tripp

Methods
-------
findRoot
^^^^^^^^

.. java:method:: public float findRoot(Function function, float startLow, float startHigh, float tolerance)
   :outertype: RootFinder

   :param function: Function ``f(x)`` to find root of
   :param startLow: Low-valued x from which to start search
   :param startHigh: High-valued x from which to start. You typically give startLow and startHigh so that you expect the signs of the functions at these values to be different.
   :param tolerance: Max acceptable ``|f(x)|`` for which to return x
   :return: x for which ``|f(x)|`` <= tolerance

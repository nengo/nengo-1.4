.. java:import:: ca.nengo.util IndexFinder

StatefulIndexFinder
===================

.. java:package:: ca.nengo.util.impl
   :noindex:

.. java:type:: public class StatefulIndexFinder implements IndexFinder

   An IndexFinder that searches linearly, starting where the last answer was. This is a good choice if many interpolations will be made on the same series, and adjacent requests will be close to each other. TODO: test

   :author: Bryan Tripp

Constructors
------------
StatefulIndexFinder
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public StatefulIndexFinder(float[] values)
   :outertype: StatefulIndexFinder

   :param values: Must be monotonically increasing.

Methods
-------
areMonotonicallyIncreasing
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static boolean areMonotonicallyIncreasing(float[] values)
   :outertype: StatefulIndexFinder

   :param values: A list of values
   :return: True if list values increases monotonically, false otherwise

clone
^^^^^

.. java:method:: @Override public StatefulIndexFinder clone() throws CloneNotSupportedException
   :outertype: StatefulIndexFinder

findIndexBelow
^^^^^^^^^^^^^^

.. java:method:: public int findIndexBelow(float value)
   :outertype: StatefulIndexFinder


IndexFinder
===========

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public interface IndexFinder extends Cloneable

   Searches a monotonically increasing list of floating-point values for the largest one that is less than or equal to a requested value. The list of values is typically set at construction time.

   :author: Bryan Tripp

Methods
-------
clone
^^^^^

.. java:method:: public IndexFinder clone() throws CloneNotSupportedException
   :outertype: IndexFinder

findIndexBelow
^^^^^^^^^^^^^^

.. java:method:: public int findIndexBelow(float value)
   :outertype: IndexFinder

   :param value: A floating-point value that the list is expected to span
   :return: The index of the largest value in the list which is smaller than the 'value' arg


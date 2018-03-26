CouplingMatrix
==============

.. java:package:: ca.nengo.ui.configurable.matrixEditor
   :noindex:

.. java:type:: public interface CouplingMatrix

   A specification for the coupling of a projection.

   :author: Bryan Tripp

Methods
-------
getElement
^^^^^^^^^^

.. java:method:: public float getElement(int row, int col)
   :outertype: CouplingMatrix

   Returns the element at the given matrix location.

   :param row: the row number
   :param col: the column number
   :return: TODO

getFromSize
^^^^^^^^^^^

.. java:method:: public int getFromSize()
   :outertype: CouplingMatrix

   :return: TODO

getToSize
^^^^^^^^^

.. java:method:: public int getToSize()
   :outertype: CouplingMatrix

   :return: TODO

setElement
^^^^^^^^^^

.. java:method:: public void setElement(float theValue, int row, int col)
   :outertype: CouplingMatrix

   :param theValue: TODO
   :param row: TODO
   :param col: TODO


CouplingMatrixImpl
==================

.. java:package:: ca.nengo.ui.configurable.matrixEditor
   :noindex:

.. java:type:: public class CouplingMatrixImpl implements CouplingMatrix

   Default implementation of coupling matrix.

   :author: Bryan Tripp

Constructors
------------
CouplingMatrixImpl
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CouplingMatrixImpl()
   :outertype: CouplingMatrixImpl

   TODO

CouplingMatrixImpl
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CouplingMatrixImpl(int theFromSize, int theToSize)
   :outertype: CouplingMatrixImpl

   Creates a new instance.

   :param theFromSize: the number of rows/columns in the matrix
   :param theToSize: TODO

Methods
-------
getData
^^^^^^^

.. java:method:: public float[][] getData()
   :outertype: CouplingMatrixImpl

   :return: TODO

getElement
^^^^^^^^^^

.. java:method:: public float getElement(int theRow, int theCol)
   :outertype: CouplingMatrixImpl

   **See also:** :java:ref:`ca.nengo.ui.configurable.matrixEditor.CouplingMatrix.getElement(int,
   int)`

getFromSize
^^^^^^^^^^^

.. java:method:: public int getFromSize()
   :outertype: CouplingMatrixImpl

getToSize
^^^^^^^^^

.. java:method:: public int getToSize()
   :outertype: CouplingMatrixImpl

setData
^^^^^^^

.. java:method:: public void setData(float[][] theData)
   :outertype: CouplingMatrixImpl

   :param theData: TODO

setElement
^^^^^^^^^^

.. java:method:: public void setElement(float theValue, int row, int col)
   :outertype: CouplingMatrixImpl


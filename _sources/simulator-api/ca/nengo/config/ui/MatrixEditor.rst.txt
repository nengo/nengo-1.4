.. java:import:: java.awt BorderLayout

.. java:import:: java.awt Component

.. java:import:: java.awt Dimension

.. java:import:: java.awt FlowLayout

.. java:import:: java.awt.event ActionEvent

.. java:import:: java.awt.event ActionListener

.. java:import:: java.util EventObject

.. java:import:: javax.swing DefaultCellEditor

.. java:import:: javax.swing JButton

.. java:import:: javax.swing JLabel

.. java:import:: javax.swing JOptionPane

.. java:import:: javax.swing JPanel

.. java:import:: javax.swing JScrollPane

.. java:import:: javax.swing JTable

.. java:import:: javax.swing JTextField

.. java:import:: javax.swing.border EmptyBorder

.. java:import:: javax.swing.table AbstractTableModel

.. java:import:: javax.swing.table DefaultTableCellRenderer

.. java:import:: javax.swing.table JTableHeader

.. java:import:: javax.swing.table TableModel

MatrixEditor
============

.. java:package:: ca.nengo.config.ui
   :noindex:

.. java:type:: public class MatrixEditor extends JPanel

   An UI component for editing matrices. TODO: don't really need to enforce equal column lengths TODO: allow copy/paste, allow insert/delete at specific rows/columns

   :author: Bryan Tripp

Constructors
------------
MatrixEditor
^^^^^^^^^^^^

.. java:constructor:: public MatrixEditor(float[][] matrix)
   :outertype: MatrixEditor

   Creates an editor for the given matrix.

   :param matrix: The matrix to be edited

MatrixEditor
^^^^^^^^^^^^

.. java:constructor:: public MatrixEditor(float[][] matrix, boolean numRowsFixed, boolean numColsFixed)
   :outertype: MatrixEditor

   :param matrix: The matrix to be edited
   :param numRowsFixed: If false, the user is able to change the number of matrix rows
   :param numColsFixed: If false, the user is able to change the number of matrix columns

Methods
-------
finishEditing
^^^^^^^^^^^^^

.. java:method:: public void finishEditing()
   :outertype: MatrixEditor

   Stops current cell editing

getColumnCount
^^^^^^^^^^^^^^

.. java:method:: public int getColumnCount()
   :outertype: MatrixEditor

   :return: Number of columns

getControlPanel
^^^^^^^^^^^^^^^

.. java:method:: public JPanel getControlPanel()
   :outertype: MatrixEditor

   :return: The panel containing controls (caller can add further controls in a FlowLayout)

getMatrix
^^^^^^^^^

.. java:method:: public float[][] getMatrix()
   :outertype: MatrixEditor

   :return: The matrix being edited

getRowCount
^^^^^^^^^^^

.. java:method:: public int getRowCount()
   :outertype: MatrixEditor

   :return: Number of rows


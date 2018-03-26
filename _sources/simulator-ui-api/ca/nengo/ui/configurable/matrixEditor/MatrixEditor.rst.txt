.. java:import:: java.awt BorderLayout

.. java:import:: java.awt Component

.. java:import:: java.awt Dimension

.. java:import:: java.awt.event WindowAdapter

.. java:import:: java.awt.event WindowEvent

.. java:import:: java.util EventObject

.. java:import:: javax.swing DefaultCellEditor

.. java:import:: javax.swing JFrame

.. java:import:: javax.swing JOptionPane

.. java:import:: javax.swing JPanel

.. java:import:: javax.swing JScrollPane

.. java:import:: javax.swing JTable

.. java:import:: javax.swing JTextField

.. java:import:: javax.swing UIManager

.. java:import:: javax.swing.table AbstractTableModel

.. java:import:: javax.swing.table TableColumn

.. java:import:: javax.swing.table TableModel

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

MatrixEditor
============

.. java:package:: ca.nengo.ui.configurable.matrixEditor
   :noindex:

.. java:type:: public class MatrixEditor extends JPanel

   An UI component for editing matrices.

   :author: Bryan Tripp

Fields
------
myTable
^^^^^^^

.. java:field::  JTable myTable
   :outertype: MatrixEditor

Constructors
------------
MatrixEditor
^^^^^^^^^^^^

.. java:constructor:: public MatrixEditor(CouplingMatrix theMatrix)
   :outertype: MatrixEditor

   Creates an editor for the given coupling matrix.

   :param theMatrix: TODO

Methods
-------
finishEditing
^^^^^^^^^^^^^

.. java:method:: public void finishEditing()
   :outertype: MatrixEditor

   TODO

getValueAt
^^^^^^^^^^

.. java:method:: public Object getValueAt(int arg0, int arg1)
   :outertype: MatrixEditor

   :param arg0: TODO
   :param arg1: TODO
   :return: TODO

main
^^^^

.. java:method:: public static void main(String[] args)
   :outertype: MatrixEditor

   For testing

   :param args: TODO

setValueAt
^^^^^^^^^^

.. java:method:: public void setValueAt(Object arg0, int arg1, int arg2)
   :outertype: MatrixEditor

   :param arg0: TODO
   :param arg1: TODO
   :param arg2: TODO


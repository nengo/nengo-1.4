/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "MatrixEditor.java". Description:
"An UI component for editing matrices.

  @author Bryan Tripp"

The Initial Developer of the Original Code is Bryan Tripp & Centre for Theoretical Neuroscience, University of Waterloo. Copyright (C) 2006-2008. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of the GNU
Public License license (the GPL License), in which case the provisions of GPL
License are applicable  instead of those above. If you wish to allow use of your
version of this file only under the terms of the GPL License and not to allow
others to use your version of this file under the MPL, indicate your decision
by deleting the provisions above and replace  them with the notice and other
provisions required by the GPL License.  If you do not delete the provisions above,
a recipient may use your version of this file under either the MPL or the GPL License.
 */

/*
 * Created on Jan 30, 2004
 */
package ca.nengo.ui.widgets.matrixEditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import ca.nengo.ui.lib.Style.NengoStyle;

/**
 * An UI component for editing matrices.
 * 
 * @author Bryan Tripp
 */
public class MatrixEditor extends JPanel {

    private static final long serialVersionUID = 1L;

    private class MECellEditor extends DefaultCellEditor {
        private static final long serialVersionUID = 7289808186710531L;
        private JTextField myTextField;

        public MECellEditor() {
            super(new JTextField());
            myTextField = (JTextField) this.getComponent();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column) {

            Component component = super.getTableCellEditorComponent(table,
                    value,
                    isSelected,
                    row,
                    column);

            myTextField.selectAll();
            return component;
        }
    }

    private class MatrixTableModel extends AbstractTableModel {
    	
        private static final long serialVersionUID = 1L;
        private final CouplingMatrix myMatrix;

        public MatrixTableModel(CouplingMatrix theMatrix) {
            myMatrix = theMatrix;
        }

        public int getColumnCount() {
            return myMatrix.getFromSize();
        }

        @Override
        public String getColumnName(int theColumn) {
            return String.valueOf(theColumn + 1);
        }

        public int getRowCount() {
            return myMatrix.getToSize();
        }

        public Object getValueAt(int row, int col) {
            return new Float(myMatrix.getElement(row + 1, col + 1));
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return true;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            try {
                float val = Float.parseFloat(value.toString());
                myMatrix.setElement(val, row + 1, col + 1);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null,
                        "Please enter a number",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        
    }

    private final TableModel myTableModel;
    private final JTable myTable;
    
    private static final int minColWidth = 30;

    /**
     * Creates an editor for the given coupling matrix.
     * @param matrix The coupling matrix
     */
    public MatrixEditor(CouplingMatrix matrix) {
        super(new BorderLayout());
        myTableModel = new MatrixTableModel(matrix);
        myTable = new JTable(myTableModel);
        if (NengoStyle.GTK) {
            myTable.setRowHeight(24);
        }

        // resize massive tables to preserve minimum column width
        int columnCount = myTable.getColumnCount();
        Dimension tableSize = myTable.getPreferredScrollableViewportSize();
        if (tableSize.width < columnCount * minColWidth) {
            for (int i = 0; i < columnCount; i++) {
                TableColumn column = null;
                column = myTable.getColumnModel().getColumn(i);
                column.setMinWidth(minColWidth);
                column.setPreferredWidth(minColWidth);
            }
            myTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }

        myTable.setDefaultEditor(Object.class, new MECellEditor());
        JScrollPane scroll = new JScrollPane(myTable);
        this.add(scroll, BorderLayout.CENTER);
    }

    public void finishEditing() {
        if (myTable.getCellEditor() != null) {
            myTable.getCellEditor().stopCellEditing();
        }
    }

    /**
     * @param row Row index
     * @param col Column index
     * @return Object at passed row, col
     */
    public Object getValueAt(int row, int col) {
        return myTableModel.getValueAt(row, col);
    }

    /**
     * @param value Object to set
     * @param row Row index
     * @param col Column index
     */
    public void setValueAt(Object value, int row, int col) {
        myTableModel.setValueAt(value, row, col);
    }

}

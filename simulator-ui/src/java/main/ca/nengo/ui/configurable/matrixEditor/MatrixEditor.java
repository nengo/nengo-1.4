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
package ca.nengo.ui.configurable.matrixEditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * An UI component for editing matrices.
 * 
 * @author Bryan Tripp
 */
public class MatrixEditor extends JPanel {

	private static final long serialVersionUID = 1L;

	private class MyCellEditor extends DefaultCellEditor {
		private static final long serialVersionUID = 7289808186710531L;
		private JTextField myTextField;

		public MyCellEditor() {
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

		@Override
		public boolean isCellEditable(EventObject anEvent) {
			// TODO Auto-generated method stub
			return super.isCellEditable(anEvent);
		}
	}

	// for testing
	public static void main(String args[]) {
		CouplingMatrix matrix = new CouplingMatrixImpl(5, 3);
		MatrixEditor editor = new MatrixEditor(matrix);

		try {
			JFrame frame = new JFrame("test");
			frame.getContentPane().add(editor);

			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});

			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private final TableModel myTableModel;
	JTable myTable;

	/**
	 * Creates an editor for the given coupling matrix.
	 */
	public MatrixEditor(CouplingMatrix theMatrix) {
		super(new BorderLayout());
		myTableModel = new MatrixTableModel(theMatrix);
		myTable = new JTable(myTableModel);
		myTable.setDefaultEditor(Object.class, new MyCellEditor());
		JScrollPane scroll = new JScrollPane(myTable);
		this.add(scroll, BorderLayout.CENTER);
	}

	public void finishEditing() {
		if (myTable.getCellEditor() != null)
			myTable.getCellEditor().stopCellEditing();
	}

	public Object getValueAt(int arg0, int arg1) {
		return myTableModel.getValueAt(arg0, arg1);
	}

	public void setValueAt(Object arg0, int arg1, int arg2) {
		myTableModel.setValueAt(arg0, arg1, arg2);
	}

	private class MatrixTableModel extends AbstractTableModel {

		/**
		 * 
		 */
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

		public Object getValueAt(int theRow, int theColumn) {
			return new Float(myMatrix.getElement(theRow + 1, theColumn + 1));
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		@Override
		public void setValueAt(Object theValue, int theRow, int theColumn) {
			try {
				float val = Float.parseFloat(theValue.toString());
				myMatrix.setElement(val, theRow + 1, theColumn + 1);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null,
						"Please enter a number",
						"Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}

	}

}

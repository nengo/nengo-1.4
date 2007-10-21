/*
 * Created on Jan 30, 2004
 */
package ca.neo.ui.configurable.matrixEditor;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * An UI component for editing matrices.
 * 
 * @author Bryan Tripp
 */
public class MatrixEditor extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
		JScrollPane scroll = new JScrollPane(myTable);
		this.add(scroll, BorderLayout.CENTER);
	}

	public void finishEditing() {
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
				JOptionPane.showMessageDialog(null, "Please enter a number",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}

	}

}

/*
 * Created on Jan 30, 2004
 */
package ca.uwaterloo.nesim.editor.control;

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

import ca.uwaterloo.nesim.editor.model.CouplingMatrix;
import ca.uwaterloo.nesim.editor.model.impl.CouplingMatrixImpl;

/**
 * An UI component for editing matrices. 
 * @author Bryan Tripp
 */
public class MatrixEditor extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TableModel myTableModel;
	
	/**
	 * Creates an editor for the given coupling matrix.   
	 */
	public MatrixEditor(CouplingMatrix theMatrix) {
		super(new BorderLayout());
		myTableModel = new MatrixTableModel(theMatrix);
		JTable table = new JTable(myTableModel); 
		JScrollPane scroll = new JScrollPane(table);
		this.add(scroll, BorderLayout.CENTER);
	}
		
	private class MatrixTableModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private CouplingMatrix myMatrix;
		
		public MatrixTableModel(CouplingMatrix theMatrix) {
			myMatrix = theMatrix;	
		}
		
		public int getColumnCount() {
			return myMatrix.getFromSize();
		}

		public int getRowCount() {
			return myMatrix.getToSize();
		}

		public Object getValueAt(int theRow, int theColumn) {
			return new Float(myMatrix.getElement(theRow + 1, theColumn + 1));
		}
		
		public void setValueAt(Object theValue, int theRow, int theColumn) {
			try {
				float val = Float.parseFloat(theValue.toString());
				myMatrix.setElement(val, theRow + 1, theColumn + 1);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Please enter a number", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		public String getColumnName(int theColumn) {
			return String.valueOf(theColumn + 1);
		}
		
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}
		
	}

	//for testing	
	public static void main(String args[]) {
		CouplingMatrix matrix = new CouplingMatrixImpl(5, 3);
		MatrixEditor editor = new MatrixEditor(matrix);
		
		try {
			JFrame frame = new JFrame("test");
			frame.getContentPane().add(editor);

			frame.addWindowListener(new WindowAdapter() {
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

}

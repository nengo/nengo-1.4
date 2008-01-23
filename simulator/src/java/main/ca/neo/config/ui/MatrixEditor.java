/*
 * Created on Jan 30, 2004
 */
package ca.neo.config.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

/**
 * An UI component for editing matrices.
 * 
 * TODO: don't really need to enforce equal column lengths
 * 
 * @author Bryan Tripp
 */
public class MatrixEditor extends JPanel {

	private static final long serialVersionUID = 1L;

	private float[][] myMatrix;
	private final TableModel myTableModel;
	private JTable myTable;

	/**
	 * Creates an editor for the given coupling matrix.
	 */
	public MatrixEditor(float[][] matrix) {
		super(new BorderLayout());
		myMatrix = matrix;
		myTableModel = new MatrixTableModel(matrix);
		myTable = new JTable(myTableModel);
		myTable.setMinimumSize(new Dimension(matrix.length == 0 ? 50 : 50*matrix[0].length, 16*matrix.length));
		myTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		myTable.setRowSelectionAllowed(false);
		JScrollPane scroll = new JScrollPane(myTable,  
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		JTable rowHeader = getRowHeaderView(matrix.length);
		rowHeader.setBackground(myTable.getTableHeader().getBackground());
		scroll.setRowHeaderView(rowHeader);
		scroll.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowHeader.getTableHeader());
		this.add(scroll, BorderLayout.CENTER);
	}
	
	private static JTable getRowHeaderView(int rows) {
		String[][] labels = new String[rows][];
		for (int i = 0; i < rows; i++) {
			labels[i] = new String[]{String.valueOf(i+1)};
		}
		JTable result = new JTable(labels, new String[]{""});
		result.setPreferredScrollableViewportSize(new Dimension(40, 100));
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				JLabel field = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				field.setBorder(new EmptyBorder(0, 0, 0, 3));
				return field;
			}			
		};
		renderer.setHorizontalAlignment(JTextField.RIGHT);
		result.getColumnModel().getColumn(0).setCellRenderer(renderer);
		result.setEnabled(false);
		
		JTableHeader corner = result.getTableHeader();
		corner.setReorderingAllowed(false);
		corner.setResizingAllowed(false);
		
		return result;
	}
	
	public float[][] getMatrix() {
		return myMatrix;
	}

	public void finishEditing() {
		if (myTable.getCellEditor() != null)
			myTable.getCellEditor().stopCellEditing();
	}

	private class MatrixTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;		
		private final float[][] myMatrix;

		public MatrixTableModel(float[][] matrix) {
			int cols = matrix.length > 0 ? matrix[0].length : 0;
			for (int i = 1; i < matrix.length; i++) {
				if (matrix[i].length != cols) {
					throw new IllegalArgumentException("Matrix must have the same number of columns in each row");
				}
			}
			myMatrix = matrix;
		}

		public int getColumnCount() {
			return myMatrix.length > 0 ? myMatrix[0].length : 0;
		}

		@Override
		public String getColumnName(int theColumn) {
			return String.valueOf(theColumn + 1);
		}

		public int getRowCount() {
			return myMatrix.length;
		}

		public Object getValueAt(int row, int column) {
			return new Float(myMatrix[row][column]);
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		@Override
		public void setValueAt(Object value, int row, int column) {
			try {
				float val = Float.parseFloat(value.toString());
				myMatrix[row][column] = val;
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Please enter a number",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	// for testing
	public static void main(String args[]) {
		float[][] matrix = new float[][]{new float[3], new float[3], new float[3], new float[3], new float[3]};
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

}

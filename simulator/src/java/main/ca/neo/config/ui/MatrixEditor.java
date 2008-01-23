/*
 * Created on Jan 30, 2004
 */
package ca.neo.config.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
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
	private final MatrixTableModel myTableModel;
	private final RowHeaderTableModel myRowHeaderModel;
	private JTable myTable;
	private JPanel myControlPanel;

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
		
		myRowHeaderModel = new RowHeaderTableModel(myTableModel);
		JTable rowHeader = getRowHeaderView(myRowHeaderModel);
		rowHeader.setBackground(myTable.getTableHeader().getBackground());
		scroll.setRowHeaderView(rowHeader);
		scroll.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowHeader.getTableHeader());
		this.add(scroll, BorderLayout.CENTER);
		
		myControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		this.add(myControlPanel, BorderLayout.SOUTH);
	}
	
	public MatrixEditor(float[][] matrix, boolean numRowsFixed, boolean numColsFixed) {
		this(matrix);
		
		if (!numRowsFixed) {
			JButton resizeRowsButton = new JButton("Set # Rows"); 
			resizeRowsButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int n = promptForNum("rows", myTableModel.getRowCount());
					if (n >= 0) {
						MatrixEditor.this.setNumRows(n);
					}
				}					
			});
			myControlPanel.add(resizeRowsButton);
		}
		
		if (!numColsFixed) {
			JButton resizeColsButton = new JButton("Set # Columns");
			resizeColsButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int n = promptForNum("columns", myTableModel.getColumnCount());
					if (n >= 0) {
						MatrixEditor.this.setNumCols(n);
					}
				}					
			});
			myControlPanel.add(resizeColsButton);				
		}
	}
	
	public JPanel getControlPanel() {
		return myControlPanel;
	}
	
	private int promptForNum(String name, int initialValue) {
		int n = -1;

		String nString = JOptionPane.showInputDialog("Number of " + name + ":", String.valueOf(initialValue));
		try {
			if (nString != null) n = Integer.parseInt(nString);  
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(MatrixEditor.this, 
					"# " + name + " must be an integer", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		return n;
	}
	
	private void setNumRows(int n) {
		int rows = myMatrix.length;
		int cols = myMatrix.length > 0 ? myMatrix[0].length : 0;
		
		float[][] newMatrix = new float[n][];
		System.arraycopy(myMatrix, 0, newMatrix, 0, Math.min(rows, n));
		
		for (int i = rows; i < n; i++) {
			newMatrix[i] = new float[cols];
		}
		
		myMatrix = newMatrix;
		myTableModel.setMatrix(myMatrix);
		
		if (n > rows) {
			myTableModel.fireTableRowsInserted(rows-1, n-1);			
			myRowHeaderModel.fireTableRowsInserted(rows-1, n-1);			
		} else if (n < rows) {
			myTableModel.fireTableRowsDeleted(n, rows-1);
			myRowHeaderModel.fireTableRowsDeleted(n, rows-1);
		}
	}
	
	private void setNumCols(int n) {
		int rows = myMatrix.length;
		int cols = myMatrix.length > 0 ? myMatrix[0].length : 0;
		
		float[][] newMatrix = new float[rows][];
		for (int i = 0; i < rows; i++) {
			newMatrix[i]= new float[n];
			System.arraycopy(myMatrix[i], 0, newMatrix[i], 0, Math.min(cols,n));
		}
		
		myMatrix = newMatrix;
		myTableModel.setMatrix(myMatrix);
		
		System.out.println("cols: " + myTableModel.getColumnCount());

//		myTableModel.fireTableRowsUpdated(0, rows-1);	
		myTableModel.fireTableStructureChanged();	
	}

	private static JTable getRowHeaderView(TableModel model) {
		JTable result = new JTable(model); //labels, new String[]{""});
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
	
	private class RowHeaderTableModel extends AbstractTableModel {
		
		private static final long serialVersionUID = 1L;
		
		private TableModel myTableModel;
		
		public RowHeaderTableModel(TableModel model) {
			myTableModel = model;
		}

		public int getColumnCount() {
			return 1;
		}

		public int getRowCount() {
			return myTableModel.getRowCount();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return String.valueOf(rowIndex+1);
		}

		@Override
		public String getColumnName(int column) {
			return "";
		}	
		
	}

	private class MatrixTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;		
		private float[][] myMatrix;

		public MatrixTableModel(float[][] matrix) {
			setMatrix(matrix);
		}
		
		public void setMatrix(float[][] matrix) {
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
//		float[][] matrix = new float[0][];
		MatrixEditor editor = new MatrixEditor(matrix, false, false);

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

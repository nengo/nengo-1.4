package ca.neo.ui.configurable.panels;

import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertyInputPanel;
import ca.neo.ui.configurable.matrixEditor.CouplingMatrixImpl;
import ca.neo.ui.configurable.matrixEditor.MatrixEditor;
import ca.shu.ui.lib.util.Util;

public/**
		 * Input panel for a matrix
		 * 
		 * @author Shu Wu
		 */
class CouplingMatrixPanel extends PropertyInputPanel {

	private static final long serialVersionUID = 1L;
	private CouplingMatrixImpl couplingMatrix;

	/**
	 * Editor responsible for creating the spreadsheet-like matrix editing
	 * Interface
	 */
	private MatrixEditor editor;

	public CouplingMatrixPanel(PropertyDescriptor property, int fromSize,
			int toSize) {
		super(property);

		couplingMatrix = new CouplingMatrixImpl(fromSize, toSize);
		editor = new MatrixEditor(couplingMatrix);

		add(editor);
	}

	@Override
	public float[][] getValue() {
		editor.finishEditing();

		return couplingMatrix.getData();
	}

	@Override
	public boolean isValueSet() {
		return true;
	}

	@Override
	public void setValue(Object value) {
		/*
		 * Transfers the new matrix values to the editor
		 */
		if (value instanceof float[][]) {
			float[][] matrix = (float[][]) value;

			if (matrix[0].length == couplingMatrix.getFromSize()
					&& matrix.length == couplingMatrix.getToSize()) {

				for (int i = 0; i < matrix.length; i++) {

					for (int j = 0; j < matrix[i].length; j++) {
						editor.setValueAt(matrix[i][j], i, j);
					}
				}
			} else {
				Util
						.debugMsg("Termination weights not applied because they don't match dimensions");
			}
		}
	}
}
package ca.neo.ui.configurable.targets;

import ca.neo.math.Function;
import ca.neo.ui.configurable.ConfigParam;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.IConfigurable;
import ca.neo.ui.configurable.struct.PTFunction;

public class ConfigurableFunctionArray implements IConfigurable {

	private Function[] myFunctions;
	private int dimension;

	public ConfigurableFunctionArray(Function[] functions) {
		super();
		myFunctions = functions;
		init(functions.length);
	}

	private void init(int dimension) {
		this.dimension = dimension;
	}

	public ConfigurableFunctionArray(int dimension) {
		super();
		init(dimension);

	}

	public void completeConfiguration(ConfigParam properties) {
		myFunctions = new Function[dimension];
		for (int i = 0; i < dimension; i++) {
			myFunctions[i] = (Function) properties.getProperty("Function " + i);

		}

	}

	public ConfigParamDescriptor[] getConfigSchema() {
		ConfigParamDescriptor[] props = new ConfigParamDescriptor[dimension];

		for (int i = 0; i < dimension; i++) {
			Function defaultFunction = null;
			if (myFunctions != null) {
				defaultFunction = myFunctions[i];
			}
			PTFunction function = new PTFunction("Function " + i,
					defaultFunction);

			props[i] = function;
		}

		return props;
	}

	public Function[] getFunctions() {
		return myFunctions;
	}

	public String getTypeName() {
		return dimension + "x Functions";
	}
}

// class FunctionsInputPanel extends ConfigParamInputPanel {
//
// private static final long serialVersionUID = 1L;
// CouplingMatrixImpl couplingMatrix;
//
// MatrixEditor editor;
//
// int dimensions;
//
// public FunctionsInputPanel(ConfigParamDescriptor property, int dimensions) {
// super(property);
// this.fromSize = fromSize;
// this.toSize = toSize;
//
// couplingMatrix = new CouplingMatrixImpl(fromSize, toSize);
// editor = new MatrixEditor(couplingMatrix);
//
// addToPanel(editor);
// }
//
// @Override
// public float[][] getValue() {
// return couplingMatrix.getData();
// }
//
// @Override
// public void initPanel() {
// /*
// * do nothing here
// */
// }
//
// @Override
// public boolean isValueSet() {
// return true;
// }
//
// @Override
// public void setValue(Object value) {
// if (value instanceof float[][]) {
// float[][] matrix = (float[][]) value;
//
// for (int i = 0; i < matrix.length; i++) {
//
// for (int j = 0; j < matrix[i].length; j++) {
// editor.setValueAt(matrix[i][j], i, j);
// }
//
// }
//
// }
//
// }
//
// }
//
// class FunctionsProp extends ConfigParamDescriptor {
//
// private static final long serialVersionUID = 1L;
// int dimensions;
//
// public FunctionsProp(Function[] functions) {
// super(typeName, functions);
// init(functions.length);
// }
//
// private void init(int dimensions) {
// this.dimensions = dimensions;
// }
//
// public FunctionsProp(int dimensions) {
// super(typeName);
// init(dimensions);
// }
//
// @Override
// public ConfigParamInputPanel createInputPanel() {
// return new FunctionsInputPanel(this, dimensions);
// }
//
// @Override
// public Class<Function[]> getTypeClass() {
// return Function[].class;
// }
//
// static final String typeName = "Functions";
//
// @Override
// public String getTypeName() {
// return typeName;
// }
//
// }

/*
 * Created on 17-Dec-07
 */
package ca.neo.config.handlers;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import ca.neo.config.IconRegistry;
import ca.neo.config.ui.ConfigurationChangeListener;
import ca.neo.config.ui.MatrixEditor;
import ca.neo.util.MU.MatrixExpander;
import ca.neo.util.MU.VectorExpander;

public class MatrixHandler extends BaseHandler {
	
	public MatrixHandler() {
		super(float[][].class);
	}

	@Override
	public Component getEditor(Object o, ConfigurationChangeListener listener) {
		float[][] matrix = (float[][]) o;
		float[][] copy = new float[matrix.length][];
		for (int i = 0; i < matrix.length; i++) {
			copy[i] = new float[matrix[i].length];
			System.arraycopy(matrix[i], 0, copy[i], 0, matrix[i].length);
		}
		final MatrixEditor me = new MatrixEditor(copy, false, false);
		me.setPreferredSize(new Dimension(300, 150));
		JButton okButton = new JButton("OK");
		me.getControlPanel().add(okButton);
		
		listener.setProxy(new ConfigurationChangeListener.EditorProxy() {
			public Object getValue() {
				return me.getMatrix();
			}
		});
		okButton.addActionListener(listener);
		return me;
	}

	@Override
	public Component getRenderer(Object o) {
		JPanel result = new JPanel(new FlowLayout());
		
		float[][] matrix = (float[][]) o;
		String text = toString(matrix, '\t', "\r\n");
		result.add(new JLabel(IconRegistry.getInstance().getIcon(float[][].class)));
		result.add(new JTextArea(text));

		return result;
	}
	
	@Override
	public Object fromString(String s) {
		return fromString(s, ',', "\r\n");
//		MatrixExpander result = new MatrixExpander();
//		BufferedReader reader = new BufferedReader(new StringReader(s));
//		String line = null;
//		try {
//			while ((line = reader.readLine()) != null) {
//				StringTokenizer tok = new StringTokenizer(line, ",", false);
//				VectorExpander row = new VectorExpander();
//				while (tok.hasMoreTokens()) {
//					row.add(Float.parseFloat(tok.nextToken()));
//				}
//				result.add(row.toArray());
//			}
//		} catch (NumberFormatException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return result.toArray();
	}

	@Override
	public String toString(Object o) {
		return toString((float[][]) o, ',', "\r\n");
	}
	
	public static float[][] fromString(String s, char colDelim, String rowDelim) {
		String colDelimString = String.valueOf(colDelim);
		MatrixExpander result = new MatrixExpander();
		
		StringTokenizer rowTokenizer = new StringTokenizer(s, rowDelim, false);
		while (rowTokenizer.hasMoreTokens()) {
			StringTokenizer elemTokenizer = new StringTokenizer(rowTokenizer.nextToken(), colDelimString, false);
			VectorExpander row = new VectorExpander();
			while (elemTokenizer.hasMoreTokens()) {
				row.add(Float.parseFloat(elemTokenizer.nextToken()));
			}
			result.add(row.toArray());
		}
//		BufferedReader reader = new BufferedReader(new StringReader(s));
//		String line = null;
//		try {
//			while ((line = reader.readLine()) != null) {
//				StringTokenizer tok = new StringTokenizer(line, ",", false);
//				VectorExpander row = new VectorExpander();
//				while (tok.hasMoreTokens()) {
//					row.add(Float.parseFloat(tok.nextToken()));
//				}
//				result.add(row.toArray());
//			}
//		} catch (NumberFormatException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		return result.toArray();		
	}
	
	public static String toString(float[][] matrix, char colDelim, String rowDelim) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				result.append(matrix[i][j]);
				if (j < matrix[i].length - 1) result.append(colDelim);
			}
			if (i < matrix.length - 1) result.append(rowDelim);
		}
		return result.toString();
	}

	/**
	 * @see ca.neo.config.ConfigurationHandler#getDefaultValue(java.lang.Class)
	 */
	public Object getDefaultValue(Class c) {
		return new float[0][];
	}

}

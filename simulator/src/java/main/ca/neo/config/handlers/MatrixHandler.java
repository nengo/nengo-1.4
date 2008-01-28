/*
 * Created on 17-Dec-07
 */
package ca.neo.config.handlers;

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

/**
 * ConfigurationHandler for float[][] values. 
 * 
 * @author Bryan Tripp
 */
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
		result.add(new JLabel(IconRegistry.getInstance().getIcon(o)));
		result.add(new JTextArea(text));

		return result;
	}
	
	@Override
	public Object fromString(String s) {
		return fromString(s, ',', "\r\n");
	}

	@Override
	public String toString(Object o) {
		return toString((float[][]) o, ',', "\r\n");
	}
	
	/**
	 * @param s A String representation of a matrix, eg from toString(float[][], char, String)  
	 * @param colDelim The character used to delimit matrix columns in this string
	 * @param rowDelim The string (can be >1 chars) used to delimit matrix rows in this string 
	 * @return The matrix represented by the string
	 */
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
		return result.toArray();		
	}

	/**
	 * @param matrix A matrix  
	 * @param colDelim A character to be used to delimit matrix columns
	 * @param rowDelim A String to be used to delimit matrix rows
	 * @return A String representation of the given matrix using the given delimiters 
	 */
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

/*
 * Created on 17-Dec-07
 */
package ca.neo.config.handlers;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import ca.neo.config.IconRegistry;
import ca.neo.config.ui.ConfigurationChangeListener;
import ca.neo.config.ui.MatrixEditor;

/**
 * ConfigurationHandler for float[] values. 
 * 
 * @author Bryan Tripp
 */
public class VectorHandler extends BaseHandler {

	public VectorHandler() {
		super(float[].class);
	}

	@Override
	public Component getEditor(Object o, ConfigurationChangeListener listener) {
		float[] vector = (float[]) o;
		float[] copy = new float[vector.length];
		System.arraycopy(vector, 0, copy, 0, vector.length);
		
		final MatrixEditor result = new MatrixEditor(new float[][]{copy}, true, false);
		result.setPreferredSize(new Dimension(300, 85));
		JButton okButton = new JButton("OK");
		result.getControlPanel().add(okButton);
		
		listener.setProxy(new ConfigurationChangeListener.EditorProxy() {
			public Object getValue() {
				return result.getMatrix()[0];
			}
		});
		okButton.addActionListener(listener);
		
		return result;
	}
	
	@Override
	public Component getRenderer(Object o) {
		JLabel result = new JLabel(MatrixHandler.toString(new float[][]{(float[]) o}, ' ', "\r\n"), 
				IconRegistry.getInstance().getIcon(o), SwingConstants.LEFT);
		result.setFont(result.getFont().deriveFont(Font.PLAIN));
		return result;
	}

	@Override
	public String toString(Object o) {
		return MatrixHandler.toString(new float[][]{(float[]) o}, ',', "\r\n");
	}
	
	@Override
	public Object fromString(String s) {
		return MatrixHandler.fromString(s, ',', "\r\n")[0];
	}	

	/**
	 * @see ca.neo.config.ConfigurationHandler#getDefaultValue(java.lang.Class)
	 */
	public Object getDefaultValue(Class c) {
		return new float[0];
	}

}

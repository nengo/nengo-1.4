/*
 * Created on 17-Dec-07
 */
package ca.neo.config.handlers;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import ca.neo.config.IconRegistry;
import ca.neo.config.EditorProxy;
import ca.neo.config.ui.ConfigurationChangeListener;
import ca.neo.config.ui.MatrixEditor;

public class VectorHandler extends BaseHandler {

	public VectorHandler() {
		super(float[].class);
	}

	@Override
	public Component getEditor(Object o, ConfigurationChangeListener listener) {
		float[] vector = (float[]) o;
		float[] copy = new float[vector.length];
		System.arraycopy(vector, 0, copy, 0, vector.length);
		
		final MatrixEditor result = new MatrixEditor(new float[][]{copy});
		result.setPreferredSize(new Dimension(200, 85));
		JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton okButton = new JButton("OK");
		controlPanel.add(okButton);
		result.add(controlPanel, BorderLayout.SOUTH);
		
		listener.setProxy(new EditorProxy() {
			public Object getValue() {
				return result.getMatrix()[0];
			}
		});
		okButton.addActionListener(listener);
		
		return result;
	}
	
	@Override
	public Component getRenderer(Object o) {
		return new JLabel(MatrixHandler.toString(new float[][]{(float[]) o}, '\t', "\r\n"), 
				IconRegistry.getInstance().getIcon(o), SwingConstants.LEFT);
	}

	@Override
	public String toString(Object o) {
		return MatrixHandler.toString(new float[][]{(float[]) o}, ',', "\r\n");
	}
	
	@Override
	public Object fromString(String s) {
		return MatrixHandler.fromString(s, ',', "\r\n")[0];
	}	

}

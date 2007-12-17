/*
 * Created on 12-Dec-07
 */
package ca.neo.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import ca.neo.config.ui.ConfigurationChangeListener;
import ca.neo.config.ui.ConfigurationTreeCellEditor;
import ca.neo.config.ui.MatrixEditor;
import ca.neo.model.Configurable;
import ca.neo.model.SimulationMode;
import ca.neo.model.Units;
import ca.neo.model.impl.MockConfigurable;
import ca.neo.model.impl.MockConfigurable.MockLittleConfigurable;

/**
 * 
 * @author Bryan Tripp
 */
public class ConfigurationConfiguration {

	private static ConfigurationConfiguration ourInstance;
	
	private List<Class> myIconClasses;
	private List<Icon> myIcons;
	
	public static ConfigurationConfiguration getInstance() {
		if (ourInstance == null) {
			ourInstance = new ConfigurationConfiguration();
			
			//TODO: move these somewhere configurable
			ourInstance.setIcon(Integer.class, "/ca/neo/config/integer_icon.GIF");
			ourInstance.setIcon(float[].class, "/ca/neo/config/float_array_icon.GIF");
			ourInstance.setIcon(float[][].class, "/ca/neo/config/matrix_icon.GIF");
			ourInstance.setIcon(String.class, "/ca/neo/config/string_icon.JPG");
		}
		
		return ourInstance;
	}
	
	private ConfigurationConfiguration() {
		myIconClasses = new ArrayList<Class>(10);
		myIcons = new ArrayList<Icon>(10);
	}
	
	public Icon getIcon(Object o) {
		return getIcon(o.getClass());
	}
	
	private Icon getIcon(Class c) {
		Icon result = null;
		for (int i = 0; result == null && i < myIconClasses.size(); i++) {
			if (myIconClasses.get(i).isAssignableFrom(c)) {
				result = myIcons.get(i);
			}
		}
		
		return result;
	}
	
	public void setIcon(Class c, Icon icon) {
		myIconClasses.add(c);
		myIcons.add(icon);
	}
	
	public void setIcon(Class c, String path) {
		myIconClasses.add(c);
		myIcons.add(createImageIcon(path, ""));		
	}
	
	private ImageIcon createImageIcon(String path, String description) {
	    java.net.URL imgURL = getClass().getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL, description);
	    } else {
	        return null;
	    }
	}
	
	public Component getRenderer(Object o) {
		Component result = MainHandler.getInstance().getRenderer(o);
//		if (o instanceof float[][]) {
//			float[][] f = (float[][]) o;
//			StringBuffer buf = new StringBuffer();
//			for (int i = 0; i < f.length; i++) {
//				for (int j = 0; j < f[i].length; j++) {
//					buf.append(Float.toString(f[i][j]));
//					if (j < f[i].length - 1) buf.append("\t");
//				}
//				if (i < f.length - 1) buf.append("\r\n");
//			}
//			JPanel panel = new JPanel(new FlowLayout());
//			panel.add(new JLabel(getIcon(float[][].class)));
//			panel.add(new JTextArea(buf.toString()));
//			result = panel;
//		}
		
		return result;
	}
	
	public Component getEditor(Class type, Object value, JTree tree, TreePath path) {
		System.out.println(value.getClass().getName());
		ConfigurationChangeListener listener = new ConfigurationChangeListener(tree, path);
		return MainHandler.getInstance().getEditor(value, listener);
//		Component result = null;
//		
//		if (Integer.class.isAssignableFrom(type) || Float.class.isAssignableFrom(type) || String.class.isAssignableFrom(type)) {
			
//			final JTextField tf = new JTextField(ConfigurationConfiguration.getInstance().getDisplayText(value));
//			if (tf.getPreferredSize().width < 20) 
//				tf.setPreferredSize(new Dimension(20, tf.getPreferredSize().height));
//			
//			ActionListener listener = null;
//			if (Integer.class.isAssignableFrom(type)) {
//				listener = new ConfigurationChangeListener(tree, path) {
//					public Object getValue() throws Exception {
//						return new Integer(tf.getText());						
//					}
//				};
//			} else if (Float.class.isAssignableFrom(type)) {
//				listener = new ConfigurationChangeListener(tree, path) {
//					public Object getValue() throws Exception {
//						return new Float(tf.getText());						
//					}
//				};
//			} else if (String.class.isAssignableFrom(type)) {
//				listener = new ConfigurationChangeListener(tree, path) {
//					public Object getValue() throws Exception {
//						return new String(tf.getText());						
//					}
//				};
//			}
//			tf.addActionListener(listener);
//			
//			result = tf;
//		} else if (Boolean.class.isAssignableFrom(type)) {
//			final JCheckBox cb = new JCheckBox("", ((Boolean) value).booleanValue());
//			final JButton button = new JButton("OK");
//			button.addActionListener(new ConfigurationChangeListener(tree, path) {
//				public Object getValue() throws Exception {
//					return new Boolean(cb.isSelected());
//				}
//			});
//			JPanel panel = new JPanel(new FlowLayout());
//			panel.setBackground(Color.WHITE);
//			panel.add(cb);
//			panel.add(button);
//			result = panel;
//			
//			cb.setEnabled(false);
//			button.setEnabled(false);
//			Thread thread = new Thread() {
//				public void run() {
//					try {
//						Thread.sleep(10);
//					} catch (InterruptedException e) {}
//					cb.setEnabled(true);
//					button.setEnabled(true);					
//				}
//			};
//			thread.start();
//		} else if (float[][].class.isAssignableFrom(type)) {
//			float[][] matrix = (float[][]) value;
//			float[][] copy = new float[matrix.length][];
//			for (int i = 0; i < matrix.length; i++) {
//				copy[i] = new float[matrix[i].length];
//				System.arraycopy(matrix[i], 0, copy[i], 0, matrix[i].length);
//			}
//			final MatrixEditor me = new MatrixEditor(copy);
//			me.setPreferredSize(new Dimension(200, 150));
//			JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//			JButton okButton = new JButton("OK");
//			controlPanel.add(okButton);
//			me.add(controlPanel, BorderLayout.SOUTH);
//			okButton.addActionListener(new ConfigurationChangeListener(tree, path) {
//				public Object getValue() throws Exception {
//					return me.getMatrix();
//				}				
//			});
//			result = me;
//		} else if (float[].class.isAssignableFrom(type)) {
//			float[] vector = (float[]) value;
//			float[] copy = new float[vector.length];
//			System.arraycopy(vector, 0, copy, 0, vector.length);
//			final MatrixEditor me = new MatrixEditor(new float[][]{copy});
//			me.setPreferredSize(new Dimension(200, 85));
//			JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//			JButton okButton = new JButton("OK");
//			controlPanel.add(okButton);
//			me.add(controlPanel, BorderLayout.SOUTH);
//			okButton.addActionListener(new ConfigurationChangeListener(tree, path) {
//				public Object getValue() throws Exception {
//					return me.getMatrix()[0];
//				}				
//			});
//			result = me;
//		} else if (SimulationMode.class.isAssignableFrom(type)) {
//			SimulationMode mode = (SimulationMode) value;
//			SimulationMode[] modes = new SimulationMode[]{
//					SimulationMode.DIRECT, 
//					SimulationMode.CONSTANT_RATE, 
//					SimulationMode.RATE, 
//					SimulationMode.APPROXIMATE, 
//					SimulationMode.DEFAULT, 
//					SimulationMode.PRECISE};
//			final JComboBox cb = new JComboBox(modes);
//			cb.setSelectedItem(mode);
//			cb.addActionListener(new ConfigurationChangeListener(tree, path) {
//				public Object getValue() throws Exception {
//					return cb.getSelectedItem();
//				}				
//			});
//			result = cb;
//		} else if (Units.class.isAssignableFrom(type)) {
//			Units unit = (Units) value;
//			Units[] units = new Units[]{
//					Units.ACU, 
//					Units.AVU, 
//					Units.M, 
//					Units.M_PER_S, 
//					Units.mV, 
//					Units.N, 
//					Units.Nm, 
//					Units.RAD, 
//					Units.RAD_PER_S, 
//					Units.S, 
//					Units.SPIKES, 
//					Units.SPIKES_PER_S, 
//					Units.uA, 
//					Units.uAcm2, 
//					Units.UNK 
//			};
//			final JComboBox cb = new JComboBox(units);
//			cb.setSelectedItem(unit);
//			cb.addActionListener(new ConfigurationChangeListener(tree, path) {
//				public Object getValue() throws Exception {
//					return cb.getSelectedItem();
//				}				
//			});
//			result = cb;
//		}
//		
//		return result;
	}
	
//	public String getDisplayText(Object o) {
//		return MainHandler.getInstance().toString(o);
//		String result = o.toString();
		
//		if (o instanceof float[]) {
//			float[] f = (float[]) o;
//			StringBuffer buf = new StringBuffer();
//			for (int i = 0; i < f.length; i++) {
//				buf.append(Float.toString(f[i]));
//				if (i < f.length - 1) buf.append(" ");
//			}
//			result = buf.toString();
//		}
		
//		return result;
//	}
	
	public List<Class> getImplementations(Class type) {
		List<Class> result = new ArrayList<Class>(10);
		if (Configurable.class.isAssignableFrom(type)) {
			result.add(MockConfigurable.class);
			result.add(MockConfigurable.MockChildConfigurable.class);
			result.add(MockLittleConfigurable.class);
		}
		return result;
	}
	
}

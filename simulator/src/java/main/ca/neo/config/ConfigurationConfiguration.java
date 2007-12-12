/*
 * Created on 12-Dec-07
 */
package ca.neo.config;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextArea;

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
		Component result = null;
		
		if (o instanceof float[][]) {
			float[][] f = (float[][]) o;
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < f.length; i++) {
				for (int j = 0; j < f[i].length; j++) {
					buf.append(Float.toString(f[i][j]));
					if (j < f[i].length - 1) buf.append("\t");
				}
				if (i < f.length - 1) buf.append("\r\n");
			}
			result = new JTextArea(buf.toString());
		}
		
		return result;
	}
	
	public Component getEditor(Object o) {
		return null;
	}
	
	public String getDisplayText(Object o) {
		String result = o.toString();
		
		if (o instanceof float[]) {
			float[] f = (float[]) o;
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < f.length; i++) {
				buf.append(Float.toString(f[i]));
				if (i < f.length - 1) buf.append(" ");
			}
			result = buf.toString();
//		} else if (o instanceof float[][]) {
//			float[][] f = (float[][]) o;
//			StringBuffer buf = new StringBuffer();
//			for (int i = 0; i < f.length; i++) {
//				for (int j = 0; j < f[i].length; j++) {
//					buf.append(Float.toString(f[i][j]));
//					if (j < f[i].length - 1) buf.append("\t");
//				}
//				if (i < f.length - 1) buf.append("\r\n");
//			}
//			result = buf.toString();
		}
		
		return result;
	}
	
}

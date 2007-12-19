/*
 * Created on 12-Dec-07
 */
package ca.neo.config;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * 
 * @author Bryan Tripp
 */
public class IconRegistry {

	private static IconRegistry ourInstance;
	
	private List<Class> myIconClasses;
	private List<Icon> myIcons;
	
	public static IconRegistry getInstance() {
		if (ourInstance == null) {
			ourInstance = new IconRegistry();
			
			//TODO: move these somewhere configurable
			ourInstance.setIcon(Integer.class, "/ca/neo/config/integer_icon.GIF");
			ourInstance.setIcon(float[].class, "/ca/neo/config/float_array_icon.GIF");
			ourInstance.setIcon(float[][].class, "/ca/neo/config/matrix_icon.GIF");
			ourInstance.setIcon(String.class, "/ca/neo/config/string_icon.JPG");
		}
		
		return ourInstance;
	}
	
	private IconRegistry() {
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
	
}

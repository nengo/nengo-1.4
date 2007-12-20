/*
 * Created on 12-Dec-07
 */
package ca.neo.config;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

/**
 * 
 * @author Bryan Tripp
 */
public class IconRegistry {

	private static Logger ourLogger = Logger.getLogger(IconRegistry.class);
	private static IconRegistry ourInstance;
	
	private List<Class> myIconClasses;
	private List<Icon> myIcons;
	
	public static IconRegistry getInstance() {
		if (ourInstance == null) {
			ourInstance = new IconRegistry();
			
			//TODO: move these somewhere configurable
			ourInstance.setIcon(Integer.class, "/ca/neo/config/ui/integer_icon.GIF");
			ourInstance.setIcon(float[].class, "/ca/neo/config/ui/float_array_icon.GIF");
			ourInstance.setIcon(float[][].class, "/ca/neo/config/ui/matrix_icon.GIF");
			ourInstance.setIcon(String.class, "/ca/neo/config/ui/string_icon.JPG");
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
		
		if (result == null) {
			result = new DefaultIcon();
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
		ImageIcon result = null;
	    java.net.URL imgURL = getClass().getResource(path);
	    if (imgURL != null) {
	        result = new ImageIcon(imgURL, description);
	    } else {
	        ourLogger.warn("Can't load icon from " + path);
	    }
	    
	    return result;
	}
	
	private static class DefaultIcon implements Icon {

		public int getIconHeight() {
			return 16;
		}

		public int getIconWidth() {
			return 16;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			Polygon p = new Polygon(new int[]{x+3, x+3, x+13, x+13}, new int[]{y+3, y+13, y+13, y+3}, 4);
			g.drawPolygon(p);
//			g.fillPolygon(p);			
		}
		
	}
	
}

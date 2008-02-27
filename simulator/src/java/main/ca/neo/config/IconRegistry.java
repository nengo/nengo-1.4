/*
 * Created on 12-Dec-07
 */
package ca.neo.config;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import ca.neo.dynamics.DynamicalSystem;
import ca.neo.dynamics.Integrator;
import ca.neo.model.Ensemble;
import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.Noise;
import ca.neo.model.Origin;
import ca.neo.model.SimulationMode;
import ca.neo.model.Termination;
import ca.neo.model.neuron.Neuron;
import ca.neo.model.neuron.SpikeGenerator;
import ca.neo.model.neuron.SynapticIntegrator;


/**
 * A registry of graphical Icons that can be used for displaying Property values.
 *  
 * @author Bryan Tripp
 */
public class IconRegistry {

	private static Logger ourLogger = Logger.getLogger(IconRegistry.class);
	private static IconRegistry ourInstance;
	
	private List<Class> myIconClasses;
	private List<Icon> myIcons;
	
	/**
	 * @return Singleton instance
	 */
	public static IconRegistry getInstance() {
		if (ourInstance == null) {
			ourInstance = new IconRegistry();
			
			//TODO: move these somewhere configurable
			ourInstance.setIcon(Property.class, new Icon(){
				public void paintIcon(Component c, Graphics g, int x, int y) {
					g.drawPolygon(new int[]{8, 13, 8, 3}, new int[]{3, 8, 13, 8}, 4);
				}
				public int getIconWidth() {
					return 16;
				}
				public int getIconHeight() {
					return 16;
				}
			});
			ourInstance.setIcon(Boolean.class, "/ca/neo/config/ui/boolean_icon.GIF");
			ourInstance.setIcon(Boolean.TYPE, "/ca/neo/config/ui/boolean_icon.GIF");
			ourInstance.setIcon(Integer.class, "/ca/neo/config/ui/integer_icon.GIF");
			ourInstance.setIcon(Integer.TYPE, "/ca/neo/config/ui/integer_icon.GIF");
			ourInstance.setIcon(Float.class, "/ca/neo/config/ui/float_icon.GIF");
			ourInstance.setIcon(Float.TYPE, "/ca/neo/config/ui/float_icon.GIF");
			ourInstance.setIcon(float[].class, "/ca/neo/config/ui/float_array_icon.GIF");
			ourInstance.setIcon(float[][].class, "/ca/neo/config/ui/matrix_icon.GIF");
			ourInstance.setIcon(String.class, "/ca/neo/config/ui/string_icon.JPG");

			ourInstance.setIcon(DynamicalSystem.class, "/ca/neo/config/ui/dynamicalsystem02.jpg");
			ourInstance.setIcon(Integrator.class, "/ca/neo/config/ui/integrator.jpg");
			ourInstance.setIcon(Noise.class, "/ca/neo/config/ui/noise01.jpg");
			ourInstance.setIcon(Origin.class, "/ca/neo/config/ui/origin01.jpg");
			ourInstance.setIcon(SimulationMode.class, "/ca/neo/config/ui/simulationmode.jpg");
			ourInstance.setIcon(SpikeGenerator.class, "/ca/neo/config/ui/spikegenerator.jpg");
			ourInstance.setIcon(Termination.class, "/ca/neo/config/ui/termination.jpg");		
			
			ourInstance.setIcon(Neuron.class, "/ca/neo/config/ui/neuron.jpg");		
			ourInstance.setIcon(Network.class, "/ca/neo/config/ui/network.jpg");		
			ourInstance.setIcon(Ensemble.class, "/ca/neo/config/ui/ensemble.jpg");		
			ourInstance.setIcon(Node.class, "/ca/neo/config/ui/node.jpg");		
			ourInstance.setIcon(SynapticIntegrator.class, "/ca/neo/config/ui/synintegrator2.JPG");		
		}
		
		return ourInstance;
	}
	
	private IconRegistry() {
		myIconClasses = new ArrayList<Class>(10);
		myIcons = new ArrayList<Icon>(10);
	}
	
	/**
	 * @param o An object 
	 * @return An icon to use in displaying the given object
	 */
	public Icon getIcon(Object o) {
		return (o == null) ? null : getIcon(o.getClass());
	}
	
	/**
	 * @param c Class of object
	 * @return An icon to use in displaying objects of the given class
	 */
	public Icon getIcon(Class c) {
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
	
	/**
	 * @param c A class
	 * @param icon An Icon to use for objects of the given class 
	 */
	public void setIcon(Class c, Icon icon) {
		myIconClasses.add(c);
		myIcons.add(icon);
	}
	
	/**
	 * @param c A class
	 * @param path Path to an image file from which to make an Icon for objects of the 
	 * 		given class
	 */
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
			g.setColor(Color.LIGHT_GRAY);
			g.drawOval(1, 1, 14, 14);
		}
		
	}
	
}

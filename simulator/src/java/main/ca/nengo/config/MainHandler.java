/*
 * Created on 17-Dec-07
 */
package ca.nengo.config;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import org.apache.log4j.Logger;

import ca.nengo.config.handlers.BooleanHandler;
import ca.nengo.config.handlers.EnumHandler;
import ca.nengo.config.handlers.FloatHandler;
import ca.nengo.config.handlers.IntegerHandler;
import ca.nengo.config.handlers.MatrixHandler;
import ca.nengo.config.handlers.StringHandler;
import ca.nengo.config.handlers.VectorHandler;
import ca.nengo.config.ui.ConfigurationChangeListener;
import ca.nengo.config.ui.ConfigurationTreeModel.NullValue;
import ca.nengo.model.SimulationMode;
import ca.nengo.model.Units;
import ca.nengo.model.neuron.impl.IzhikevichSpikeGenerator;

/**
 * A composite ConfigurationHandler which delegates to other underlying ConfigurationHandlers 
 * that can handle specific classes.  
 * 
 * @author Bryan Tripp
 */
public class MainHandler implements ConfigurationHandler {
	
	public static String HANDLERS_FILE_PROPERTY = "ca.nengo.config.handlers";
	
	private static Logger ourLogger = Logger.getLogger(ConfigurationHandler.class);
	private static MainHandler ourInstance;	
	
	private List<ConfigurationHandler> myHandlers;
	
	/**
	 * @return Singleton instance
	 */
	public static synchronized MainHandler getInstance() {
		if (ourInstance == null) {
			ourInstance = new MainHandler();
		}
		return ourInstance;
	}
	
	private MainHandler() {
		myHandlers = new ArrayList<ConfigurationHandler>(20);
		
		String fileName = System.getProperty(HANDLERS_FILE_PROPERTY, "handlers.txt");
		File file = new File(fileName);
		if (file.exists() && file.canRead()) {
			try {
				loadHandlers(file);
			} catch (IOException e) {
				String message = "Can't load handlers";
				ourLogger.error(message, e);
				throw new RuntimeException(message, e);
			}
		} else {
			ourLogger.warn("Can't open configuration handlers file " + fileName);
		}
		
		addHandler(new FloatHandler());
		addHandler(new StringHandler());
		addHandler(new IntegerHandler());
		addHandler(new BooleanHandler());
		addHandler(new VectorHandler());
		addHandler(new MatrixHandler());
		addHandler(new EnumHandler(SimulationMode.class, SimulationMode.DEFAULT));
		addHandler(new EnumHandler(Units.class, Units.UNK));
		addHandler(new EnumHandler(IzhikevichSpikeGenerator.Preset.class, IzhikevichSpikeGenerator.Preset.DEFAULT));
	}
	
	private void loadHandlers(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String line = null;
		while ((line = reader.readLine()) != null) {
			String className = line.trim();
			try {
				ConfigurationHandler h = (ConfigurationHandler) Class.forName(className).newInstance();
				addHandler(h);
			} catch (Exception e) {
				String message = "Can't create handler";
				ourLogger.error(message, e);
				throw new RuntimeException(message, e);
			}
		}
	}
	
	/**
	 * @param handler New handler to which the MainHandler can delegate 
	 */
	public void addHandler(ConfigurationHandler handler) {
		myHandlers.add(handler);
	}

	/**
	 * @see ca.nengo.config.ConfigurationHandler#canHandle(java.lang.Class)
	 */
	public boolean canHandle(Class c) {
		boolean result = false;
		for (int i = myHandlers.size()-1; i >= 0 && !result; i--) {
			if (myHandlers.get(i).canHandle(c)) result = true;
		}
		return result;
	}

	/**
	 * @param c The class of the object represented by s 
	 * @param s A String representation of an object
	 * @return x.fromString(s), where x is a handler appropriate for the class c  
	 */
	public Object fromString(Class c, String s) {
		Object result = null;
		
		ConfigurationHandler handler = getHandler(myHandlers, c);
		if (handler != null) result = handler.fromString(s);
		
		return result;
	}
	
	/**
	 * @see ca.nengo.config.ConfigurationHandler#fromString(java.lang.String)
	 */
	public Object fromString(String s) {
		return null;
	}

	/**
	 * @see ca.nengo.config.ConfigurationHandler#getEditor(Object, ConfigurationChangeListener)
	 */
	public Component getEditor(Object o, ConfigurationChangeListener listener) {
		Component result = null;
		
		Class c = o.getClass();
		ConfigurationHandler handler = getHandler(myHandlers, c);
		if (handler != null) result = handler.getEditor(o, listener);

		return result;
	}

	/**
	 * @see ca.nengo.config.ConfigurationHandler#getRenderer(java.lang.Object)
	 */
	public Component getRenderer(Object o) {
		Component result = null;

		if (o instanceof NullValue) result = new JLabel("EMPTY"); 
			
		Class c = o.getClass();
		ConfigurationHandler handler = getHandler(myHandlers, c);
		if (handler != null) result = handler.getRenderer(o);
		
		return result;
	}

	/**
	 * @see ca.nengo.config.ConfigurationHandler#toString(java.lang.Object)
	 */
	public String toString(Object o) {
		String result = null;
		
		Class c = o.getClass();
		ConfigurationHandler handler = getHandler(myHandlers, c);
		if (handler != null) result = handler.toString(o);

		return result;
	}

	/**
	 * @see ca.nengo.config.ConfigurationHandler#getDefaultValue(java.lang.Class)
	 */
	public Object getDefaultValue(Class c) {
		Object result = null;
		
		ConfigurationHandler handler = getHandler(myHandlers, c);
		if (handler != null) result = handler.getDefaultValue(c);
		
		return result;
	}
	
	//returns last handler that can handle given class 
	private static ConfigurationHandler getHandler(List<ConfigurationHandler> handlers, Class c) {
		ConfigurationHandler result = null;
		for (int i = handlers.size()-1; i >= 0 && result == null; i--) {
			if (handlers.get(i).canHandle(c)) {
				result = handlers.get(i);
			}
		}
		return result;
	}

}

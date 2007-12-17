/*
 * Created on 17-Dec-07
 */
package ca.neo.config;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ca.neo.config.handlers.BooleanHandler;
import ca.neo.config.handlers.FloatHandler;
import ca.neo.config.handlers.IntegerHandler;
import ca.neo.config.handlers.MatrixHandler;
import ca.neo.config.handlers.SimulationModeHandler;
import ca.neo.config.handlers.StringHandler;
import ca.neo.config.handlers.UnitsHandler;
import ca.neo.config.handlers.VectorHandler;
import ca.neo.config.ui.ConfigurationChangeListener;
import ca.neo.model.SimulationMode;

public class MainHandler implements ConfigurationHandler {
	
	public static String HANDLERS_FILE_PROPERTY = "ca.neo.config.handlers";
	
	private static Logger ourLogger = Logger.getLogger(ConfigurationHandler.class);
	private static MainHandler ourInstance;	
	
	private List<ConfigurationHandler> myHandlers;
	
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
		addHandler(new SimulationModeHandler());
		addHandler(new UnitsHandler());
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
	
	public void addHandler(ConfigurationHandler handler) {
		myHandlers.add(handler);
	}

	/**
	 * @see ca.neo.config.ConfigurationHandler#canHandle(java.lang.Class)
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
		for (int i = myHandlers.size()-1; i >= 0 && result == null; i--) {
			if (myHandlers.get(i).canHandle(c)) {
				result = myHandlers.get(i).fromString(s);
			}
		}
		return result;
	}
	
	/**
	 * @see ca.neo.config.ConfigurationHandler#fromString(java.lang.String)
	 */
	public Object fromString(String s) {
		return null;
	}

	/**
	 * @see ca.neo.config.ConfigurationHandler#getDisplayText(java.lang.Object)
	 */
//	public String getDisplayText(Object o) {
//		String result = null;
//		
//		Class c = o.getClass();
//		for (int i = myHandlers.size()-1; i >= 0 && result == null; i--) {
//			if (myHandlers.get(i).canHandle(c)) {
//				result = myHandlers.get(i).getDisplayText(o);
//			}
//		}
//		return result;
//	}

	/**
	 * @see ca.neo.config.ConfigurationHandler#getEditor(Object, ConfigurationChangeListener)
	 */
	public Component getEditor(Object o, ConfigurationChangeListener listener) {
		Component result = null;
		
		Class c = o.getClass();
		for (int i = myHandlers.size()-1; i >= 0 && result == null; i--) {
			if (myHandlers.get(i).canHandle(c)) {
				result = myHandlers.get(i).getEditor(o, listener);
			}
		}
		return result;
	}

	/**
	 * @see ca.neo.config.ConfigurationHandler#getRenderer(java.lang.Object)
	 */
	public Component getRenderer(Object o) {
		Component result = null;
		
		Class c = o.getClass();
		for (int i = myHandlers.size()-1; i >= 0 && result == null; i--) {
			if (myHandlers.get(i).canHandle(c)) {
				result = myHandlers.get(i).getRenderer(o);
			}
		}
		return result;
	}

	/**
	 * @see ca.neo.config.ConfigurationHandler#toString(java.lang.Object)
	 */
	public String toString(Object o) {
		String result = null;
		
		Class c = o.getClass();
		for (int i = myHandlers.size()-1; i >= 0 && result == null; i--) {
			if (myHandlers.get(i).canHandle(c)) {
				result = myHandlers.get(i).toString(o);
			}
		}
		return result;
	}

}

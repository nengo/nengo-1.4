/*
 * Created on 7-Dec-07
 */
package ca.neo.io;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;

import ca.neo.config.MainHandler;
import ca.neo.model.Configurable;
import ca.neo.model.Configuration;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.Configuration.Property;
import ca.neo.model.impl.ConfigurationImpl;
import ca.neo.util.MU;

/**
 * 
 * TODO: shorter names, name constants
 * 
 * @author Bryan Tripp
 */
public class ConfigurableIO {
	
	private static final String NAMESPACE = "http://www.nengo.ca";
	private static final String CONFIGURABLE = "configurable";
	private static final String PROPERTY = "property";
	private static final String NONCONFIGURABLE = "nonconfigurable";
	private static final String CLASS = "class";
	private static final String VALUE = "value";
	private static final String NAME = "name";
	
	
	private static XMLOutputFactory ourOutputFactory = XMLOutputFactory.newInstance();
	private static XMLInputFactory ourInputFactory = XMLInputFactory.newInstance();
	private static Logger ourLogger = Logger.getLogger(ConfigurableIO.class);

	public static void save(Configurable configurable, File file) throws IOException {
		try {
			FileWriter fw = new FileWriter(file);
			XMLStreamWriter writer = ourOutputFactory.createXMLStreamWriter(fw);
			
			writer.writeStartDocument("1.0");
			writeConfigurable(writer, configurable);
			writer.writeEndDocument();
			
			writer.flush();
			writer.close();
			fw.close();
		} catch (XMLStreamException e) {
			ourLogger.error("Can't save Configurable", e);
			throw new IOException("Can't save: " + e.getMessage());
		} catch (StructuralException e) {
			ourLogger.error("Can't save Configurable", e);
			throw new IOException("Can't save: " + e.getMessage());
		}
	}
	
	private static void writeConfigurable(XMLStreamWriter writer, Configurable configurable) throws XMLStreamException, StructuralException {
		writer.writeStartElement(/*NAMESPACE,*/ CONFIGURABLE);
		writer.writeAttribute(/*NAMESPACE,*/ CLASS, configurable.getClass().getName());
		
		Configuration configuration = configurable.getConfiguration();
		Iterator<String> propertyNames = configuration.getPropertyNames().iterator();
		while (propertyNames.hasNext()) {
			String name = propertyNames.next();
			writeProperty(writer, configuration.getProperty(name));
		}
		
		writer.writeEndElement();
	}
	
	private static void writeProperty(XMLStreamWriter writer, Property property) throws XMLStreamException, StructuralException {
		writer.writeStartElement(/*NAMESPACE,*/ PROPERTY);
		writer.writeAttribute(/*NAMESPACE,*/ NAME, property.getName());
		writer.writeAttribute(/*NAMESPACE,*/ CLASS, property.getType().getName());
		
		boolean isConfigurable = Configurable.class.isAssignableFrom(property.getType());
		System.out.println(property.getType().getName() + " configurable " + isConfigurable);
		for (int i = 0; i < property.getNumValues(); i++) {
			Object o = property.getValue(i);
			if (isConfigurable) {
				writeConfigurable(writer, (Configurable) o); 
			} else {
				writeNonConfigurable(writer, o);
			}			
		}
		
		writer.writeEndElement();
	}
	
	private static void writeNonConfigurable(XMLStreamWriter writer, Object o) throws XMLStreamException, StructuralException {
		writer.writeStartElement(/*NAMESPACE,*/ NONCONFIGURABLE);
		writer.writeAttribute(/*NAMESPACE,*/ CLASS, o.getClass().getName());
		
		String text = MainHandler.getInstance().toString(o);
		boolean containsWhitespace = text.matches("\\s");
		if (containsWhitespace || text.length() > 50) {
			writer.writeCharacters(text);
		} else {
			writer.writeAttribute(VALUE, text);
		}
		
//		if (o instanceof Integer 
//			|| o instanceof Float 
//			|| o instanceof Boolean 
//			|| o instanceof String 
//			|| o instanceof SimulationMode 
//			|| o instanceof Units)
//		{			
//			writer.writeAttribute(/*NAMESPACE,*/ VALUE, o.toString());
//		} else {
//			float[][] data = null;
//			if (o instanceof float[][]) {
//				data = (float[][]) o;  
//			} else if (o instanceof float[]) {
//				data = new float[][]{(float[]) o};
//			} else {
//				throw new StructuralException("Unexpected parameter type: " + o.getClass().getName());
//			}
//			writer.writeCharacters(MU.toString(data, 5)); //TODO: better performance and accuracy possible
//		}
		
		writer.writeEndElement();
	}
	
	
	
	public static Configurable load(File file) throws IOException, StructuralException {
		Configurable result = null;
		
		FileReader fr = new FileReader(file); 
		try {
			XMLStreamReader reader = ourInputFactory.createXMLStreamReader(fr);
			reader.nextTag();
			if (reader.getLocalName().equals(CONFIGURABLE)) {
				result = readConfigurable(reader);
			} else {
				throw new StructuralException("XML doesn't contain <configurable> as root element");
			}			
		} catch (XMLStreamException e) {
			ourLogger.error("Can't read Configurable", e);
			throw new IOException("Can't read: " + e.getMessage());
		}
		
		return result;
	}
	
	//assumes we start on a <configurable> tag
	private static Configurable readConfigurable(XMLStreamReader reader) throws StructuralException, IOException {
		String className = reader.getAttributeValue(0); //TODO: use name (namespace needed)
		Map<String, Property> properties = new HashMap<String, Property>(10);
		
		try {
			readContents : while (reader.hasNext()) {
				int eventType = reader.next();
				if (eventType == XMLStreamReader.END_ELEMENT && reader.getName().equals(CONFIGURABLE)) {
					break readContents;
				} else if (eventType == XMLStreamReader.START_ELEMENT && reader.getLocalName().equals(PROPERTY)) {
					Property property = readProperty(reader);
					properties.put(property.getName(), property);
				}				
			}
		} catch (XMLStreamException e) {
			ourLogger.error("Can't read Configurable", e);
			throw new IOException("Can't read: " + e.getMessage());
		}
		
		return createConfigurable(className, properties);
	}
	
	private static Configurable createConfigurable(String className, Map<String, Property> properties) throws StructuralException {
		Configurable result = null;
		try {
			Class c = Class.forName(className);
			Constructor<?>[] constructors = c.getConstructors();
			for (int i = 0; i < constructors.length; i++) { //look for Configuration arg constructor
				if (constructors[i].getParameterTypes().length == 1 
						&& Configuration.class.isAssignableFrom(constructors[i].getParameterTypes()[0])) {
					Method templateMethod = c.getMethod("getConstructionTemplate", new Class[0]);
					Configuration template = (Configuration) templateMethod.invoke(c, new Object[0]);
					setMutableProperties(template, properties);
					result = (Configurable) constructors[i].newInstance(new Object[]{template});
				}
			}
			for (int i = 0; result == null && i < constructors.length; i++) { //look for zero-arg constructor
				if (constructors[i].getParameterTypes().length == 0) {
					result = (Configurable) constructors[i].newInstance(new Object[0]);
				}
			}
			
			//set properties that can be set after construction
			setMutableProperties(result.getConfiguration(), properties);
			
		} catch (ClassNotFoundException e) {
			throw new StructuralException(e);
		} catch (SecurityException e) {
			throw new StructuralException(e);
		} catch (NoSuchMethodException e) {
			throw new StructuralException(e);
		} catch (IllegalArgumentException e) {
			throw new StructuralException(e);
		} catch (IllegalAccessException e) {
			throw new StructuralException(e);
		} catch (InvocationTargetException e) {
			throw new StructuralException(e);
		} catch (InstantiationException e) {
			throw new StructuralException(e);
		}
		
		return result;
	}
	
	private static void setMutableProperties(Configuration configuration, Map<String, Property> properties) throws StructuralException {
		Iterator<String> propertyNames = configuration.getPropertyNames().iterator();
		while (propertyNames.hasNext()) {
			String name = propertyNames.next();
			Property source = properties.get(name);
			Property destination = configuration.getProperty(name);
			if (destination.isMutable()) {
				for (int i = 0; i < source.getNumValues(); i++) {
					if (i < destination.getNumValues()) {
						destination.setValue(i, source.getValue(i));
					} else {
						destination.addValue(source.getValue(i));
					}
				}
			}
		}
	}
	
	private static Property readProperty(XMLStreamReader reader) throws StructuralException, IOException {
		String name = reader.getAttributeValue(0);
		String className = reader.getAttributeValue(1);

		Class c;
		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new StructuralException("Can't load class " + className, e);
		}
		
		Property result = new ConfigurationImpl.TemplateProperty(null, name, c, true, false);
		try {
			readContents : while (reader.hasNext()) {
				int eventType = reader.next();
				if (eventType == XMLStreamReader.END_ELEMENT && reader.getName().equals(PROPERTY)) {
					break readContents;
				} else if (eventType == XMLStreamReader.START_ELEMENT && reader.getLocalName().equals(CONFIGURABLE)) {
					Configurable configurable = readConfigurable(reader);
					result.addValue(configurable);
				} else if (eventType == XMLStreamReader.START_ELEMENT && reader.getLocalName().equals(NONCONFIGURABLE)) {
					Object nonconfigurable = readNonConfigurable(reader);
					result.addValue(nonconfigurable);
				}
			}
		} catch (XMLStreamException e) {
			ourLogger.error("Can't read Configurable", e);
			throw new IOException("Can't read: " + e.getMessage());
		}

		return result;
	}
	
	private static Object readNonConfigurable(XMLStreamReader reader) throws StructuralException {
		String className = reader.getAttributeValue(0);
		Class<?> c;
		
		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new StructuralException("Can't load class " + className, e);
		}
		
		String value = null; 
		if (reader.getAttributeCount() > 1) {
			value = reader.getAttributeValue(1);
		} else {
			try {
				value = reader.getElementText();
			} catch (XMLStreamException e) {
				throw new StructuralException("Can't read element text", e);
			}
		}
		
		return MainHandler.getInstance().fromString(c, value);
	}
	
}

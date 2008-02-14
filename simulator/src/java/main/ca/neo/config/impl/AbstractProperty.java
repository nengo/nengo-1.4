/*
 * Created on 15-Jan-08
 */
package ca.neo.config.impl;

import java.lang.reflect.Method;

import ca.neo.config.Configuration;
import ca.neo.config.JavaSourceParser;
import ca.neo.config.Property;

/**
 * Base implementation of Property. 
 *  
 * @author Bryan Tripp
 */
public abstract class AbstractProperty implements Property {
		
	private Configuration myConfiguration;
	private String myName;
	private Class myClass;
	private boolean myMutable;
	private String myDocumentation;
	
	/**
	 * @param configuration Configuration to which the Property belongs
	 * @param name Name of the Property
	 * @param c Type of the Property
	 * @param mutable Whether the Property value(s) can be modified
	 */
	public AbstractProperty(Configuration configuration, String name, Class c, boolean mutable) {
		myConfiguration = configuration;
		myName = name;
		myClass = c;	
		myMutable = mutable;
	}
	
	/**
	 * @see ca.neo.config.Property#getName()
	 */
	public String getName() {
		return myName;
	}
	
	/**
	 * @see ca.neo.config.Property#getType()
	 */
	public Class getType() {
		return myClass;
	}

	/**
	 * @see ca.neo.config.Property#isMutable()
	 */
	public boolean isMutable() {
		return myMutable;
	}
	
	protected Configuration getConfiguration() {
		return myConfiguration;
	}

	/**
	 * @see ca.neo.config.Property#getDocumentation()
	 */
	public String getDocumentation() {
		return myDocumentation;
	}

	/**
	 * @param text New documentation text (can be plain text or HTML)
	 */
	public void setDocumentation(String text) {
		myDocumentation = text;
	}
	
	/**
	 * @param methods The methods that underlie this property
	 * @return A default documentation string composed of javadocs for these methods
	 */
	protected String getDefaultDocumentation(Method[] methods) {
		StringBuffer buf = new StringBuffer("<p><small>[Note: No documentation has been written specifically for the property <i>");
		buf.append(getName());  
		buf.append("</i>. Documentation for the API methods that support this property is shown below.]</small></p>");
		
		for (int i = 0; i < methods.length; i++) {
			appendDocs(buf, methods[i]);			
		}
		
		return buf.toString();
	}
	
	private static void appendDocs(StringBuffer buf, Method method) {
		if (method != null) {
			buf.append("<p><i>" + JavaSourceParser.getSignature(method) + "</i><br>");
			String docs = JavaSourceParser.getDocs(method); 
			if (docs != null) buf.append(docs);
			buf.append("</p>");
		}
	}

}
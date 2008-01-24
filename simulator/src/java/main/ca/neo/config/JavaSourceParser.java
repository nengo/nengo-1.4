/*
 * Created on 13-Nov-07
 */
package ca.neo.config;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import ca.neo.model.Network;
import ca.neo.model.impl.BasicOrigin;
import ca.neo.model.impl.NetworkImpl;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

/**
 * Utility for extracting data from Java source code files, including 
 * variable names and documentation.  
 *  
 * @author Bryan Tripp
 */
public class JavaSourceParser {
	
	private static JavaDocBuilder ourBuilder;
	
	static {
		ourBuilder = new JavaDocBuilder();
		addSource(new File("src/java/main"));
	}
	
	/**
	 * Adds source code under the given directory to the database. 
	 * 
	 * @param baseDir Root directory of source code
	 */
	public static void addSource(File baseDir) {
		ourBuilder.addSourceTree(baseDir);
	}
	
	/**
	 * @param c A Java class
	 * @return Class-level documentation if available, othewise null 
	 */
	public static String getDocs(Class c) {
		JavaClass jc = ourBuilder.getClassByName(c.getName());
		JavaClass[] interfaces = jc.getImplementedInterfaces();
		
		StringBuffer docs = new StringBuffer(jc.getName());
		if (c.getSuperclass() != Object.class) {
			docs.append(" extends ");
			docs.append(c.getSuperclass().getSimpleName());
		} 
		
		if (interfaces.length > 0) {
			docs.append(" implements ");
		}
		
		for (int i = 0; i < interfaces.length; i++) {
			docs.append(interfaces[i].getName());
			if (i < interfaces.length - 1) docs.append(", ");
		}		
		docs.append(":\r\n");
		
		docs.append(jc.getComment());		
		for (int i = 0; i < interfaces.length; i++) {
			docs.append("\r\n\r\n" + interfaces[i].getFullyQualifiedName() + ":\r\n");
			docs.append(interfaces[i].getComment());
		}
		
		return docs.toString();
	}
	
	/**
	 * @param m A Java method
	 * @return Method-level documentation if available, otherwise null
	 */
	public static String getDocs(Method m) {
		JavaMethod jm = getJavaMethod(m);
		return jm.getComment() + getTagText(jm);
	}
	
	//returns concatenated text of doc tag names and values
	private static String getTagText(AbstractJavaEntity entity) {
		StringBuffer result = new StringBuffer();
		
		DocletTag[] tags = entity.getTags();
		for (int i = 0; i < tags.length; i++) {
			result.append(tags[i].getName());
			result.append(": ");
			result.append(tags[i].getValue());
		}
		
		return result.toString();
	}
	
	/**
	 * @param m A Java method
	 * @return Names of method arguments if available, otherwise the default {"arg0", "arg1", ...}
	 */
	public static String[] getArgNames(Method m) {
		String[] result = new String[m.getParameterTypes().length];
		
		JavaMethod jm = getJavaMethod(m);
		for (int i = 0; i < result.length; i++) {
			result[i] = (jm == null) ? "arg"+i : jm.getParameters()[i].getName();
		}
		
		return result;
	}
	
	public static String[] getArgNames(Constructor c) {
		String[] result = new String[c.getParameterTypes().length];
		
		JavaMethod jm = getJavaMethod(c);
		System.out.println("jm: " + jm);
		for (int i = 0; i < result.length; i++) {
			result[i] = (jm == null) ? "arg"+i : jm.getParameters()[i].getName();
		}
		
		return result;		
	}

	/**
	 * @param m A Java method 
	 * @param arg Index of an argument on this method
	 * @return Argument documentation if available, otherwise null
	 */
	public static String getArgDocs(Method m, int arg) {
		return getArgDocs(getJavaMethod(m), arg);
	}
	
	private static String getArgDocs(JavaMethod jm, int arg) {
		String result = null;
		
		if (jm != null && jm.getParameters().length > arg) {
			String argName = jm.getParameters()[arg].getName();
			DocletTag[] tags = jm.getTags();
			for (int i = 0; i < tags.length && result == null; i++) {
				if (tags[i].getName().equals("param") && tags[i].getValue().startsWith(argName)) {
					result = tags[i].getValue().substring(argName.length()).trim();
				}
			}
		}
		
		return result;
	}
	
	//returns source wrapper for given method or null
	private static JavaMethod getJavaMethod(Method m) {
		return getJavaMethod(m.getDeclaringClass().getName(), m.getName(), m.getParameterTypes());
//		JavaMethod result = null;
//		
//		JavaClass sourceClass = ourBuilder.getClassByName(m.getDeclaringClass().getName());
//		JavaMethod[] sourceMethods = sourceClass.getMethods();
//		
//		for (int i = 0; i < sourceMethods.length && result == null; i++) {
//			JavaParameter[] sourceParams = sourceMethods[i].getParameters();
//			if (sourceMethods[i].getName().equals(m.getName()) && sourceParams.length == m.getParameterTypes().length) {
//				boolean matches = true;
//				for (int j = 0; j < sourceParams.length && matches; j++) {
//					String sourceParamTypeName = sourceParams[j].getType().getJavaClass().getFullyQualifiedName();										
//					if (!sourceParamTypeName.equals(m.getParameterTypes()[j].getName())) {
//						matches = false;
//					}
//				}	
//				
//				if (matches) {
//					result = sourceMethods[i];
//				}
//			}
//		}
//		
//		return result;
	}
	
	private static JavaMethod getJavaMethod(Constructor c) {
		return getJavaMethod(c.getDeclaringClass().getName(), c.getDeclaringClass().getSimpleName(), c.getParameterTypes());
	}
	
	private static JavaMethod getJavaMethod(String className, String methodName, Class[] paramTypes) {
		JavaMethod result = null;
		
		JavaClass sourceClass = ourBuilder.getClassByName(className);
		JavaMethod[] sourceMethods = sourceClass.getMethods();
		
		for (int i = 0; i < sourceMethods.length && result == null; i++) {
			JavaParameter[] sourceParams = sourceMethods[i].getParameters();
			if (sourceMethods[i].getName().equals(methodName) && sourceParams.length == paramTypes.length) {
				boolean matches = true;
				for (int j = 0; j < sourceParams.length && matches; j++) {
					String sourceParamTypeName = sourceParams[j].getType().getJavaClass().getFullyQualifiedName();										
					if (!sourceParamTypeName.equals(paramTypes[j].getName())) {
						matches = false;
					}
				}	
				
				if (matches) {
					result = sourceMethods[i];
				}
			}
		}
		
		return result;
	}
	
	/**
	 * @param html Some text 
	 * @return The same text with HTML tags removed
	 */
	public static String removeTags(String html) {
		return html.replaceAll("<.+>", ""); 
	}
	
	public static void main(String[] args) {
//		addSource(new File("src/java/main"));
		
		Network n = new NetworkImpl();
//		System.out.println(getClassDocs(n.getClass()));
		int num = 2;
//		System.out.println(n.getClass().getMethods()[num].getName());
//		System.out.println(getDocs(n.getClass().getMethods()[num]));	
//		
		String[] argNames = getArgNames(BasicOrigin.class.getConstructors()[0]);
		for (int i = 0; i < argNames.length; i++) {
			System.out.println(argNames[i]);
		}
		
//		System.out.println(getTagText(getJavaMethod(n.getClass().getMethods()[num])));
//		System.out.println(getArgDocs(n.getClass().getMethods()[num], 0));
	}

}

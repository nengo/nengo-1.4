package ca.neo.ui.util;

import java.lang.reflect.*;

public class ReflectNeo {
	
	public static void main(String[] args) {
		getNeoNodes();
	}
	
	public static void getNeoNodes() {
		Package pack = Package.getPackage("java.lang");
		
		
		try {
			Class c = Class.forName("ca.neo.model.impl.AbstractNode");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
}

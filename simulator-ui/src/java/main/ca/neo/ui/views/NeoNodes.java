package ca.neo.ui.views;

import ca.neo.model.impl.*;

public enum NeoNodes {
	NETWORK(NetworkImpl.class, "Network"),
	FUNCTION_INPUT(FunctionInput.class, "Function Input");
	
	Class classType;
	String name;
	private NeoNodes(Class classType, String name) {
		this.classType = classType;
		this.name = name;
	}
	
}

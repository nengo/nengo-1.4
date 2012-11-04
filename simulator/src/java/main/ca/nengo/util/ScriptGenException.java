package ca.nengo.util;

import ca.nengo.NengoException;

public class ScriptGenException extends NengoException{

	public ScriptGenException(String message) {
		super(message);
	}

	public ScriptGenException(Throwable cause) {
		super(cause);
	}

	
	public ScriptGenException(String message, Throwable cause) {
		super(message, cause);
	}

}

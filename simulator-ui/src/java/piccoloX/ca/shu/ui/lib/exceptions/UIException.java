package ca.shu.ui.lib.exceptions;

public class UIException extends Exception {

	private static final long serialVersionUID = 1L;

	public UIException() {
		super();
	}

	public UIException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public UIException(String arg0) {
		super(arg0);
	}

	public UIException(Throwable arg0) {
		super(arg0);
	}

}

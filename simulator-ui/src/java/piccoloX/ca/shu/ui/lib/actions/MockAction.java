package ca.shu.ui.lib.actions;

import java.security.InvalidParameterException;

public class MockAction extends StandardAction {

	public MockAction(String description) {
		super(description);
		setEnabled(false);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void action() throws ActionException {
		throw new InvalidParameterException("This action is not meant to be executed");
	}

}

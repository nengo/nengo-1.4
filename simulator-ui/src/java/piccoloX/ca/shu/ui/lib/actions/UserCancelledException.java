package ca.shu.ui.lib.actions;

import ca.shu.ui.lib.exceptions.ActionException;

public class UserCancelledException extends ActionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserCancelledException() {
		super("User cancelled operation", false);
		// TODO Auto-generated constructor stub
	}

}

package ca.shu.ui.lib.actions;

/**
 * Error in performing an action
 * 
 * @author Shu Wu
 * 
 */
public class ActionException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private boolean showWarning;

	public boolean showWarning() {
		return showWarning;
	}

	/**
	 * @param description
	 */
	public ActionException(String description) {
		this(description, true);
	}

	/**
	 * @param description
	 *            Description of the problem
	 * @param showWarningPopup
	 *            if true, a warning should be shown to the user
	 */
	public ActionException(String description, boolean showWarningPopup) {
		super(description);

		this.showWarning = showWarningPopup;

	}

}

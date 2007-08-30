package ca.shu.ui.lib.exceptions;


/**
 * Error in performing an action
 * 
 * @author Shu Wu
 * 
 */
public class ActionException extends UIException {
	private static final long serialVersionUID = 1L;

	private final boolean showWarning;

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

	public boolean showWarning() {
		return showWarning;
	}

}

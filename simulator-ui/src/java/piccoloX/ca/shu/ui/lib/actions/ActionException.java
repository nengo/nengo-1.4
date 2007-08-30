package ca.shu.ui.lib.actions;

import ca.shu.ui.lib.exceptions.UIException;
import ca.shu.ui.lib.util.Util;

/**
 * Exception thrown during an action
 * 
 * @author Shu Wu
 */
public class ActionException extends UIException {
	private static final long serialVersionUID = 1L;

	/**
	 * Whether an warning should be shown when the action is handled by defaults
	 */
	private final boolean showWarning;

	/**
	 * @param description
	 *            Description of the exception
	 */
	public ActionException(String description) {
		this(description, true);
	}

	@Override
	public void defaultHandleBehavior() {

		if (showWarning) {
			System.out.println("Action Exception: " + toString());
			Util.UserWarning(getMessage());

		} else
			System.out.println("Action Exception: " + toString());

	}

	/**
	 * @param description
	 *            Description of the exception
	 * @param showWarningPopup
	 *            If true, a warning should be shown to the user
	 */
	public ActionException(String description, boolean showWarningPopup) {
		super(description);

		this.showWarning = showWarningPopup;

	}

}

package ca.shu.ui.lib.actions;

import ca.shu.ui.lib.AppFrame;

public class ExitAction extends StandardAction {
	private AppFrame appFrame;

	public ExitAction(AppFrame appFrame, String description) {
		super(description);
		this.appFrame = appFrame;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void action() throws ActionException {
		appFrame.exitAppFrame();
		appFrame.dispose();
	}

}

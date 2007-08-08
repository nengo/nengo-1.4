package ca.neo.ui.models.actions;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import ca.neo.ui.NeoGraphics;
import ca.neo.ui.models.nodes.PNodeContainer;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.objects.widgets.TrackedActivity;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.Util;

public class SaveNodeContainerAction extends StandardAction {
	PNodeContainer nodeContainer;

	public SaveNodeContainerAction(String description,
			PNodeContainer nodeContainer) {
		super("Save " + nodeContainer.getTypeName(), description);
		this.nodeContainer = nodeContainer;
	}

	private static final long serialVersionUID = 1L;
	File file;

	@Override
	protected void action() throws ActionException {

		int returnVal = NeoGraphics.FileChooser.showSaveDialog(UIEnvironment
				.getInstance());
		// NeoGraphics.FileChooser.setDefaul

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = NeoGraphics.FileChooser.getSelectedFile();

			TrackedActivity task = new TrackedActivity("Saving network") {

				@Override
				public void doActivity() {
					try {
						nodeContainer.saveModel(file);
					} catch (IOException e) {
						Util.Error("Could not save file: " + e.toString());
					} catch (OutOfMemoryError e) {
						Util
								.Error("Out of memory, please increase memory size: "
										+ e.toString());
					}
				}
			};
			task.startThread();

		} else {
			throw new UserCancelledException();
		}

	}
}

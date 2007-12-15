package ca.neo.ui.actions;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import ca.neo.ui.NeoGraphics;
import ca.neo.ui.models.UINeoNode;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.objects.activities.AbstractActivity;
import ca.shu.ui.lib.util.UserMessages;

/**
 * Saves a PNodeContainer
 * 
 * @author Shu Wu
 */
public class SaveNodeAction extends StandardAction {
	private static final long serialVersionUID = 1L;

	/**
	 * File to be saved to
	 */
	private File file;

	private UINeoNode nodeUI;

	/**
	 * @param nodeUI
	 *            Node to be saved
	 */
	public SaveNodeAction(UINeoNode nodeUI) {
		super("Save");

		this.nodeUI = nodeUI;
	}

	@Override
	protected void action() throws ActionException {
		int returnVal = JFileChooser.CANCEL_OPTION;

		NeoGraphics.FileChooser.setSelectedFile(new File(nodeUI.getFileName()));

		returnVal = NeoGraphics.FileChooser.showSaveDialog();

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = NeoGraphics.FileChooser.getSelectedFile();

			AbstractActivity task = new AbstractActivity("Saving model") {

				@Override
				public void doActivity() {
					try {

						nodeUI.saveModel(file);

					} catch (IOException e) {
						UserMessages.showError("Could not save file: "
								+ e.toString());
					} catch (OutOfMemoryError e) {
						UserMessages
								.showError("Out of memory, please increase memory size: "
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

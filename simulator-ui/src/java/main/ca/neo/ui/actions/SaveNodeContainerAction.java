package ca.neo.ui.actions;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import ca.neo.io.FileManager;
import ca.neo.model.Ensemble;
import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.ui.NeoGraphics;
import ca.neo.ui.models.nodes.UINodeContainer;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.objects.widgets.TrackedActivity;
import ca.shu.ui.lib.objects.widgets.TransientStatusMessage;
import ca.shu.ui.lib.util.Util;

/**
 * Saves a PNodeContainer
 * 
 * @author Shu Wu
 * 
 */
public class SaveNodeContainerAction extends StandardAction {
	private static final long serialVersionUID = 1L;

	/**
	 * @param model
	 *            Model to be saved
	 * @return Whether the specified model can be saved
	 */
	public static boolean canSave(Node model) {
		if (model instanceof Network) {
			return true;
		} else if (model instanceof Ensemble) {
			return true;
		}
		return false;

	}

	/**
	 * File to be saved to
	 */
	private File file;

	private UINodeContainer nodeUI;

	/**
	 * @param actionName
	 *            Name of the action
	 * @param nodeUI
	 *            Node to be saved
	 */
	public SaveNodeContainerAction(String actionName, UINodeContainer nodeUI) {
		super("Save " + nodeUI.getTypeName(), actionName);

		this.nodeUI = nodeUI;
	}

	/**
	 * @param model
	 *            Model to be saved
	 * @param file
	 *            File to be saved in
	 * @throws IOException
	 *             if model cannot be saved to file
	 */
	private void saveModel(Node model, File file) throws IOException {
		FileManager fm = new FileManager();
		if (!canSave(model)) {
			Util.UserError("Trying to save an unsupported model "
					+ nodeUI.getClass().getSimpleName());
			return;
		}

		if (model instanceof Network) {
			fm.save((Network) model, file);
		} else if (model instanceof Ensemble) {
			fm.save((Ensemble) model, file);
		}
		TransientStatusMessage.show("File saved!", 2500);
	}

	@Override
	protected void action() throws ActionException {
		int returnVal = JFileChooser.CANCEL_OPTION;
		Node model = nodeUI.getModel();

		NeoGraphics.FileChooser.setSelectedFile(new File(nodeUI.getFileName()));

		if (model instanceof Network) {
			returnVal = NeoGraphics.FileChooser.showSaveNetworkDialog();
		} else if (model instanceof Ensemble) {
			returnVal = NeoGraphics.FileChooser.showSaveEnsembleDialog();
		} else {
			throw new ActionException("Trying to save unsupported file type "
					+ nodeUI.getClass().getSimpleName());

		}

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = NeoGraphics.FileChooser.getSelectedFile();

			TrackedActivity task = new TrackedActivity("Saving model") {

				@Override
				public void doActivity() {
					try {
						nodeUI.saveContainerConfig();
						nodeUI.setFileName(file.toString());

						saveModel(nodeUI.getModel(), file);

					} catch (IOException e) {
						Util.UserError("Could not save file: " + e.toString());
					} catch (OutOfMemoryError e) {
						Util
								.UserError("Out of memory, please increase memory size: "
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

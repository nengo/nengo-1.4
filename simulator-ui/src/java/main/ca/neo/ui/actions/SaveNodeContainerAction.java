package ca.neo.ui.actions;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import ca.neo.io.FileManager;
import ca.neo.model.Ensemble;
import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.ui.NeoGraphics;
import ca.neo.ui.models.nodes.PNodeContainer;
import ca.neo.ui.util.NeoFileChooser;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.objects.widgets.TrackedActivity;
import ca.shu.ui.lib.objects.widgets.TransientStatusMessage;
import ca.shu.ui.lib.util.Util;

public class SaveNodeContainerAction extends StandardAction {
	private static final long serialVersionUID = 1L;

	File file;

	PNodeContainer nodeContainer;

	public SaveNodeContainerAction(String actionName,
			PNodeContainer nodeContainer) {
		super("Save " + nodeContainer.getTypeName(), actionName);

		this.nodeContainer = nodeContainer;
	}

	public NeoFileChooser getFileChooser() {
		return NeoGraphics.FileChooser;
	}

	public static boolean canSave(Node node) {
		if (node instanceof Network) {
			return true;
		} else if (node instanceof Ensemble) {
			return true;
		}
		return false;

	}

	public void saveModel(Node model, File file) throws IOException {
		FileManager fm = new FileManager();
		if (!canSave(model)) {
			Util.UserError("Trying to save an unsupported model "
					+ nodeContainer.getClass().getSimpleName());
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
		Node model = nodeContainer.getModel();

		getFileChooser().setSelectedFile(new File(nodeContainer.getFileName()));

		if (model instanceof Network) {
			returnVal = getFileChooser().showSaveNetworkDialog();
		} else if (model instanceof Ensemble) {
			returnVal = getFileChooser().showSaveEnsembleDialog();
		} else {
			throw new ActionException("Trying to save unsupported file type "
					+ nodeContainer.getClass().getSimpleName());

		}

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = getFileChooser().getSelectedFile();

			TrackedActivity task = new TrackedActivity("Saving model") {

				@Override
				public void doActivity() {
					try {
						nodeContainer.saveConfig();
						nodeContainer.setFileName(file.toString());

						saveModel(nodeContainer.getModel(), file);

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

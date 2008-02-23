package ca.neo.ui.actions;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import ca.neo.io.FileManager;
import ca.neo.model.Node;
import ca.neo.ui.NeoGraphics;
import ca.neo.ui.models.INodeContainer;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.activities.TrackedAction;
import ca.shu.ui.lib.util.UserMessages;

/**
 * Action used to open a Neo model from file
 * 
 * @author Shu Wu
 */
public class OpenNeoFileAction extends StandardAction {

	private static final long serialVersionUID = 1L;
	private File file;
	private INodeContainer nodeContainer;
	private Object objLoaded;

	/**
	 * @param actionName
	 *            Name of this action
	 * @param nodeContainer
	 *            Container to which the loaded model shall be added to
	 */
	public OpenNeoFileAction(INodeContainer nodeContainer) {
		super("Open from file");
		init(nodeContainer);
	}

	@Override
	protected void action() throws ActionException {
		int response = NeoGraphics.FileChooser.showOpenDialog();
		if (response == JFileChooser.APPROVE_OPTION) {
			file = NeoGraphics.FileChooser.getSelectedFile();

			TrackedAction loadActivity = new TrackedAction("Loading network") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void action() throws ActionException {
					FileManager fm = new FileManager();
					try {
						objLoaded = fm.load(file);

						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								if (objLoaded != null)
									processLoadedObject(objLoaded);
								objLoaded = null;

							}
						});

					} catch (IOException e) {
						UserMessages.showError("IO Exception loading file");
					} catch (ClassNotFoundException e) {
						UserMessages.showError("Class not found");
					} catch (ClassCastException e) {
						UserMessages.showError("Incorrect file version");
					} catch (OutOfMemoryError e) {
						UserMessages.showError("Out of memory loading file");
					} catch (Exception e) {
						UserMessages.showError("Unexpected exception loading file");
					}

		
				}

			};
			loadActivity.doAction();
		}

	}

	/**
	 * Initializes field variables
	 */
	private void init(INodeContainer nodeContainer) {
		this.nodeContainer = nodeContainer;
	}

	/**
	 * Wraps the loaded object and adds it to the Node Container
	 * 
	 * @param objLoaded
	 *            Loaded object
	 */
	private void processLoadedObject(Object objLoaded) {

		if (objLoaded instanceof Node) {
			nodeContainer.addNodeModel((Node) objLoaded);
		} else {
			UserMessages.showError("File does not contain a Node");
		}

	}
}

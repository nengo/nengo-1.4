package ca.neo.ui.actions;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import ca.neo.io.FileManager;
import ca.neo.model.Node;
import ca.neo.ui.NeoGraphics;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.UINeoNode;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.activities.AbstractActivity;
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

			AbstractActivity loadActivity = new AbstractActivity(
					"Loading network") {

				@Override
				public void doActivity() {
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
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (ClassCastException e) {
						e.printStackTrace();
					} finally {
						if (objLoaded == null) {
							UserMessages
									.showError("Incorrect file type or wrong version");
						}
					}

				}

			};
			loadActivity.startThread();
		}

	}

	/**
	 * Initializes field variables
	 */
	protected void init(INodeContainer nodeContainer) {
		this.nodeContainer = nodeContainer;
	}

	/**
	 * Wraps the loaded object and adds it to the Node Container
	 * 
	 * @param objLoaded
	 *            Loaded object
	 */
	protected void processLoadedObject(Object objLoaded) {

		if (objLoaded instanceof Node) {
			UINeoNode nodeUI = UINeoNode.createNodeUI((Node) objLoaded);
			nodeContainer.addNeoNode(nodeUI);
		} else {
			UserMessages.showError("File does not contain a Node");
		}

	}
}

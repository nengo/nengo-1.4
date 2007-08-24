package ca.neo.ui.actions;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import ca.neo.io.FileManager;
import ca.neo.model.Ensemble;
import ca.neo.model.Network;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.ui.NeoGraphics;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.nodes.PEnsemble;
import ca.neo.ui.models.nodes.PNEFEnsemble;
import ca.neo.ui.models.nodes.PNetwork;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.widgets.TrackedActivity;
import ca.shu.ui.lib.util.Util;

/**
 * Action used to open a Neo model from file
 * 
 * @author Shu Wu
 * 
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
	public OpenNeoFileAction(String actionName, INodeContainer nodeContainer) {
		super("Open model from file", actionName);
		init(nodeContainer);
	}

	/**
	 * Initializes field variables
	 */
	protected void init(INodeContainer nodeContainer) {
		this.nodeContainer = nodeContainer;
	}

	@Override
	protected void action() throws ActionException {
		int response = NeoGraphics.FileChooser.showOpenDialog();
		if (response == JFileChooser.APPROVE_OPTION) {
			file = NeoGraphics.FileChooser.getSelectedFile();

			TrackedActivity loadActivity = new TrackedActivity(
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
							Util
									.UserError("Incorrect file type or wrong version");
						}
					}

				}

			};
			loadActivity.startThread();
		}

	}

	/**
	 * Tries to open the loaded object as an Ensemble and wrap it with a UI
	 * object
	 * 
	 * @param objLoaded
	 * @return An Ensemble UI Wrapper around the loaded object
	 */
	protected PNeoNode openAsEnsemble(Object objLoaded) {

		PEnsemble ensembleUI = null;
		if (objLoaded instanceof Ensemble) {
			ensembleUI = new PEnsemble((Ensemble) objLoaded);
		} else if (objLoaded instanceof NEFEnsemble) {
			ensembleUI = new PNEFEnsemble((NEFEnsemble) objLoaded);
		}
		if (ensembleUI != null)
			return ensembleUI;
		else
			return null;
	}

	/**
	 * Tries to open the loaded object as an Network and wrap it with a UI
	 * object
	 * 
	 * @param objLoaded
	 * @return An Network UI Wrapper around the loaded object
	 */
	protected PNeoNode openAsNetwork(Object objLoaded) {
		if (objLoaded instanceof Network) {
			PNetwork networkUI = new PNetwork((Network) objLoaded);

			return networkUI;

		} else
			return null;

	}

	/**
	 * Wraps the loaded object and adds it to the Node Container
	 * @param objLoaded Loaded object
	 */
	protected void processLoadedObject(Object objLoaded) {
		PNeoNode node;
		node = openAsNetwork(objLoaded);
		if (node == null)
			node = openAsEnsemble(objLoaded);

		if (node != null) {
			nodeContainer.addNeoNode(node);
		} else {
			Util
					.UserError("Could not load Model (must be a Network / (NEF) Ensemble)");
		}

	}
}

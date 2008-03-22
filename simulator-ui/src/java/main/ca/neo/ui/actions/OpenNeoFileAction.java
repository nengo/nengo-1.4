package ca.neo.ui.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import org.python.core.PyClass;
import org.python.util.PythonInterpreter;
import org.python.util.PythonObjectInputStream;

import ca.neo.model.Node;
import ca.neo.ui.NengoGraphics;
import ca.neo.ui.models.NodeContainer;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.NodeContainer.ContainerException;
import ca.neo.ui.models.nodes.UINodeViewable;
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
	private NodeContainer nodeContainer;
	private Object objLoaded;

	/**
	 * @param actionName
	 *            Name of this action
	 * @param nodeContainer
	 *            Container to which the loaded model shall be added to
	 */
	public OpenNeoFileAction(NodeContainer nodeContainer) {
		super("Open from file");
		init(nodeContainer);
	}

	@Override
	protected void action() throws ActionException {
		int response = NengoGraphics.FileChooser.showOpenDialog();
		if (response == JFileChooser.APPROVE_OPTION) {
			file = NengoGraphics.FileChooser.getSelectedFile();

			TrackedAction loadActivity = new TrackedAction("Loading network") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void action() throws ActionException {
					try {
						// loading Python-based objects requires using a
						// PythonObjectInputStream from within a
						// PythonInterpreter.
						// loading sometimes fails if a new interpreter is
						// created, so
						// we use the one from the NengoGraphics.
						PythonInterpreter pi = NengoGraphics.getInstance().getPythonInterpreter();
						pi.set("___inStream",
								new PythonObjectInputStream(new FileInputStream(file)));
						org.python.core.PyObject obj = pi.eval("___inStream.readObject()");
						objLoaded = obj.__tojava__(Class.forName("ca.neo.model.Node"));
						pi.exec("del ___inStream");

						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								if (objLoaded != null) {
									try {
										processLoadedObject(objLoaded);
									} catch (ActionException e) {
										UserMessages.showWarning("Could not add node: "
												+ e.getMessage());
									}
								}
								objLoaded = null;

							}
						});

					} catch (IOException e) {
						UserMessages.showError("IO Exception loading file");
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						UserMessages.showError("Class not found");
					} catch (ClassCastException e) {
						UserMessages.showError("Incorrect file version");
					} catch (OutOfMemoryError e) {
						UserMessages.showError("Out of memory loading file");
					} catch (org.python.core.PyException e) {
						PyClass pyClass = (PyClass) e.type;
						String value = e.value.toString();
						if (pyClass.__name__.equals("ImportError")) {
							if (value.equals("no module named main")) {
								UserMessages.showError("Error: this file was "
										+ "built using Python class definitions that "
										+ "cannot be found.<br>To fix this problem, "
										+ "make a 'main.py' file in 'simulator-ui/lib/Lib' "
										+ "<br>and place the required python class definitions "
										+ "inside.");
							} else if (value.startsWith("no module named ")) {
								UserMessages.showError("Error: this file was "
										+ "built using Python class definitions in <br>a file "
										+ "named " + value.substring(16) + ", which"
										+ "cannot be found.<br>To fix this problem, please "
										+ "place this file in 'simulator-ui/lib/Lib'.");
							} else {
								UserMessages.showError("Python error interpretting file:<br>" + e);
							}
						} else if (pyClass.__name__.equals("AttributeError")) {
							String attr = value.substring(value.lastIndexOf(' ') + 1);
							UserMessages.showError("Error: this file uses a Python "
									+ "definition of the class " + attr + ", but this definition "
									+ "cannot be found.<br>If this class was defined in a "
									+ "separate .py file, please place this file in "
									+ "'simulator-ui/lib/Lib'.<br>Otherwise, please place the "
									+ "class definition in 'simulator-ui/lib/Lib/main.py' "
									+ "and restart the simulator.");
						} else {
							UserMessages.showError("Python error interpretting file:<br>" + e);
						}
					} catch (Exception e) {
						e.printStackTrace();
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
	private void init(NodeContainer nodeContainer) {
		this.nodeContainer = nodeContainer;
	}

	/**
	 * Wraps the loaded object and adds it to the Node Container
	 * 
	 * @param objLoaded
	 *            Loaded object
	 * @throws ActionException
	 */
	private void processLoadedObject(Object objLoaded) throws ActionException {

		if (objLoaded instanceof Node) {
			try {
				UINeoNode nodeUI = nodeContainer.addNodeModel((Node) objLoaded);
				if (nodeUI instanceof UINodeViewable) {
					((UINodeViewable) (nodeUI)).openViewer();
				}
			} catch (ContainerException e) {
				throw new ActionException(e);
			}
		} else {
			UserMessages.showError("File does not contain a Node");
		}

	}
}

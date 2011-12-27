/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "OpenNeoFileAction.java". Description:
"Action used to open a Neo model from file

  @author Shu Wu"

The Initial Developer of the Original Code is Bryan Tripp & Centre for Theoretical Neuroscience, University of Waterloo. Copyright (C) 2006-2008. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of the GNU
Public License license (the GPL License), in which case the provisions of GPL
License are applicable  instead of those above. If you wish to allow use of your
version of this file only under the terms of the GPL License and not to allow
others to use your version of this file under the MPL, indicate your decision
by deleting the provisions above and replace  them with the notice and other
provisions required by the GPL License.  If you do not delete the provisions above,
a recipient may use your version of this file under either the MPL or the GPL License.
 */

package ca.nengo.ui.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import org.python.core.PyClass;
import org.python.util.PythonInterpreter;
import org.python.util.PythonObjectInputStream;

import ca.nengo.model.Node;
import ca.nengo.ui.NengoGraphics;
import ca.nengo.ui.lib.actions.ActionException;
import ca.nengo.ui.lib.actions.StandardAction;
import ca.nengo.ui.lib.objects.activities.TrackedAction;
import ca.nengo.ui.lib.util.UserMessages;
import ca.nengo.ui.models.NodeContainer;
import ca.nengo.ui.models.NodeContainer.ContainerException;
import ca.nengo.ui.models.UINeoNode;
import ca.nengo.ui.models.nodes.UINodeViewable;

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

                    if (file.getName().endsWith(".py")) {
                        NengoGraphics.getInstance().getScriptConsole().addVariable("scriptname", file.getPath());
                        NengoGraphics.getInstance().getPythonInterpreter().execfile(file.getPath());
                        return;
                    }

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
                        objLoaded = obj.__tojava__(Class.forName("ca.nengo.model.Node"));
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

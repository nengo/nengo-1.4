.. java:import:: java.io File

.. java:import:: java.io FileInputStream

.. java:import:: java.io IOException

.. java:import:: javax.swing JFileChooser

.. java:import:: javax.swing SwingUtilities

.. java:import:: org.python.core PyClass

.. java:import:: org.python.util PythonInterpreter

.. java:import:: org.python.util PythonObjectInputStream

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.objects.activities TrackedAction

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.models NodeContainer

.. java:import:: ca.nengo.ui.models NodeContainer.ContainerException

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.nodes UINodeViewable

OpenNeoFileAction
=================

.. java:package:: ca.nengo.ui.actions
   :noindex:

.. java:type:: public class OpenNeoFileAction extends StandardAction

   Action used to open a Neo model from file

   :author: Shu Wu

Constructors
------------
OpenNeoFileAction
^^^^^^^^^^^^^^^^^

.. java:constructor:: public OpenNeoFileAction(NodeContainer nodeContainer)
   :outertype: OpenNeoFileAction

   :param nodeContainer: Container to which the loaded model shall be added to

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: OpenNeoFileAction


.. java:import:: java.io File

.. java:import:: java.io IOException

.. java:import:: javax.swing JFileChooser

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.actions UserCancelledException

.. java:import:: ca.nengo.ui.lib.objects.activities TrackedAction

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.models UINeoNode

GeneratePythonScriptAction
==========================

.. java:package:: ca.nengo.ui.actions
   :noindex:

.. java:type:: public class GeneratePythonScriptAction extends StandardAction

   Generates a script from a UINeoNode

   :author: Chris Eliasmith

Constructors
------------
GeneratePythonScriptAction
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public GeneratePythonScriptAction(UINeoNode nodeUI)
   :outertype: GeneratePythonScriptAction

   :param nodeUI: Node to be saved

GeneratePythonScriptAction
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public GeneratePythonScriptAction(UINeoNode nodeUI, boolean isBlocking)
   :outertype: GeneratePythonScriptAction

   :param nodeUI: Node to be saved

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: GeneratePythonScriptAction

getSaveSuccessful
^^^^^^^^^^^^^^^^^

.. java:method:: public boolean getSaveSuccessful()
   :outertype: GeneratePythonScriptAction

   :return: TODO


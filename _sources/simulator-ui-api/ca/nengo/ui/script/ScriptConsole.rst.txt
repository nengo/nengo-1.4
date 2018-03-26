.. java:import:: java.awt BorderLayout

.. java:import:: java.awt Color

.. java:import:: java.awt Insets

.. java:import:: java.awt.event ActionEvent

.. java:import:: java.awt.event FocusAdapter

.. java:import:: java.awt.event FocusEvent

.. java:import:: java.awt.event KeyEvent

.. java:import:: java.awt.event KeyListener

.. java:import:: java.awt.event WindowAdapter

.. java:import:: java.awt.event WindowEvent

.. java:import:: java.io File

.. java:import:: java.util ArrayList

.. java:import:: java.util.regex Pattern

.. java:import:: javax.swing Action

.. java:import:: javax.swing JEditorPane

.. java:import:: javax.swing JFrame

.. java:import:: javax.swing JPanel

.. java:import:: javax.swing JScrollPane

.. java:import:: javax.swing JSeparator

.. java:import:: javax.swing JTextArea

.. java:import:: javax.swing SwingUtilities

.. java:import:: javax.swing ToolTipManager

.. java:import:: javax.swing.text BadLocationException

.. java:import:: javax.swing.text Style

.. java:import:: javax.swing.text StyleConstants

.. java:import:: javax.swing.text StyleContext

.. java:import:: org.apache.log4j Logger

.. java:import:: org.python.core PyStringMap

.. java:import:: org.python.util PythonInterpreter

.. java:import:: ca.nengo.config JavaSourceParser

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.objects.activities TrackedAction

ScriptConsole
=============

.. java:package:: ca.nengo.ui.script
   :noindex:

.. java:type:: public class ScriptConsole extends JPanel

   A user interface panel for entering script commands. TODO: - talk to Terry re directory defaults (to use with execfile) - NO import defaults - DONE getting documentation help (see qdox) - DONE escape not working all the time? - DONE completion for arrays - DONE static method completion - DONE constructor completion

   :author: Bryan Tripp

Fields
------
COMMAND_STYLE
^^^^^^^^^^^^^

.. java:field:: public static final String COMMAND_STYLE
   :outertype: ScriptConsole

ERROR_STYLE
^^^^^^^^^^^

.. java:field:: public static final String ERROR_STYLE
   :outertype: ScriptConsole

HELP_STYLE
^^^^^^^^^^

.. java:field:: public static final String HELP_STYLE
   :outertype: ScriptConsole

OUTPUT_STYLE
^^^^^^^^^^^^

.. java:field:: public static final String OUTPUT_STYLE
   :outertype: ScriptConsole

Constructors
------------
ScriptConsole
^^^^^^^^^^^^^

.. java:constructor:: public ScriptConsole(PythonInterpreter interpreter)
   :outertype: ScriptConsole

   :param interpreter: The interpreter on which the console runs

Methods
-------
addVariable
^^^^^^^^^^^

.. java:method:: public void addVariable(String name, Object variable)
   :outertype: ScriptConsole

   :param name: Name of a new Python variable
   :param variable: Java object underlying the new variable

appendText
^^^^^^^^^^

.. java:method:: public void appendText(String text, String style)
   :outertype: ScriptConsole

   :param text: Text to append to display
   :param style: Name of text style (from class constants)

clearCommand
^^^^^^^^^^^^

.. java:method:: public void clearCommand()
   :outertype: ScriptConsole

   Clears the command field

completorDown
^^^^^^^^^^^^^

.. java:method:: public void completorDown()
   :outertype: ScriptConsole

   Moves down the command completor list

completorUp
^^^^^^^^^^^

.. java:method:: public void completorUp()
   :outertype: ScriptConsole

   Moves up the command completor list

enterCommand
^^^^^^^^^^^^

.. java:method:: public void enterCommand(String text)
   :outertype: ScriptConsole

   :param text: Processes the current command in the command field

getCallChain
^^^^^^^^^^^^

.. java:method:: public static String getCallChain(String command)
   :outertype: ScriptConsole

   :param command: A line of python code
   :return: The segment at the end of the command that looks like a partial call chain, eg for command "y.getY(x.get" this method would return "x.get"

getInCallChainCompletionMode
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean getInCallChainCompletionMode()
   :outertype: ScriptConsole

   :return: True if command completion is currently based on the call chain that the user is typing, false if completion is based on command history

getVariables
^^^^^^^^^^^^

.. java:method:: public String[] getVariables()
   :outertype: ScriptConsole

   Returns names of all variables in the local workspace.

hideToolTip
^^^^^^^^^^^

.. java:method:: public void hideToolTip()
   :outertype: ScriptConsole

main
^^^^

.. java:method:: public static void main(String[] args)
   :outertype: ScriptConsole

passKeyEvent
^^^^^^^^^^^^

.. java:method:: public void passKeyEvent(KeyEvent e)
   :outertype: ScriptConsole

removeVariable
^^^^^^^^^^^^^^

.. java:method:: public void removeVariable(String name)
   :outertype: ScriptConsole

   :param name: Name of python variable to delete

reset
^^^^^

.. java:method:: public void reset(boolean clearModules)
   :outertype: ScriptConsole

   Reset the script console back to initial conditions (remove all modules and variables added within the interpreter).

revertToTypedText
^^^^^^^^^^^^^^^^^

.. java:method:: public void revertToTypedText()
   :outertype: ScriptConsole

   Resets command field text to the last text typed by the user (as opposed to autocompleted text).

scrollToBottom
^^^^^^^^^^^^^^

.. java:method:: public void scrollToBottom()
   :outertype: ScriptConsole

setCurrentData
^^^^^^^^^^^^^^

.. java:method:: public void setCurrentData(Object o)
   :outertype: ScriptConsole

   :param o: The data that is currently selected in the UI.

setCurrentObject
^^^^^^^^^^^^^^^^

.. java:method:: public void setCurrentObject(Object o)
   :outertype: ScriptConsole

   :param o: The object that is currently selected in the UI.

setFocus
^^^^^^^^

.. java:method:: public void setFocus()
   :outertype: ScriptConsole

   Sets initial focus (should be called from UI thread)

setInCallChainCompletionMode
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setInCallChainCompletionMode(boolean inMode)
   :outertype: ScriptConsole

   :param inMode: Sets whether the console is in the mode of completing a call chain (otherwise it uses history completion)

setTypedText
^^^^^^^^^^^^

.. java:method:: public void setTypedText()
   :outertype: ScriptConsole

   Takes note of the text in the command field, as text that the user has typed (as opposed to recalled history). The two types must be distinguished, because we don't want an unselected history item to be used as the basis for subsequent history lookups.

showToolTip
^^^^^^^^^^^

.. java:method:: public void showToolTip()
   :outertype: ScriptConsole

withinString
^^^^^^^^^^^^

.. java:method:: public boolean withinString()
   :outertype: ScriptConsole

   :return: True iff the user is currently typing a string literal (ie is between single or double quotes)


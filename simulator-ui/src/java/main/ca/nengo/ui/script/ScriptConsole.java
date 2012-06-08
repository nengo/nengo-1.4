/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "ScriptConsole.java". Description:
"A user interface panel for entering script commands"

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

/*
 * Created on 5-Nov-07
 */
package ca.nengo.ui.script;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.apache.log4j.Logger;
import org.python.util.PythonInterpreter;
import org.python.core.PyFrame;
import org.python.core.PyException;
import org.python.core.PyObject;
import org.python.core.ThreadState;
import org.python.core.Py;
import org.python.core.TraceFunction;


import ca.nengo.config.JavaSourceParser;
import ca.nengo.ui.NengoGraphics;
import ca.nengo.ui.lib.Style.NengoStyle;
import ca.nengo.ui.lib.actions.ActionException;
import ca.nengo.ui.lib.objects.activities.TrackedAction;

/**
 * A user interface panel for entering script commands.
 *
 * TODO:
 * - talk to Terry re directory defaults (to use with execfile)
 * - NO import defaults
 * - DONE getting documentation help (see qdox)
 * - DONE escape not working all the time?
 * - DONE completion for arrays
 * - DONE static method completion
 * - DONE constructor completion
 *
 * @author Bryan Tripp
 */
public class ScriptConsole extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Logger ourLogger = Logger.getLogger(ScriptConsole.class);
	private static final String CURRENT_VARIABLE_NAME = "that";
	private static final String CURRENT_DATA_NAME = "data";


	public static final String COMMAND_STYLE = "command";
	public static final String OUTPUT_STYLE = "output";
	public static final String ERROR_STYLE = "error";
	public static final String HELP_STYLE = "help";

	private PythonInterpreter myInterpreter;
	private JEditorPane myDisplayArea;
	private JTextArea myCommandField;
	private HistoryCompletor myHistoryCompletor;
	private CallChainCompletor myCallChainCompletor;
	private boolean myInCallChainCompletionMode;
	private String myTypedText;
	private int myTypedCaretPosition;
	private StyleContext myStyleContext;
	private JSeparator seperator;
	private Style rootStyle;
	private Style commandStyle;
	private boolean myToolTipVisible;
	private int myDefaultDismissDelay;
	private long myLastHelpTime = 0;
	private Action myHideTip;

	/**
	 * @param interpreter
	 *            The interpreter on which the console runs
	 */
	public ScriptConsole(PythonInterpreter interpreter) {
		myInterpreter = interpreter;
		interpreter.execfile("python/startup_ui.py");

		myDisplayArea = new JEditorPane("text/html", "");
		myDisplayArea.setEditable(false);
		myDisplayArea.setMargin(new Insets(5, 5, 5, 5));

		myCommandField = new JTextArea();
		ToolTipManager.sharedInstance().registerComponent(myCommandField);

		setLayout(new BorderLayout());
		JScrollPane displayScroll = new JScrollPane(myDisplayArea);

		seperator = new JSeparator(JSeparator.HORIZONTAL);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(displayScroll, BorderLayout.CENTER);
		panel.add(seperator, BorderLayout.SOUTH);

		add(panel, BorderLayout.CENTER);
		add(myCommandField, BorderLayout.SOUTH);
		displayScroll.setBorder(null);

		myCommandField.addKeyListener(new CommandKeyListener(this));
		myCommandField.setFocusTraversalKeysEnabled(false);

		if(!NengoStyle.GTK) {myCommandField.setBorder(null);}

		myHistoryCompletor = new HistoryCompletor();
		myCallChainCompletor = new CallChainCompletor(myInterpreter);
		myInCallChainCompletionMode = false;
		myTypedText = "";
		myTypedCaretPosition = 0;

		interpreter.setOut(new ConsoleOutputWriter(this));
		
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				myCommandField.requestFocusInWindow();

			}
		});

		initStyles();
	}

	private void setChildrenBackground(Color fg) {
		myDisplayArea.setBackground(fg);
		if(!NengoStyle.GTK) {myCommandField.setBackground(fg);}
	}

	private void setChildrenForeground(Color fg) {
		myDisplayArea.setForeground(fg);
		if(!NengoStyle.GTK) {myCommandField.setForeground(fg);}
		seperator.setForeground(fg);
	}

	private void initStyles() {
		// Some are commented out because of inconsistencies with what
		// different platforms do with background and foreground colour.

		myStyleContext = new StyleContext();
		rootStyle = myStyleContext.addStyle("root", null);
		StyleConstants.setForeground(rootStyle, ca.nengo.ui.lib.Style.NengoStyle.COLOR_FOREGROUND);

		setChildrenBackground(ca.nengo.ui.lib.Style.NengoStyle.COLOR_BACKGROUND);
		setChildrenForeground(ca.nengo.ui.lib.Style.NengoStyle.COLOR_FOREGROUND);
		if(!NengoStyle.GTK) {myCommandField.setCaretColor(ca.nengo.ui.lib.Style.NengoStyle.COLOR_LIGHT_GREEN);}

		commandStyle = myStyleContext.addStyle(COMMAND_STYLE, rootStyle);
		StyleConstants.setForeground(commandStyle, ca.nengo.ui.lib.Style.NengoStyle.COLOR_FOREGROUND);
		StyleConstants.setItalic(commandStyle, true);
		Style outputStyle = myStyleContext.addStyle(OUTPUT_STYLE, rootStyle);
		StyleConstants.setForeground(outputStyle, Color.GRAY);
		Style errorStyle = myStyleContext.addStyle(ERROR_STYLE, rootStyle);
		StyleConstants.setForeground(errorStyle, Color.RED);

		Style helpStyle = myStyleContext.addStyle(HELP_STYLE, rootStyle);
		StyleConstants.setForeground(helpStyle, ca.nengo.ui.lib.Style.NengoStyle.COLOR_FOREGROUND);
	}

	/**
	 * @param name
	 *            Name of a new Python variable
	 * @param variable
	 *            Java object underlying the new variable
	 */
	public void addVariable(String name, Object variable) {
		myInterpreter.set(makePythonName(name), variable);
	}

	/**
	 * @param name
	 *            Name of python variable to delete
	 */
	public void removeVariable(String name) {
		name = makePythonName(name);
		if (myInterpreter.get(name) != null) {
			myInterpreter.exec("del " + name);
		}
	}

	private static String makePythonName(String name) {
		// replace special characters with "_"
		Pattern nonPythonChar = Pattern.compile("\\W");
		name = nonPythonChar.matcher(name).replaceAll("_");

		// prepend "_" if name starts with a number
		if (name.matches("\\A\\d.*")) {
			name = "_" + name;
		}

		// prepend "_" if name is reserved word
		String[] reserved = new String[] { "and", "assert", "break", "class", "continue", "def",
				"del", "elif", "else", "except", "exec", "finally", "for", "from", "global", "if",
				"import", "in", "is", "lambda", "not", "or", "pass", "print", "raise", "return",
				"try", "while", "yield" };

		for (int i = 0; i < reserved.length; i++) {
			if (name.equals(reserved[i])) {
				name = "_" + name;
			}
		}

		return name;
	}

	/**
	 * @param o
	 *            The object that is currently selected in the UI.
	 */
	public void setCurrentObject(Object o) {
		myInterpreter.set(CURRENT_VARIABLE_NAME, o);
	}

	/**
	 * @param o
	 *            The data that is currently selected in the UI.
	 */
	public void setCurrentData(Object o) {
		myInterpreter.set(CURRENT_DATA_NAME, o);
		// convert the TimeSeriesData into a numeric array
		myInterpreter.exec(CURRENT_DATA_NAME+"=array("+CURRENT_DATA_NAME+".values).T");
	}

	/**
	 * Sets initial focus (should be called from UI thread)
	 */
	public void setFocus() {
		myCommandField.requestFocus();
	}

	/**
	 * @param text
	 *            Text to append to display
	 * @param style
	 *            Name of text style (from class constants)
	 */
	public void appendText(String text, String style) {
		try {
			myDisplayArea.getDocument().insertString(myDisplayArea.getDocument().getLength(), text,
					myStyleContext.getStyle(style));
			myDisplayArea.setCaretPosition(myDisplayArea.getDocument().getLength()); // scroll
			// to
			// end
		} catch (BadLocationException e) {
			ourLogger.warn("Scrolling problem", e);
		}
	}

	/**
	 * Clears the command field
	 */
	public void clearCommand() {
		myCommandField.setText("");
	}

	/**
	 * @return True iff the user is currently typing a string literal (ie is
	 *         between single or double quotes)
	 */
	public boolean withinString() {
		char[] typedTextToCaret = myTypedText.substring(0, myTypedCaretPosition).toCharArray();
		int singles = 0;
		int doubles = 0;

		for (int i = 0; i < typedTextToCaret.length; i++) {
			if (typedTextToCaret[i] == '\'') {
                singles++;
            }
			if (typedTextToCaret[i] == '"') {
                doubles++;
            }
		}

		if (singles % 2 == 1 || doubles % 2 == 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param inMode
	 *            Sets whether the console is in the mode of completing a call
	 *            chain (otherwise it uses history completion)
	 */
	public void setInCallChainCompletionMode(boolean inMode) {
		myInCallChainCompletionMode = inMode;
		if (inMode) {
			String typedTextToCaret = myTypedText.substring(0, myTypedCaretPosition);
			myCallChainCompletor.setBase(getCallChain(typedTextToCaret));
		} else {
			myCommandField.setToolTipText(null);
		}
	}

	/**
	 * @return True if command completion is currently based on the call chain
	 *         that the user is typing, false if completion is based on command
	 *         history
	 */
	public boolean getInCallChainCompletionMode() {
		return myInCallChainCompletionMode;
	}

	/**
	 * @param text
	 *            Processes the current command in the command field
	 */
	public void enterCommand(String text) {
		appendText(">>", "root");
		appendText(text + "\r\n", COMMAND_STYLE);
		myHistoryCompletor.add(text);
		clearCommand();

		final String initText=text;
		
        (new TrackedAction("Running...") {
            private static final long serialVersionUID = 1L;
            

            @Override
            protected void action() throws ActionException {
            	NengoGraphics.getInstance().getProgressIndicator().setText(initText.trim());
            	myCommandField.setEnabled(false);
                try {
        			if (initText.startsWith("run ")) {
        				addVariable("scriptname", initText.substring(4).trim());
        				myInterpreter.execfile(initText.substring(4).trim());
        			} else if (initText.startsWith("help ")) {
        				appendText(JavaSourceParser.removeTags(getHelp(initText.substring(5).trim())), HELP_STYLE);
        				appendText("\n","root");
        			} else {
        				myInterpreter.exec(initText);
        			}
                } catch (RuntimeException e) {
          			ourLogger.error("Runtime error in interpreter", e);
           			appendText(e.toString()+"\n", ERROR_STYLE);
                }
            }
                
             @Override
             protected void postAction() {
            	 super.postAction();
            	 myCommandField.setEnabled(true);
            	 myCommandField.requestFocus();
             }
        }).doAction();
        
	}
	
	private static String getHelp(String entity) {
		String result = "No documentation found for " + entity;

		try {
			Class<?> c = Class.forName(entity);
			String docs = JavaSourceParser.getDocs(c);
			if (docs != null) {
                result = docs;
            }
		} catch (ClassNotFoundException e) {
			ourLogger.error("Class not found in help request", e);
		}

		return result;
	}

	/**
	 * Moves up the command completor list
	 */
	public void completorUp() {
		if (myInCallChainCompletionMode) {
			String callChain = getCallChain(myTypedText.substring(0, myTypedCaretPosition));
			String replacement = myCallChainCompletor.previous(callChain);
			myCommandField.select(myTypedCaretPosition - callChain.length(), myCommandField
					.getCaretPosition());
			// String selection = myCommandField.getSelectedText();
			myCommandField.replaceSelection(replacement);
			myCommandField.setToolTipText(myCallChainCompletor.getDocumentation());

			// System.out.println("caret pos: " + myTypedCaretPosition);
			// System.out.println("call chain: " + callChain);
			// System.out.println("selection: " + selection);
			// System.out.println("replacement: " + replacement);
		} else {
			myCommandField.setText(myHistoryCompletor.previous(myTypedText));
		}
	}

	/**
	 * Moves down the command completor list
	 */
	public void completorDown() {
		if (myInCallChainCompletionMode) {
			String callChain = getCallChain(myTypedText.substring(0, myTypedCaretPosition));
			String replacement = myCallChainCompletor.next(callChain);
			myCommandField.select(myTypedCaretPosition - callChain.length(), myCommandField
					.getCaretPosition());
//			 String selection = myCommandField.getSelectedText();
			myCommandField.replaceSelection(replacement);
			myCommandField.setToolTipText(myCallChainCompletor.getDocumentation());

//			 System.out.println("call chain: " + callChain);
//			 System.out.println("selection: " + selection);
//			 System.out.println("replacement: " + replacement);
		} else {
			myCommandField.setText(myHistoryCompletor.next(myTypedText));
		}
	}

	public void showToolTip() {
		if (myToolTipVisible == false && myLastHelpTime > 0 && System.currentTimeMillis() - myLastHelpTime < 500) {
			appendText(JavaSourceParser.removeTags(myCommandField.getToolTipText()) + "\r\n", HELP_STYLE);
		} else {
			final Action showTip = myCommandField.getActionMap().get("postTip");
			myHideTip = myCommandField.getActionMap().get("hideTip");
			if (showTip != null && !myToolTipVisible) {
				final ActionEvent e = new ActionEvent(myCommandField, ActionEvent.ACTION_PERFORMED, "");
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						showTip.actionPerformed(e);
					}
				});
			}
			myToolTipVisible = true;
			myDefaultDismissDelay = ToolTipManager.sharedInstance().getDismissDelay();
			ToolTipManager.sharedInstance().setDismissDelay(1000*60); //show until hideToolTip(), up to one minute max
		}
		myLastHelpTime = System.currentTimeMillis();
	}

	public void hideToolTip() {
		if (myHideTip != null && myToolTipVisible) {
			final ActionEvent e = new ActionEvent(myCommandField, ActionEvent.ACTION_PERFORMED, "");
//			final Action hideTip = myHideTip;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					myHideTip.actionPerformed(e);
				}
			});
		}
		myToolTipVisible = false;
		ToolTipManager.sharedInstance().setDismissDelay(myDefaultDismissDelay);
	}

	/**
	 * Takes note of the text in the command field, as text that the user has
	 * typed (as opposed to recalled history). The two types must be
	 * distinguished, because we don't want an unselected history item to be
	 * used as the basis for subsequent history lookups.
	 */
	public void setTypedText() {
		myTypedText = myCommandField.getText();
		myTypedCaretPosition = myCommandField.getCaretPosition();
		myHistoryCompletor.resetIndex();
		myCallChainCompletor.resetIndex();
	}

	/**
	 * Resets command field text to the last text typed by the user (as opposed
	 * to autocompleted text).
	 */
	public void revertToTypedText() {
		myCommandField.setText(myTypedText);
		myCommandField.setCaretPosition(myTypedCaretPosition);
	}

	/**
	 * @param command
	 *            A line of python code
	 * @return The segment at the end of the command that looks like a partial
	 *         call chain, eg for command "y.getY(x.get" this method would
	 *         return "x.get"
	 */
	public static String getCallChain(String command) {
		// note: I tried to do this with a single regex but I can't see how to
		// handle nested brackets properly
		Pattern pattern = Pattern.compile("\\w||\\."); // word character or dot

		char[] cc = command.toCharArray(); // command characters
		int brackets = 0;
		int start = 0;
		for (int i = cc.length - 1; i >= 0 && start == 0; i--) {
			if (cc[i] == ')') {
				brackets++;
			} else if (brackets > 0 && cc[i] == '(') {
				brackets--;
			} else if (brackets == 0
					&& !(i == cc.length-1 && cc[i] == '(') //include opening bracket if last char
					&& !pattern.matcher(String.valueOf(cc[i])).matches()) {
				start = i + 1;
			}
		}

		return command.substring(start);
	}



	private class CommandKeyListener implements KeyListener {

		private ScriptConsole myConsole;

		public CommandKeyListener(ScriptConsole console) {
			myConsole = console;
		}

		public void keyPressed(KeyEvent e) {
			try {
				int code = e.getKeyCode();
				if (code == 27 && myConsole.getInCallChainCompletionMode()) { // escape
					myConsole.revertToTypedText();
					myConsole.setInCallChainCompletionMode(false);
				} else if (code == 27) {
					myConsole.clearCommand();
				} else if (code == 9 && e.isShiftDown()) { // shift-tab
					myCommandField.append("\t");
				} else if (code == 9) { // tab
					myConsole.setInCallChainCompletionMode(true);
					e.consume();
				} else if (code == 38) { // up arrow
					myConsole.completorUp();
					e.consume();
				} else if (code == 40) { // down arrow
					myConsole.completorDown();
					e.consume();
				} else if (code == 17) { //CTRL
					myConsole.showToolTip();
				} else if (code == 10 && e.isShiftDown()) { //shift-enter
					//allow a new line to be entered
					myCommandField.append("\n");
				} else if (code == 10) { //enter.  execute code.
					e.consume();
					myConsole.enterCommand(myCommandField.getText());
				} else {
					myConsole.setInCallChainCompletionMode(false);
				}
			} catch (RuntimeException ex) {
				ourLogger.warn("Exception while processing KeyEvent", ex);
			}
		}

		public void keyReleased(KeyEvent e) {
			try {
				int code = e.getKeyCode();
				if (code == 17) { //CTRL
					myConsole.hideToolTip();
                }else if (code != 38 && code != 40) {
					myConsole.setTypedText();
					if (code == 9)
	                    myConsole.completorUp();
				}

				if (code == 46 && !myConsole.withinString()) { // .
					myConsole.setInCallChainCompletionMode(true);
				}
			} catch (RuntimeException ex) {
				ourLogger.warn("Exception while processing KeyEvent", ex);
			}
		}

		public void keyTyped(KeyEvent e) {
		}

	}

	
	
	/**
	 * Replacement for original console writer that should eliminate the "Read end dead" bug.
	 *
	 * @author Terry Stewart
	 */
	private class ConsoleOutputWriter extends java.io.Writer {
		private ScriptConsole myConsole;
		public ConsoleOutputWriter(ScriptConsole console) {
			myConsole=console;
		}
		public void write(char[] cbuf, int off, int len) {
			myConsole.appendText(new String(cbuf,off,len),OUTPUT_STYLE);
		}
		public void flush() {
		}
		public void close() {
		}
	}

	public static void main(String[] args) {
		// System.out.println(makePythonName("10balloon"));
		// System.out.println(makePythonName("assert"));
		// System.out.println(makePythonName("1 + 1 = 2"));

		JavaSourceParser.addSource(new File("../simulator/src/java/main"));
		PythonInterpreter interpreter = new PythonInterpreter();
		ScriptConsole console = new ScriptConsole(interpreter);

		JFrame frame = new JFrame("Script Console");
		frame.getContentPane().add(console);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		frame.setSize(500, 400);
		frame.setVisible(true);

		final ScriptConsole c = console;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				c.setFocus();
			}
		});

	}
}



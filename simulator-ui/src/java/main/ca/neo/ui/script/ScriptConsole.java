/*
 * Created on 5-Nov-07
 */
package ca.neo.ui.script;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.util.regex.Pattern;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.apache.log4j.Logger;
import org.python.util.PythonInterpreter;

import ca.neo.config.JavaSourceParser;

/**
 * A user interface panel for entering script commands. TODO: - talk to Terry re
 * directory defaults (to use with execfile) - escape not working all the time? -
 * import defaults - completion for arrays - getting documentation help (see
 * qdox) - static completion; constructor completion
 * 
 * @author Bryan Tripp
 */
public class ScriptConsole extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Logger ourLogger = Logger.getLogger(ScriptConsole.class);
	private static final String CURRENT_VARIABLE_NAME = "that";

	public static final String COMMAND_STYLE = "command";
	public static final String OUTPUT_STYLE = "output";
	public static final String ERROR_STYLE = "error";
	public static final String HELP_STYLE = "help";

	private PythonInterpreter myInterpreter;
	private JEditorPane myDisplayArea;
	private JTextField myCommandField;
	private HistoryCompletor myHistoryCompletor;
	private CallChainCompletor myCallChainCompletor;
	private boolean myInCallChainCompletionMode;
	private String myTypedText;
	private int myTypedCaretPosition;
	private StyleContext myStyleContext;
	private JSeparator seperator;
	private Style rootStyle;
	private Style commandStyle;

	/**
	 * @param interpreter
	 *            The interpreter on which the console runs
	 */
	public ScriptConsole(PythonInterpreter interpreter) {
		myInterpreter = interpreter;

		myDisplayArea = new JEditorPane("text/html", "");
		myDisplayArea.setEditable(false);
		myDisplayArea.setMargin(new Insets(5, 5, 5, 5));

		myCommandField = new JTextField();

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
		myCommandField.addActionListener(new CommandActionListener(this));
		myCommandField.setFocusTraversalKeysEnabled(false);

		myCommandField.setBorder(null);

		myHistoryCompletor = new HistoryCompletor();
		myCallChainCompletor = new CallChainCompletor(myInterpreter);
		myInCallChainCompletionMode = false;
		myTypedText = "";
		myTypedCaretPosition = 0;

		try {
			OutputWriter ow = new OutputWriter(this);
			interpreter.setOut(ow.getOutputStream());
			Thread owThread = new Thread(ow);
			owThread.start();
		} catch (IOException e) {
			ourLogger.error("Problem setting up console output", e);
		}

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
		myCommandField.setBackground(fg);
	}

	private void setChildrenForeground(Color fg) {
		myDisplayArea.setForeground(fg);
		myCommandField.setForeground(fg);
		seperator.setForeground(fg);
	}

	private void initStyles() {
		myStyleContext = new StyleContext();
		rootStyle = myStyleContext.addStyle("root", null);
		StyleConstants.setForeground(rootStyle, ca.shu.ui.lib.Style.Style.COLOR_FOREGROUND);

		setChildrenBackground(ca.shu.ui.lib.Style.Style.COLOR_BACKGROUND);
		setChildrenForeground(ca.shu.ui.lib.Style.Style.COLOR_FOREGROUND);
		myCommandField.setCaretColor(ca.shu.ui.lib.Style.Style.COLOR_LIGHT_GREEN);

		commandStyle = myStyleContext.addStyle(COMMAND_STYLE, rootStyle);
		StyleConstants.setForeground(commandStyle, ca.shu.ui.lib.Style.Style.COLOR_FOREGROUND);
		StyleConstants.setItalic(commandStyle, true);
		Style outputStyle = myStyleContext.addStyle(OUTPUT_STYLE, rootStyle);
		StyleConstants.setForeground(outputStyle, Color.GRAY);
		Style errorStyle = myStyleContext.addStyle(ERROR_STYLE, rootStyle);
		StyleConstants.setForeground(errorStyle, Color.RED);

		Style helpStyle = myStyleContext.addStyle(HELP_STYLE, rootStyle);
		StyleConstants.setForeground(helpStyle, ca.shu.ui.lib.Style.Style.COLOR_FOREGROUND);
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
			if (typedTextToCaret[i] == '\'')
				singles++;
			if (typedTextToCaret[i] == '"')
				doubles++;
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
		try {
			if (text.startsWith("run ")) {
				myInterpreter.execfile(text.substring(4).trim());
			} else if (text.startsWith("help ")) {
				appendText(getHelp(text.substring(5).trim()), HELP_STYLE);
			} else {
				myInterpreter.exec(text);
			}
		} catch (RuntimeException e) {
			ourLogger.error("Runtime error in interpreter", e);
			appendText(e.toString(), ERROR_STYLE);
		}
	}

	private static String getHelp(String entity) {
		String result = "No documentation found for " + entity;

		try {
			Class<?> c = Class.forName(entity);
			String docs = JavaSourceParser.getDocs(c);
			if (docs != null)
				result = docs;
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
			// String selection = myCommandField.getSelectedText();
			myCommandField.replaceSelection(replacement);

			// System.out.println("call chain: " + callChain);
			// System.out.println("selection: " + selection);
			// System.out.println("replacement: " + replacement);
		} else {
			myCommandField.setText(myHistoryCompletor.next(myTypedText));
		}
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
			} else if (brackets == 0 && !pattern.matcher(String.valueOf(cc[i])).matches()) {
				start = i + 1;
			}
		}

		return command.substring(start);
	}

	private class CommandActionListener implements ActionListener {

		private ScriptConsole myConsole;

		public CommandActionListener(ScriptConsole console) {
			myConsole = console;
		}

		public void actionPerformed(ActionEvent e) {
			myConsole.enterCommand(e.getActionCommand());
		}
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
				} else if (code == 27) {
					myConsole.clearCommand();
				} else if (code == 9) { // tab
					myConsole.setInCallChainCompletionMode(true);
				} else if (code == 38) { // up arrow
					myConsole.completorUp();
				} else if (code == 40) { // down arrow
					myConsole.completorDown();
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
				if (code != 38 && code != 40) {
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
	 * Writes interpreter output to console.
	 * 
	 * @author Bryan Tripp
	 */
	private class OutputWriter implements Runnable {

		private ScriptConsole myConsole;
		private PipedOutputStream myOutput;
		private Reader myInput;

		public OutputWriter(ScriptConsole console) throws IOException {
			myConsole = console;
			myOutput = new PipedOutputStream();
			myInput = new InputStreamReader(new PipedInputStream(myOutput));
		}

		/**
		 * @return An OutputStream to which a PythonInterpreter can write output
		 *         so that it appears in the console display.
		 */
		public OutputStream getOutputStream() {
			return myOutput;
		}

		public void run() {
			char[] buffer = new char[512];
			int charsRead = 0;

			try {
				while (charsRead >= 0) {
					charsRead = myInput.read(buffer);
					if (charsRead > 0)
						myConsole.appendText(String.valueOf(buffer, 0, charsRead), OUTPUT_STYLE);
				}
			} catch (IOException e) {
				ourLogger.error("Problem writing to console", e);
			}
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

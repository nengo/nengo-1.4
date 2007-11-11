/*
 * Created on 5-Nov-07
 */
package ca.neo.ui.script;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.python.core.PyInstance;
import org.python.core.PyInteger;
import org.python.core.PyJavaInstance;
import org.python.core.PyList;
import org.python.core.PyMethod;
import org.python.core.PyObject;
import org.python.core.PyReflectedFunction;
import org.python.core.PySequence;
import org.python.core.PyString;
import org.python.core.PyStringMap;
import org.python.core.PyTuple;
import org.python.util.PythonInterpreter;

/**
 * A user interface panel for entering script commands. 
 *  
 * @author Bryan Tripp
 */
public class ScriptConsole extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public static final String COMMAND_STYLE = "command";
	public static final String OUTPUT_STYLE = "output";
	public static final String ERROR_STYLE = "error";
	
	private PythonInterpreter myInterpreter;
	private JEditorPane myDisplayArea;
	private JTextField myCommandField;
	private CommandHistory myHistory;
	private CodeCompletor myCompletor;
	private String myTypedText;
	private StyleContext myStyleContext;
	
	public ScriptConsole(PythonInterpreter interpreter) {
		myInterpreter = interpreter;
		
		myDisplayArea = new JEditorPane("text/html", "");
		myDisplayArea.setEditable(false);
		myDisplayArea.setMargin(new Insets(5, 5, 5, 5));
		initStyles();
		
		myCommandField = new JTextField();
		
		setLayout(new BorderLayout());
		JScrollPane displayScroll = new JScrollPane(myDisplayArea);
		add(displayScroll, BorderLayout.CENTER);
		add(myCommandField, BorderLayout.SOUTH);
				
		myCommandField.addKeyListener(new CommandKeyListener(this));
		myCommandField.addActionListener(new CommandActionListener(this));
		
		myHistory = new CommandHistory();
		myCompletor = new CodeCompletor(myInterpreter);
		myTypedText = "";		
		
		try {
			OutputWriter ow = new OutputWriter(this);
			interpreter.setOut(ow.getOutputStream());
			Thread owThread = new Thread(ow);
			owThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	protected void test() {
//		List<String> variables = myCompletor.getVariables();
//		for (Iterator<String> iter = variables.iterator(); iter.hasNext(); ) {
//			System.out.println("variable: " + iter.next());
//		}
		List<String> members = myCompletor.getMembers(myCommandField.getText());
		for (Iterator<String> iter = members.iterator(); iter.hasNext(); ) {
			System.out.println("member: " + iter.next());
		}
	}
	
	private void initStyles() {
		myStyleContext = new StyleContext();
		Style rootStyle = myStyleContext.addStyle("root", null);
		Style commandStyle = myStyleContext.addStyle(COMMAND_STYLE, rootStyle);
		StyleConstants.setItalic(commandStyle, true);
		Style outputStyle = myStyleContext.addStyle(OUTPUT_STYLE, rootStyle);
		Style errorStyle = myStyleContext.addStyle(ERROR_STYLE, rootStyle);
		StyleConstants.setForeground(errorStyle, Color.RED);
	}
	
	/**
	 * Sets initial focus (should be called from UI thread)
	 */
	public void setFocus() {
		myCommandField.requestFocus();
	}
	
	/**
	 * @param text Text to append to display
	 * @param style Name of text style (from class constants) 
	 */
	public void appendText(String text, String style) {
		try {
			myDisplayArea.getDocument().insertString(myDisplayArea.getDocument().getLength(), text, myStyleContext.getStyle(style));
			myDisplayArea.setCaretPosition(myDisplayArea.getDocument().getLength()); //scroll to end
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Clears the command field
	 */
	public void clearCommand() {
		myCommandField.setText("");
	}
	
	/**
	 * @param text Processes the current command in the command field 
	 */
	public void enterCommand(String text) {
		appendText(">>", "root");
		appendText(text + "\r\n", COMMAND_STYLE);
		myHistory.add(text);
		clearCommand();
		try {
			if (text.startsWith("run ")) {
				myInterpreter.execfile(text.substring(4).trim());
			} else {
				myInterpreter.exec(text);						
			}
		} catch (RuntimeException e) {
			appendText(e.toString(), ERROR_STYLE);
		}
	}
	
	/**
	 * Moves up the command history 
	 */
	public void historyUp() {
		myCommandField.setText(myHistory.previous(myTypedText));
	}

	/**
	 * Moves down the command history
	 */
	public void historyDown() {
		myCommandField.setText(myHistory.next(myTypedText));		
	}
	
	/**
	 * Takes note of the text in the command field, as text that the user has typed 
	 * (as opposed to recalled history). The two types must be distinguished, because 
	 * we don't want an unselected history item to be used as the basis for subsequent 
	 * history lookups.  
	 */
	public void setTypedText() {
		myTypedText = myCommandField.getText();
		myHistory.resetIndex();
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
			int code = e.getKeyCode();
			if (code == 27) { //escape
				myConsole.clearCommand();
			} else if (code == 38) { //up arrow
				myConsole.historyUp();
			} else if (code == 40) { //down arrow
				myConsole.historyDown();
			} else if (code == 39) { //right arrow
				myConsole.test();
			}
		}

		public void keyReleased(KeyEvent e) {
			int code = e.getKeyCode();
			if (code != 38 && code != 40) {
				myConsole.setTypedText();	
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
		 * 		so that it appears in the console display.  
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
					if (charsRead > 0) myConsole.appendText(String.valueOf(buffer, 0, charsRead), OUTPUT_STYLE);
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) {
		
		PythonInterpreter interpreter = new PythonInterpreter();
		ScriptConsole console = new ScriptConsole(interpreter);

		JFrame frame = new JFrame("Script Console");
		frame.getContentPane().add(console); 
		
		frame.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			}
		);
		
		frame.setSize(500, 400);
		frame.setVisible(true);
		
		final ScriptConsole c = console;
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					c.setFocus();
				}
			}
		);
		
	}
}

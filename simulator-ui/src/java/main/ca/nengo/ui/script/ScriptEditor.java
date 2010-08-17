/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "ScriptEditor.java". Description: 
"A basic tabbed text editor.
  
  @author Bryan Tripp"

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
 * Created on 6-Nov-07
 */
package ca.nengo.ui.script;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;

import ca.nengo.ui.lib.AppFrame;
import ca.nengo.ui.lib.Style.Style;
import ca.nengo.ui.lib.actions.ActionException;
import ca.nengo.ui.lib.actions.StandardAction;
import ca.nengo.ui.lib.util.UIEnvironment;
import ca.nengo.ui.lib.util.menus.MenuBuilder;
import ca.nengo.util.Environment;

/**
 * A basic tabbed text editor.
 * 
 * @author Bryan Tripp
 */
public class ScriptEditor extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTabbedPane myTabs;
	private List<ScriptData> myScripts;
	private File myDirectory;
	private FileFilter myFilter;

	public ScriptEditor() {
		this(new File("."));
	}

	/**
	 * @param directory
	 *            Directory in which to save and load files by default.
	 */
	public ScriptEditor(File directory) {
		myScripts = new ArrayList<ScriptData>(10);
		setLayout(new BorderLayout());
		myTabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		myTabs.setBackground(Style.COLOR_BACKGROUND);
		add(myTabs, BorderLayout.CENTER);

		myDirectory = directory;
		myFilter = new ExtensionFileFilter(new String[] { "py" });
		setBackground(Style.COLOR_BACKGROUND);
		setBorder(null);
	}

	/**
	 * Opens a blank script in a new tab.
	 */
	public void newFile() {
		ScriptData sd = openEditor(null, false, "New Script");
		sd.getTextComponent().getDocument().addDocumentListener(new ChangeListener(sd));
	}

	/**
	 * Saves the current script in its current location. If the script doesn't
	 * have a location, this method defers to saveCurrentFileAs().
	 * 
	 * @throws IOException
	 */
	public void saveCurrentFile() throws IOException {
		int index = myTabs.getSelectedIndex();
		if (index >= 0) {
			ScriptData sd = myScripts.get(index);

			if (sd.getFile() == null) {
				saveCurrentFileAs();
			} else {
				FileWriter writer = new FileWriter(sd.getFile());
				writer.write(sd.getTextComponent().getText());
				writer.flush();
				writer.close();

				sd.setSaved(true);
			}
		}
	}

	/**
	 * Saves the currrent script in a new location.
	 * 
	 * @throws IOException
	 */
	public void saveCurrentFileAs() throws IOException {
		int index = myTabs.getSelectedIndex();
		if (index >= 0) {
			ScriptData sd = myScripts.get(index);

			JFileChooser fileChooser = new JFileChooser(myDirectory);
			fileChooser.setFileFilter(myFilter);
			int selection = fileChooser.showSaveDialog(this);
			if (selection == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				sd.setFile(file);
				sd.setName(file.getName());
				myTabs.setTitleAt(index, file.getName());
				saveCurrentFile();
			}
		}
	}

	/**
	 * Closes all open files.
	 * 
	 * @return True if the close was successful (not cancelled by user)
	 * @throws IOException
	 */
	public boolean closeAll() throws IOException {
		boolean cancelled = false;

		while (myTabs.getTabCount() > 0 && !cancelled) {
			if (closeCurrentFile() == JOptionPane.CANCEL_OPTION) {
				cancelled = true;
			}
		}

		return !cancelled;
	}

	/**
	 * Closes the current script, prompting the user to save if there are
	 * unsaved changes.
	 * 
	 * @return A JOptionPane constant indicating the user-selected action if the
	 *         user was prompted to save, otherwise JOptionPane.YES_OPTION by
	 *         default.
	 * @throws IOException
	 */
	public int closeCurrentFile() throws IOException {
		int result = JOptionPane.YES_OPTION;
		int index = myTabs.getSelectedIndex();

		if (index >= 0) {
			if (!myScripts.get(index).isSaved()) {
				Object[] options = { "Save", "Discard", "Cancel" };
				int selection = JOptionPane.showOptionDialog(this, "Would you like to save "
						+ myScripts.get(index).getName() + " before closing?", "Confirm",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						options, options[0]);
				if (selection == JOptionPane.YES_OPTION) {
					saveCurrentFile();
					doClose(index);
				} else if (selection == JOptionPane.NO_OPTION) {
					doClose(index);
				}

				result = selection;
			} else {
				doClose(index);
			}
		}

		return result;
	}

	private void doClose(int index) {
		myTabs.remove(index);
		myScripts.remove(index);
	}

	/**
	 * Opens a user-specified script file.
	 * 
	 * @throws IOException
	 */
	public void openFile() throws IOException {
		JFileChooser fileChooser = new JFileChooser(myDirectory);
		fileChooser.setFileFilter(myFilter);
		int selection = fileChooser.showOpenDialog(this);
		if (selection == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			ScriptData sd = openEditor(file, true, file.getName());

			FileReader reader = new FileReader(file);
			char[] buffer = new char[(int) file.length()];
			reader.read(buffer);
			String text = String.valueOf(buffer);
			sd.getTextComponent().setText(text);

			sd.getTextComponent().getDocument().addDocumentListener(new ChangeListener(sd));
		}
	}

	private ScriptData openEditor(File file, boolean saved, String name) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(ca.nengo.ui.lib.Style.Style.COLOR_BACKGROUND);

		JEditorPane ep = new JEditorPane();

		ep.setForeground(ca.nengo.ui.lib.Style.Style.COLOR_FOREGROUND);
		ep.setBackground(ca.nengo.ui.lib.Style.Style.COLOR_BACKGROUND);
		ep.setCaretColor(ca.nengo.ui.lib.Style.Style.COLOR_LIGHT_BLUE);

		final StyledDocument doc = new DefaultStyledDocument();
		ep.setDocument(doc);
		JScrollPane scroll = new JScrollPane(ep);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		panel.add(scroll, BorderLayout.CENTER);

		final JLabel positionLabel = new JLabel("1 : 1");
		positionLabel.setForeground(ca.nengo.ui.lib.Style.Style.COLOR_FOREGROUND);
		panel.add(positionLabel, BorderLayout.SOUTH);

		ep.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				int position = e.getDot();
				int line = doc.getDefaultRootElement().getElementIndex(position);
				int column = position
						- doc.getDefaultRootElement().getElement(line).getStartOffset();
				positionLabel.setText((line + 1) + " : " + (column + 1));
			}
		});

		ScriptData result = new ScriptData(ep, file, saved, name);

		myTabs.addTab(name, panel);
		myTabs.setSelectedComponent(panel);
		myScripts.add(result);

		return result;
	}

	private class ExtensionFileFilter extends FileFilter {

		private List<String> myExtensions;

		public ExtensionFileFilter(String[] extensions) {
			myExtensions = new ArrayList<String>(extensions.length);
			for (int i = 0; i < extensions.length; i++) {
				myExtensions.add(extensions[i]);
			}
		}

		@Override
		public boolean accept(File f) {
			boolean result = false;

			int dot = f.getName().lastIndexOf('.');
			if (dot >= 0) {
				String extension = f.getName().substring(dot + 1);
				if (myExtensions.contains(extension))
					result = true;
			}

			return result;
		}

		@Override
		public String getDescription() {
			StringBuffer buf = new StringBuffer("Files with extension ");
			for (Iterator<String> iter = myExtensions.iterator(); iter.hasNext();) {
				buf.append('.');
				buf.append(iter.next());
				if (iter.hasNext())
					buf.append(", ");
			}
			return buf.toString();
		}

	}

	private class ChangeListener implements DocumentListener {

		// TODO: could unregister this after a change and register a new one
		// with a save

		private ScriptData myScript;

		public ChangeListener(ScriptData script) {
			myScript = script;
		}

		public void changedUpdate(DocumentEvent e) {
			myScript.setSaved(false);
		}

		public void insertUpdate(DocumentEvent e) {
			myScript.setSaved(false);
		}

		public void removeUpdate(DocumentEvent e) {
			myScript.setSaved(false);
		}

	}

	/**
	 * Data related to a single open script file.
	 * 
	 * @author Bryan Tripp
	 */
	private class ScriptData {

		private JTextComponent myTextComponent;
		private File myFile;
		private boolean mySaved;
		private String myName;

		public ScriptData(JTextComponent textComponent, File file, boolean saved, String name) {
			myTextComponent = textComponent;
			myFile = file;
			mySaved = saved;
			myName = name;
		}

		public JTextComponent getTextComponent() {
			return myTextComponent;
		}

		public File getFile() {
			return myFile;
		}

		public void setFile(File file) {
			myFile = file;
		}

		public boolean isSaved() {
			return mySaved;
		}

		public void setSaved(boolean saved) {
			mySaved = saved;
		}

		public String getName() {
			return myName;
		}

		public void setName(String name) {
			myName = name;
		}

	}

	/**
	 * Opens an editor and console in a new window.
	 * 
	 * @return The new console.
	 */
	public static void openEditor() {
		openEditor(false);
	}

	private static void openEditor(final boolean exitOnWindowClose) {
		final ScriptEditor editor = new ScriptEditor();
		editor.setPreferredSize(new Dimension(600, 600));

		final JFrame frame = new JFrame("Script Editor");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(editor, BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBorder(null);
		Style.applyMenuStyle(menuBar, true);

		frame.setBackground(Style.COLOR_BACKGROUND2);

		MenuBuilder fileMenu = new MenuBuilder("File");
		fileMenu.getJMenu().setMnemonic(KeyEvent.VK_F);

		fileMenu.addAction(new StandardAction("New") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void action() throws ActionException {
				editor.newFile();
			}
		}, KeyEvent.VK_N, KeyStroke.getKeyStroke(KeyEvent.VK_N, AppFrame.MENU_SHORTCUT_KEY_MASK));

		fileMenu.addAction(new StandardAction("Open") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void action() throws ActionException {
				try {
					editor.openFile();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}, KeyEvent.VK_O, KeyStroke.getKeyStroke(KeyEvent.VK_O, AppFrame.MENU_SHORTCUT_KEY_MASK));

		fileMenu.addAction(new StandardAction("Save") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void action() throws ActionException {
				try {
					editor.saveCurrentFile();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}, KeyEvent.VK_S, KeyStroke.getKeyStroke(KeyEvent.VK_S, AppFrame.MENU_SHORTCUT_KEY_MASK));

		fileMenu.addAction(new StandardAction("Save As...") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void action() throws ActionException {
				try {
					editor.saveCurrentFileAs();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}, KeyEvent.VK_A);

		fileMenu.addAction(new StandardAction("Close") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void action() throws ActionException {
				try {
					editor.closeCurrentFile();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}, KeyEvent.VK_C);

		fileMenu.addAction(new StandardAction("Exit") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void action() throws ActionException {
				try {
					if (editor.closeAll()) {
						frame.dispose();
					}
					if (exitOnWindowClose) {
						System.exit(0);
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}, KeyEvent.VK_X);

		menuBar.add(fileMenu.getJMenu());
		frame.setJMenuBar(menuBar);

		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new MyWindowAdapter(editor, exitOnWindowClose));

		frame.pack();

		if (UIEnvironment.getInstance() != null) {
			frame.setLocationRelativeTo(UIEnvironment.getInstance());
		}

		frame.setVisible(true);
	}

	public static void main(String[] args) {
		openEditor(true);
		Environment.setUserInterface(true);
	}
}

class MyWindowAdapter extends WindowAdapter {
	private boolean exitOnWindowClose;
	private ScriptEditor editor;

	public MyWindowAdapter(ScriptEditor editor, boolean exitOnWindowClose) {
		super();
		this.exitOnWindowClose = exitOnWindowClose;
		this.editor = editor;
	}

	public void windowClosing(WindowEvent e) {
		try {
			if (editor.closeAll()) {
				e.getWindow().dispose();
			}
			if (exitOnWindowClose) {
				System.exit(0);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
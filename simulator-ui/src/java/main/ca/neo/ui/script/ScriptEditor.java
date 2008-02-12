/*
 * Created on 6-Nov-07
 */
package ca.neo.ui.script;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;


import ca.neo.util.Environment;

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
	 * @param directory Directory in which to save and load files by default.  
	 */
	public ScriptEditor(File directory) {
		myScripts = new ArrayList<ScriptData>(10);
		setLayout(new BorderLayout());		
		myTabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		add(myTabs, BorderLayout.CENTER);
		
		myDirectory = directory;
		myFilter = new ExtensionFileFilter(new String[]{"py"});
		
//		setBackground(ca.shu.ui.lib.Style.Style.COLOR_BACKGROUND);
	}
	
	/**
	 * Opens a blank script in a new tab. 
	 */
	public void newFile() {
		ScriptData sd = openEditor(null, false, "New Script");
		sd.getTextComponent().getDocument().addDocumentListener(new ChangeListener(sd));
	}
	
	/**
	 * Saves the current script in its current location. If the script doesn't have a location, this method 
	 * defers to saveCurrentFileAs(). 
	 * 
	 * @throws IOException
	 */
	public void saveCurrentFile() throws IOException {
		int index = myTabs.getSelectedIndex();
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
	
	/**
	 * Saves the currrent script in a new location. 
	 * 
	 * @throws IOException
	 */
	public void saveCurrentFileAs() throws IOException {
		int index = myTabs.getSelectedIndex();
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
	 * Closes the current script, prompting the user to save if there are unsaved changes. 
	 * 
	 * @return A JOptionPane constant indicating the user-selected action if the user was 
	 * 		prompted to save, otherwise JOptionPane.YES_OPTION by default. 
	 * @throws IOException
	 */
	public int closeCurrentFile() throws IOException {
		int result = JOptionPane.YES_OPTION;
		int index = myTabs.getSelectedIndex();
		
		if (!myScripts.get(index).isSaved()) {
			Object[] options = {"Save", "Discard", "Cancel"};
			int selection = JOptionPane.showOptionDialog(this, 
					"Would you like to save " + myScripts.get(index).getName() + " before closing?",
					"Confirm",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, 
					null, 
					options,
					options[0]);
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
		panel.setBackground(ca.shu.ui.lib.Style.Style.COLOR_BACKGROUND2);
		
		JEditorPane ep = new JEditorPane();
		
		ep.setForeground(ca.shu.ui.lib.Style.Style.COLOR_FOREGROUND);
		ep.setBackground(ca.shu.ui.lib.Style.Style.COLOR_BACKGROUND);
		ep.setCaretColor(ca.shu.ui.lib.Style.Style.COLOR_LIGHT_BLUE);
		
		final StyledDocument doc = new DefaultStyledDocument();
		ep.setDocument(doc);
		JScrollPane scroll = new JScrollPane(ep);
		panel.add(scroll, BorderLayout.CENTER);
		
		final JLabel positionLabel = new JLabel("1 : 1");
		positionLabel.setForeground(ca.shu.ui.lib.Style.Style.COLOR_FOREGROUND);
		panel.add(positionLabel, BorderLayout.SOUTH);
		
		ep.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				int position = e.getDot();
				int line = doc.getDefaultRootElement().getElementIndex(position);
				int column = position - doc.getDefaultRootElement().getElement(line).getStartOffset();
				positionLabel.setText((line+1) + " : " + (column+1));
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
				if (myExtensions.contains(extension)) result = true;
			}
			
			return result;
		}

		@Override
		public String getDescription() {
			StringBuffer buf = new StringBuffer("Files with extension ");
			for (Iterator<String> iter = myExtensions.iterator(); iter.hasNext(); ) {
				buf.append('.');
				buf.append(iter.next());
				if (iter.hasNext()) buf.append(", ");
			}
			return buf.toString();
		}
		
	}
	
	private class ChangeListener implements DocumentListener {
		
		//TODO: could unregister this after a change and register a new one with a save
		
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
	
	private static void openEditor(boolean exitOnWindowClose) {
		final ScriptEditor editor = new ScriptEditor();
		editor.setPreferredSize(new Dimension(600, 600));
		
		JFrame frame = new JFrame("Script Editor");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(editor, BorderLayout.CENTER);
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");

		JMenuItem newItem = new JMenuItem("New");
		newItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editor.newFile();
			}
		});
		fileMenu.add(newItem);

		JMenuItem openItem = new JMenuItem("Open");
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					editor.openFile();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		fileMenu.add(openItem);

		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					editor.saveCurrentFile();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		fileMenu.add(saveItem);		
	
		JMenuItem saveAsItem = new JMenuItem("Save As...");
		saveAsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					editor.saveCurrentFileAs();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		fileMenu.add(saveAsItem);		
	
		JMenuItem closeItem = new JMenuItem("Close");
		closeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					editor.closeCurrentFile();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		fileMenu.add(closeItem);		

		menuBar.add(fileMenu);
		frame.setJMenuBar(menuBar);

		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new MyWindowAdapter(editor, exitOnWindowClose));
		
		frame.pack();
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
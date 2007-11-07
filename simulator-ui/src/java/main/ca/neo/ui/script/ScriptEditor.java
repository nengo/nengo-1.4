/*
 * Created on 6-Nov-07
 */
package ca.neo.ui.script;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.text.JTextComponent;

/**
 * A basic text editor. 
 * 
 * @author Bryan Tripp
 */
public class ScriptEditor extends JPanel {
	
	private JTabbedPane myTabs;
	private List<ScriptData> myScripts; //TODO

	public ScriptEditor() {
		setLayout(new BorderLayout());
		
		myTabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		add(myTabs, BorderLayout.CENTER);		
	}
	
	public void newFile() {
		openEditor("New Script");
	}
	
	public void saveCurrentFile() {
		System.out.println("saving ... ");
		
		//TODO
	}
	
	public void saveCurrentFileAs() {
		System.out.println("saving as ... ");
		JFileChooser fileChooser = new JFileChooser(); //TODO: set working directory
		int selection = fileChooser.showSaveDialog(this);
		if (selection == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
		}
	}
	
	public void closeCurrentFile() {
		System.out.println("closing ... ");
		myTabs.remove(myTabs.getSelectedComponent());
	}
	
	public void openFile() {
		System.out.println("opening ... ");
		//TODO
	}
	
	private void openEditor(String name) {
		JEditorPane ep = new JEditorPane();
		myTabs.addTab(name, ep);
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
		
		public ScriptData(JTextComponent textComponent, File file, boolean saved) {
			myTextComponent = textComponent;
			myFile = file;
			mySaved = saved;
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
		
	}
	
	public static void main(String[] args) {
		
		final ScriptEditor editor = new ScriptEditor();
		
		JFrame frame = new JFrame("Script Editor");
		frame.getContentPane().add(editor);
		
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
				editor.openFile();
			}
		});
		fileMenu.add(openItem);
		JMenuItem saveItem = new JMenuItem("Save As ...");
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editor.saveCurrentFileAs();
			}
		});
		fileMenu.add(saveItem);		
		menuBar.add(fileMenu);
		JMenuItem closeItem = new JMenuItem("Close");
		closeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editor.closeCurrentFile();
			}
		});
		fileMenu.add(closeItem);		
		menuBar.add(fileMenu);
		
		frame.setJMenuBar(menuBar);
		
		frame.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			}
		);
		
		frame.setSize(500, 400);
		frame.setVisible(true);
		
	}
}

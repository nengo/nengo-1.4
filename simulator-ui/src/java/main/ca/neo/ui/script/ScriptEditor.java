/*
 * Created on 6-Nov-07
 */
package ca.neo.ui.script;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * A basic text editor. 
 * 
 * @author Bryan Tripp
 */
public class ScriptEditor extends JPanel {
	
	private JTabbedPane myTabs;

	public ScriptEditor() {
		setLayout(new BorderLayout());
		
		myTabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		add(myTabs, BorderLayout.CENTER);		
	}
	
	public static void main(String[] args) {
		
		ScriptEditor editor = new ScriptEditor();
		
		JFrame frame = new JFrame("Script Editor");
		frame.getContentPane().add(editor);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem newItem = new JMenuItem("New");
		fileMenu.add(newItem);
		JMenuItem openItem = new JMenuItem("Open");
		fileMenu.add(openItem);
		JMenuItem saveItem = new JMenuItem("Save");
		fileMenu.add(saveItem);		
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

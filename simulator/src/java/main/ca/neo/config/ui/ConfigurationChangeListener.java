/*
 * Created on 17-Dec-07
 */
package ca.neo.config.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

import ca.neo.config.EditorProxy;

public class ConfigurationChangeListener implements ActionListener {
	
	private ConfigurationTreeModel myModel;
	private TreeCellEditor myEditor;
	private TreePath myPath;
	private EditorProxy myEditorProxy;
	
	public ConfigurationChangeListener(JTree tree, TreePath path) {
		myModel = (ConfigurationTreeModel) tree.getModel();
		myEditor = tree.getCellEditor();
		myPath = path;
	}
	
	public void setProxy(EditorProxy proxy) {
		myEditorProxy = proxy;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			myModel.setValue(this, myPath, myEditorProxy.getValue());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		myEditor.stopCellEditing();
	}
	
}
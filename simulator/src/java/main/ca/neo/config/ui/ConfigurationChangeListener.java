/*
 * Created on 17-Dec-07
 */
package ca.neo.config.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

/**
 * A listener for changes to Property values. 
 * 
 * TODO: is there a better option than EditorProxy? 
 * TODO: can we avoid references to this class from ca.neo.config? 
 * 
 * @author Bryan Tripp
 */
public class ConfigurationChangeListener implements ActionListener {
	
	private JTree myTree;
	private ConfigurationTreeModel myModel;
	private TreeCellEditor myEditor;
	private TreePath myPath;
	private EditorProxy myEditorProxy;
	
	public ConfigurationChangeListener(JTree tree, TreePath path) {
		myTree = tree;
		myModel = (ConfigurationTreeModel) tree.getModel();
		myEditor = tree.getCellEditor();
		myPath = path;
	}
	
	/**
	 * Called by a ConfigurationHandler's editor. 
	 * 
	 * @param proxy Provides access to an updated property value after it is changed by the user 
	 */
	public void setProxy(EditorProxy proxy) {
		myEditorProxy = proxy;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			myModel.setValue(myPath, myEditorProxy.getValue());
		} catch (Exception ex) {
			String message = "The new value is invalid. The old value will be retained.";
			if (ex.getMessage() != null) message = ex.getMessage();
			ConfigExceptionHandler.handle(ex, message, myTree);
		}
		myEditor.stopCellEditing();
	}
	
	/**
	 * An editor component (from ConfigurationHandler.getEditor(...)) must implement EditorProxy 
	 * in order to allow retrieval of a new value when editing is complete. For example if 
	 * the component is a JTextField, the implementation could be 
	 * <code>getValue() { jtf.getText(); }</code>. 
	 *  
	 * @author Bryan Tripp
	 */
	public interface EditorProxy {
		
		/**
		 * @return Current value of edited object 
		 */
		public Object getValue();
		
	}
	
}
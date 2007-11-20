package ca.neo.ui.dataList;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import ca.neo.model.Network;

public class DataListView extends JPanel implements TreeSelectionListener {

	private static boolean DEBUG = false;

	private static String lineStyle = "Horizontal";
	// Optionally play with line styles. Possible values are
	// "Angled" (the default), "Horizontal", and "None".
	private static boolean playWithLineStyle = false;
	private static final long serialVersionUID = 1L;
	// Optionally set the look and feel.
	private static boolean useSystemLookAndFeel = false;

	static Network myNetwork;

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	public static JDialog createViewer(Frame owner,
			SimulatorDataModel simulatorData) {
		if (useSystemLookAndFeel) {
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				System.err.println("Couldn't use system look and feel.");
			}
		}

		// Create and set up the window.
		JDialog dialog = new JDialog(owner, "Data Viewer");

		dialog.setPreferredSize(new Dimension(300, 600));
		dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

		// Create and set up the content pane.
		DataListView newContentPane = new DataListView(simulatorData);

		newContentPane.setOpaque(true); // content panes must be opaque
		dialog.setContentPane(newContentPane);

		// Display the window.
		dialog.pack();
		dialog.setVisible(true);

		return dialog;
	}

	private JTree tree;

	public DataListView(SimulatorDataModel data) {
		super(new GridLayout(1, 0));

		// createNodes(top);

		// Create a tree that allows one selection at a time.
		tree = new JTree(data);
		tree.setEditable(true);

		data.addTreeModelListener(new MyTreeModelListener());
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		tree.addMouseListener(new TreeMouseListener());

		// Listen for when the selection changes.

		tree.addTreeSelectionListener(this);
		if (playWithLineStyle) {
			System.out.println("line style = " + lineStyle);
			tree.putClientProperty("JTree.lineStyle", lineStyle);
		}

		// Create the scroll pane and add the tree to it.
		JScrollPane treeView = new JScrollPane(tree);

		// Add the scroll panes to a split pane.
		// JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		// splitPane.setTopComponent(treeView);
		add(treeView);

		Dimension minimumSize = new Dimension(100, 50);
		treeView.setMinimumSize(minimumSize);
		// splitPane.setDividerLocation(100);
		// splitPane.setPreferredSize(new Dimension(500, 300));

		// Add the split pane to this panel.
		// add(splitPane);
	}

	/** Required by TreeSelectionListener interface. */
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();

		if (node == null)
			return;

		Object nodeInfo = node.getUserObject();
		if (node.isLeaf()) {

			System.out.println("Leaf Node clicked: " + node.toString());

		} else {
			System.out.println("Node clicked: " + node.toString());
		}
		if (DEBUG) {
			System.out.println(nodeInfo.toString());
		}
	}

	private class MyTreeModelListener implements TreeModelListener {

		public void treeNodesChanged(TreeModelEvent e) {
		}

		public void treeNodesInserted(TreeModelEvent e) {
			tree.scrollPathToVisible(e.getTreePath());
		}

		public void treeNodesRemoved(TreeModelEvent e) {
		}

		public void treeStructureChanged(TreeModelEvent e) {
		}

	}

	private class TreeMouseListener implements MouseListener {

		private void DoubleMouseClicked(MouseEvent e) {
			DataTreeNode dataNode = getDataNode(e);

			if (dataNode != null) {
				dataNode.getDefaultAction().doAction();
			}
		}

		private DataTreeNode getDataNode(MouseEvent e) {
			Object source = e.getSource();
			if (source instanceof JTree) {
				Object selectedComponent = ((JTree) source)
						.getLastSelectedPathComponent();

				if (selectedComponent instanceof DataTreeNode) {
					return (DataTreeNode) selectedComponent;

				}
			}
			return null;
		}

		private void LeftMouseClicked(MouseEvent e) {
			DataTreeNode dataNode = getDataNode(e);

			if (dataNode != null) {
				JPopupMenu menu = dataNode.showContextMenu();

				menu.show(e.getComponent(), e.getPoint().x, e.getPoint().y);
				menu.setVisible(true);

			}
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				LeftMouseClicked(e);
			} else if (e.getClickCount() == 2
					&& e.getButton() == MouseEvent.BUTTON1) {
				DoubleMouseClicked(e);
			}

		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

	}

}

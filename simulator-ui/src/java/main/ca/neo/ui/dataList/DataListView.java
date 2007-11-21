package ca.neo.ui.dataList;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Hashtable;

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
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ca.neo.model.Network;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;

public class DataListView extends JPanel implements TreeSelectionListener {

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

	private SimulatorDataModel dataModel;
	private JTree tree;

	public DataListView(SimulatorDataModel data) {
		super(new GridLayout(1, 0));

		this.dataModel = data;

		// Create a tree that allows one selection at a time.
		tree = new JTree(dataModel);
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);

		dataModel.addTreeModelListener(new MyTreeModelListener());
		tree.addMouseListener(new MyTreeMouseListener());

		// Listen for when the selection changes.
		tree.addTreeSelectionListener(this);
		if (playWithLineStyle) {
			tree.putClientProperty("JTree.lineStyle", lineStyle);
		}

		JScrollPane scrollPane = new JScrollPane(tree);
		add(scrollPane);

		Dimension minimumSize = new Dimension(100, 50);
		scrollPane.setMinimumSize(minimumSize);
	}

	/** Required by TreeSelectionListener interface. */
	public void valueChanged(TreeSelectionEvent e) {
		// DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
		// .getLastSelectedPathComponent();
		//
		// if (node == null)
		// return;
		//
		// Object nodeInfo = node.getUserObject();
		// if (node.isLeaf()) {
		//
		// System.out.println("Leaf Node clicked: " + node.toString());
		//
		// } else {
		// System.out.println("Node clicked: " + node.toString());
		// }
		// if (DEBUG) {
		// System.out.println(nodeInfo.toString());
		// }
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

	private class MyTreeMouseListener implements MouseListener {

		private void DoubleMouseClicked(MouseEvent e) {
			TreePath[] paths = getTreePaths(e);
			if (paths.length == 1) {
				DataTreeNode dataNode = getDataNode(paths[0]);

				if (dataNode != null) {
					dataNode.getDefaultAction().doAction();
				}
			}
		}

		private DataTreeNode getDataNode(TreePath treePath) {
			Object selectedNode = treePath.getLastPathComponent();

			if (selectedNode instanceof DataTreeNode) {
				return (DataTreeNode) selectedNode;

			}
			return null;
		}

		private MutableTreeNode[] getTreeNodes(TreePath[] treePaths) {

			ArrayList<MutableTreeNode> treeNodes = new ArrayList<MutableTreeNode>(
					treePaths.length);

			for (TreePath path : treePaths) {
				Object obj = path.getLastPathComponent();
				if (obj instanceof MutableTreeNode) {
					treeNodes.add((MutableTreeNode) obj);
				}
			}
			return treeNodes.toArray(new MutableTreeNode[0]);
		}

		private TreePath[] getTreePaths(MouseEvent e) {
			Object source = e.getSource();
			TreePath[] paths;
			if (source instanceof JTree) {
				JTree tree = (JTree) source;

				paths = tree.getSelectionPaths();

			} else {
				paths = new TreePath[0];
			}
			return paths;
		}

		private void LeftMouseClicked(MouseEvent e) {

			JPopupMenu menu = null;

			TreePath[] paths = getTreePaths(e);

			DataTreeNode dataNode = null;
			if (paths.length == 1) {
				dataNode = getDataNode(paths[0]);
			}

			if (dataNode != null) {
				menu = dataNode.showContextMenu();
			} else {
				PopupMenuBuilder menuBuilder = new PopupMenuBuilder(null);
				MutableTreeNode[] treeNodes = getTreeNodes(paths);
				menuBuilder.addAction(new RemoveTreeNodes(treeNodes));
				menu = menuBuilder.toJPopupMenu();
			}
			if (menu != null) {
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

	private static class UndoInfo {
		TreeNode nodeParent;
		int nodeIndex;

		public UndoInfo(TreeNode nodeParent, int nodeIndex) {
			super();
			this.nodeParent = nodeParent;
			this.nodeIndex = nodeIndex;
		}
	}

	private class RemoveTreeNodes extends ReversableAction {

		private static final long serialVersionUID = 1L;

		private MutableTreeNode[] nodesToRemove;

		Hashtable<MutableTreeNode, UndoInfo> undoLUT;

		public RemoveTreeNodes(MutableTreeNode[] nodesToRemove) {
			super("Clear data");

			this.nodesToRemove = nodesToRemove;
		}

		@Override
		protected void action() throws ActionException {
			undoLUT = new Hashtable<MutableTreeNode, UndoInfo>(
					nodesToRemove.length);

			for (MutableTreeNode nodeToRemove : nodesToRemove) {
				TreeNode nodeParent = nodeToRemove.getParent();
				int nodeIndex = nodeParent.getIndex(nodeToRemove);

				undoLUT.put(nodeToRemove, new UndoInfo(nodeParent, nodeIndex));

				dataModel.removeNodeFromParent(nodeToRemove);
			}
		}

		@Override
		protected void undo() throws ActionException {
			int numOfFailures = 0;

			/*
			 * To maintain same node order as before the removal. we add back in
			 * the reverse order we remove them.
			 */
			for (int i = nodesToRemove.length - 1; i >= 0; i--) {
				MutableTreeNode nodeToRemove = nodesToRemove[i];

				UndoInfo undoInfo = undoLUT.get(nodeToRemove);
				if (undoInfo != null) {
					if (undoInfo.nodeParent instanceof MutableTreeNode) {
						dataModel.insertNodeInto(nodeToRemove,
								(MutableTreeNode) undoInfo.nodeParent,
								undoInfo.nodeIndex);
					} else {
						numOfFailures++;
					}

				} else {
					numOfFailures++;
				}

			}

			if (numOfFailures > 0) {
				UserMessages.showWarning("Undo clear failed for "
						+ numOfFailures + " nodes");
			}

		}
	}

}

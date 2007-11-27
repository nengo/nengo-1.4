package ca.neo.ui.dataList;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ca.neo.io.DelimitedFileExporter;
import ca.neo.model.Network;
import ca.neo.ui.util.FileExtensionFilter;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;

public class DataListView extends JPanel implements TreeSelectionListener {

	private static final long serialVersionUID = 1L;

	public static final String DATA_FILE_EXTENSION = "txt";

	static Network myNetwork;

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	public static JDialog createViewer(Frame owner,
			SimulatorDataModel simulatorData) {

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
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		dataModel.addTreeModelListener(new MyTreeModelListener());
		tree.addMouseListener(new MyTreeMouseListener());

		// Listen for when the selection changes.
		tree.addTreeSelectionListener(this);

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

		private void DoubleClickEvent(MouseEvent e) {
			TreePath[] paths = getTreePaths(e);
			List<MutableTreeNode> leafNodes = getLeafNodes(paths);

			if (leafNodes.size() == 1
					&& leafNodes.get(0) instanceof DataTreeNode) {
				DataTreeNode dataNode = (DataTreeNode) (leafNodes.get(0));

				if (dataNode != null) {
					dataNode.getDefaultAction().doAction();
				}
			}
		}

		private List<MutableTreeNode> getLeafNodes(TreePath[] treePaths) {

			ArrayList<MutableTreeNode> treeNodes = new ArrayList<MutableTreeNode>(
					treePaths.length);

			Object treeRoot = dataModel.getRoot();

			for (TreePath path : treePaths) {
				Object obj = path.getLastPathComponent();
				if (obj != treeRoot && obj instanceof MutableTreeNode) {
					treeNodes.add((MutableTreeNode) obj);
				}
			}
			return treeNodes;
		}

		private TreePath[] getTreePaths(MouseEvent e) {
			Object source = e.getSource();
			TreePath[] paths = null;
			if (source instanceof JTree) {
				JTree tree = (JTree) source;

				paths = tree.getSelectionPaths();
			}
			if (paths == null) {
				paths = new TreePath[0];
			}

			return paths;
		}

		private void ContextMenuEvent(MouseEvent e) {

			JPopupMenu menu = null;

			TreePath[] paths = getTreePaths(e);
			List<MutableTreeNode> leafNodes = getLeafNodes(paths);

			if (leafNodes.size() > 0) {
				PopupMenuBuilder menuBuilder;

				if (leafNodes.size() == 1) {
					MutableTreeNode leafNode = leafNodes.get(0);

					menuBuilder = new PopupMenuBuilder(leafNode.toString());

					menuBuilder.addAction(new ExportData(DataListView.this,
							leafNode));

					if (leafNode instanceof DataTreeNode) {
						((DataTreeNode) leafNode)
								.constructPopupMenu(menuBuilder);
					}

				} else {
					menuBuilder = new PopupMenuBuilder(leafNodes.size()
							+ " nodes selected");

					menuBuilder.addAction(new PlotNodesAction(leafNodes));

				}

				menuBuilder.addAction(new RemoveTreeNodes(leafNodes));
				menu = menuBuilder.toJPopupMenu();

				if (menu != null) {
					menu.show(e.getComponent(), e.getPoint().x, e.getPoint().y);
					menu.setVisible(true);
				}
			}
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				ContextMenuEvent(e);
			} else if (e.getClickCount() == 2
					&& e.getButton() == MouseEvent.BUTTON1) {
				DoubleClickEvent(e);
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

	private class RemoveTreeNodes extends ReversableAction {

		private static final long serialVersionUID = 1L;

		private List<MutableTreeNode> nodesToRemove;

		Hashtable<MutableTreeNode, UndoInfo> undoLUT;

		public RemoveTreeNodes(List<MutableTreeNode> nodesToRemove) {
			super("Clear data");

			this.nodesToRemove = nodesToRemove;
		}

		@Override
		protected void action() throws ActionException {
			undoLUT = new Hashtable<MutableTreeNode, UndoInfo>(
					nodesToRemove.size());

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
			for (int i = nodesToRemove.size() - 1; i >= 0; i--) {
				MutableTreeNode nodeToRemove = nodesToRemove.get(i);

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

	private static class UndoInfo {
		int nodeIndex;
		TreeNode nodeParent;

		public UndoInfo(TreeNode nodeParent, int nodeIndex) {
			super();
			this.nodeParent = nodeParent;
			this.nodeIndex = nodeIndex;
		}
	}

}

/**
 * Describes how to build a folder structure for a wrapped data node when it is
 * being exported
 * 
 * @author Shu Wu
 */
class DataFilePath {
	private DataTreeNode dataNode;
	private Collection<String> position;

	public DataFilePath(DataTreeNode dataNode, Collection<String> position) {
		super();
		this.dataNode = dataNode;
		this.position = position;
	}

	public Collection<String> getFolderPath() {
		return position;
	}

	public DataTreeNode getDataNode() {
		return dataNode;
	}

}

class DataFileChooser extends JFileChooser {

	public DataFileChooser() {
		super();
		setAcceptAllFileFilterUsed(false);
	}

	private static final long serialVersionUID = 1L;

}

class DataFileFilter extends FileExtensionFilter {

	@Override
	public boolean acceptExtension(String extension) {
		if (extension.compareTo(DataListView.DATA_FILE_EXTENSION) == 0) {
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Delimited Text File";
	}

}

class ExportData extends StandardAction {

	private static final long serialVersionUID = 1L;
	private MutableTreeNode nodeToExport;
	private DelimitedFileExporter fileExporter;
	private static DataFileChooser fileChooser = new DataFileChooser();
	private Component parent;
	private static final DataFileFilter DATA_FILE_FILTER = new DataFileFilter();

	public ExportData(Component parent, MutableTreeNode nodeToExport) {
		super(getActionDescription(nodeToExport));
		this.nodeToExport = nodeToExport;
		fileExporter = new DelimitedFileExporter();
		this.parent = parent;

	}

	private static String getActionDescription(MutableTreeNode node) {
		if (isSingleFileExport(node)) {
			return "Export to file";
		} else {
			return "Export to folder";
		}
	}

	private static boolean isSingleFileExport(MutableTreeNode node) {
		if (node.getChildCount() == 0 && node instanceof DataTreeNode) {
			return true;
		}
		return false;
	}

	@Override
	protected void action() throws ActionException {
		try {
			if (isSingleFileExport(nodeToExport)) {
				File file = getUserSelectedFile(DATA_FILE_FILTER);

				if (file != null) {
					exportNode((DataTreeNode) nodeToExport, file);
				}

				/*
				 * Export one data item
				 */
			} else {
				exportAllDataNodes();
			}

		} catch (IOException e) {
			e.printStackTrace();
			UserMessages.showWarning("Could not export: " + e.getMessage());
		}
	}

	private File getUserSelectedFolder() {
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int result = fileChooser.showSaveDialog(parent);

		if (result == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		} else {
			return null;
		}

	}

	private File getUserSelectedFile(FileFilter filter) {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(filter);

		int result = fileChooser.showSaveDialog(parent);

		if (result == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		} else {
			return null;
		}

	}

	private void exportNode(DataTreeNode node, File file) throws IOException,
			ActionException {
		if (node instanceof SpikePatternNode) {
			fileExporter.export((((SpikePatternNode) node).getUserObject()),
					file);
		} else if (node instanceof TimeSeriesNode) {
			fileExporter
					.export((((TimeSeriesNode) node).getUserObject()), file);
		} else {
			throw new ActionException("Could not export node type: "
					+ node.getClass().getSimpleName());
		}
	}

	private void findDataItemsRecursive(MutableTreeNode node,
			ArrayList<String> position, Collection<DataFilePath> dataItemsPaths) {

		if (node instanceof DataTreeNode) {
			DataTreeNode dataNode = (DataTreeNode) node;

			if (dataNode.includeInExport()) {
				DataFilePath dataP = new DataFilePath(dataNode, position);
				dataItemsPaths.add(dataP);
			}
		}

		Enumeration<?> children = node.children();
		while (children.hasMoreElements()) {
			Object obj = children.nextElement();
			if (obj instanceof MutableTreeNode) {
				MutableTreeNode childNode = (MutableTreeNode) obj;

				@SuppressWarnings("unchecked")
				ArrayList<String> childStack = (ArrayList<String>) position
						.clone();

				String childName = childNode.toString();
				childStack.add(childName);

				findDataItemsRecursive(childNode, childStack, dataItemsPaths);

			}
		}
	}

	private void exportAllDataNodes() throws IOException, ActionException {

		/*
		 * Use recursion to find data items
		 */
		File folder = getUserSelectedFolder();

		folder.mkdir();
		if (folder != null) {

			ArrayList<DataFilePath> dataFilePaths = new ArrayList<DataFilePath>();
			findDataItemsRecursive(nodeToExport, new ArrayList<String>(),
					dataFilePaths);

			for (DataFilePath dataPath : dataFilePaths) {

				Collection<String> folderPath = dataPath.getFolderPath();
				DataTreeNode dataNode = dataPath.getDataNode();

				File dataFile = folder;
				for (String folderName : folderPath) {
					dataFile = new File(dataFile, folderName);
					dataFile.mkdir();

					if (!dataFile.exists()) {
						throw new ActionException("Problem creating folder: "
								+ dataFile.toString());
					}
				}

				dataFile = new File(dataFile, dataNode.toString() + "."
						+ DataListView.DATA_FILE_EXTENSION);

				exportNode(dataNode, dataFile);
			}

		}

	}
}
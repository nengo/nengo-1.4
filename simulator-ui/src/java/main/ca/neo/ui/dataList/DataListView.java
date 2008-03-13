package ca.neo.ui.dataList;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ca.neo.io.DelimitedFileExporter;
import ca.neo.io.MatlabExporter;
import ca.neo.ui.actions.ConfigureAction;
import ca.neo.ui.util.FileExtensionFilter;
import ca.neo.util.SpikePattern;
import ca.neo.util.TimeSeries;
import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;

public class DataListView extends JPanel implements TreeSelectionListener {

	private static final long serialVersionUID = 1L;

	public static final String DATA_FILE_EXTENSION = "txt";
	public static final String MATLAB_FILE_EXTENSION = "mat";

	private SimulatorDataModel dataModel;
	private JTree tree;

	public DataListView(SimulatorDataModel data) {
		super(new GridLayout(1, 0));

		this.dataModel = data;

		// Create a tree that allows one selection at a time.
		tree = new JTree(dataModel);
		Style.applyStyle(tree);
		DefaultTreeCellRenderer treeRenderer = new DefaultTreeCellRenderer();
		tree.setCellRenderer(treeRenderer);
		// treeRenderer.setBackground(Style.COLOR_BACKGROUND);
		Style.applyStyle(treeRenderer);

		// tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		dataModel.addTreeModelListener(new MyTreeModelListener());
		tree.addMouseListener(new MyTreeMouseListener());

		// Listen for when the selection changes.
		tree.addTreeSelectionListener(this);

		JScrollPane scrollPane = new JScrollPane(tree);
		scrollPane.setBorder(null);
		add(scrollPane);

		Dimension minimumSize = new Dimension(100, 50);
		scrollPane.setMinimumSize(minimumSize);
	}

	/** Required by TreeSelectionListener interface. */
	public void valueChanged(TreeSelectionEvent e) {
		/*
		 * Do nothing
		 */
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

	@SuppressWarnings("unchecked")
	private class MyTreeMouseListener implements MouseListener {

		private void ContextMenuEvent(MouseEvent e) {

			JPopupMenu menu = null;

			TreePath[] paths = getTreePaths(e);
			List<MutableTreeNode> leafNodes = getLeafNodes(paths);

			// Have no idea how many data nodes there are going to be. Might as
			// well make it proportional to the number of nodes.
			HashSet<DataTreeNode> dataNodes = new HashSet<DataTreeNode>(leafNodes.size());

			// Find data nodes in tree nodes
			for (MutableTreeNode treeNode : leafNodes) {
				RecursiveFindDataNodes(treeNode, dataNodes);
			}

			if (dataNodes.size() > 0) {
				PopupMenuBuilder menuBuilder;

				if (leafNodes.size() == 1) {
					MutableTreeNode leafNode = leafNodes.get(0);

					menuBuilder = new PopupMenuBuilder(leafNode.toString());

					menuBuilder
							.addAction(new ExportDelimitedFileAction(DataListView.this, leafNode));
					menuBuilder.addAction(new ExportMatlabAction(DataListView.this, leafNode));

					if (leafNode instanceof NeoTreeNode) {
						NeoTreeNode neoTreeNode = (NeoTreeNode) leafNode;

						if (neoTreeNode.getNeoNode() != null) {
							menuBuilder.addAction(new ConfigureAction("Configure", neoTreeNode
									.getNeoNode()));
						}
					}

					if (leafNode instanceof DataTreeNode) {
						((DataTreeNode) leafNode).constructPopupMenu(menuBuilder, dataModel);
					}

				} else {

					menuBuilder = new PopupMenuBuilder(dataNodes.size() + " data nodes selected");

					menuBuilder.addAction(new PlotNodesAction(dataNodes));

				}

				// make sure we don't remove the node representing the network
				List<MutableTreeNode> removeNodes= new ArrayList<MutableTreeNode>();
				TreeNode root=(TreeNode)dataModel.getRoot();
				for (MutableTreeNode node : leafNodes) {
					if (node.getParent()==root) {
						Enumeration<MutableTreeNode> childEnumerator = node.children();
						while (childEnumerator.hasMoreElements()) {
							removeNodes.add(childEnumerator.nextElement());
						}
					} else {
						removeNodes.add(node);
					}
				}				
				
				menuBuilder.addAction(new RemoveTreeNodes(removeNodes));
				menu = menuBuilder.toJPopupMenu();

				if (menu != null) {
					menu.show(e.getComponent(), e.getPoint().x, e.getPoint().y);
					menu.setVisible(true);
				}
			}
		}

		private void DoubleClickEvent(MouseEvent e) {
			TreePath[] paths = getTreePaths(e);
			List<MutableTreeNode> leafNodes = getLeafNodes(paths);

			if (leafNodes.size() == 1 && leafNodes.get(0) instanceof DataTreeNode) {
				DataTreeNode dataNode = (DataTreeNode) (leafNodes.get(0));

				if (dataNode != null) {
					dataNode.getDefaultAction().doAction();
				}
			}
		}

		private List<MutableTreeNode> getLeafNodes(TreePath[] treePaths) {

			ArrayList<MutableTreeNode> treeNodes = new ArrayList<MutableTreeNode>(treePaths.length);

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

		@SuppressWarnings("unchecked")
		private void RecursiveFindDataNodes(TreeNode topNode, HashSet<DataTreeNode> dataTreeNodes) {
			Enumeration<TreeNode> childEnumerator = topNode.children();
			while (childEnumerator.hasMoreElements()) {
				RecursiveFindDataNodes(childEnumerator.nextElement(), dataTreeNodes);
			}
			if (topNode instanceof DataTreeNode) {
				if (!dataTreeNodes.contains(topNode)) {
					dataTreeNodes.add((DataTreeNode) topNode);
				}
			}

		}

		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				ContextMenuEvent(e);
			} else if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
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
			undoLUT = new Hashtable<MutableTreeNode, UndoInfo>(nodesToRemove.size());

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
								(MutableTreeNode) undoInfo.nodeParent, undoInfo.nodeIndex);
					} else {
						numOfFailures++;
					}

				} else {
					numOfFailures++;
				}

			}

			if (numOfFailures > 0) {
				UserMessages.showWarning("Undo clear failed for " + numOfFailures + " nodes");
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
class DataPath {
	private DataTreeNode dataNode;
	private Collection<String> position;

	public DataPath(DataTreeNode dataNode, Collection<String> position) {
		super();
		this.dataNode = dataNode;
		this.position = position;
	}

	public DataTreeNode getDataNode() {
		return dataNode;
	}

	public Collection<String> getPath() {
		return position;
	}

}

abstract class ExportAction extends StandardAction {
	private static ExportFileChooser fileChooser = new ExportFileChooser();
	public static void findDataItemsRecursive(MutableTreeNode node, ArrayList<String> position,
			Collection<DataPath> dataItemsPaths) {

		if (node instanceof DataTreeNode) {
			DataTreeNode dataNode = (DataTreeNode) node;

			if (dataNode.includeInExport()) {
				DataPath dataP = new DataPath(dataNode, position);
				dataItemsPaths.add(dataP);
			}
		}

		Enumeration<?> children = node.children();
		while (children.hasMoreElements()) {
			Object obj = children.nextElement();
			if (obj instanceof MutableTreeNode) {
				MutableTreeNode childNode = (MutableTreeNode) obj;

				@SuppressWarnings("unchecked")
				ArrayList<String> childStack = (ArrayList<String>) position.clone();

				String childName = childNode.toString();
				childStack.add(childName);

				findDataItemsRecursive(childNode, childStack, dataItemsPaths);

			}
		}
	}

	private Component parent;

	private final MutableTreeNode rootNode;

	public ExportAction(Component parent, MutableTreeNode rootNodeToExport, String description) {
		super(description);
		this.rootNode = rootNodeToExport;
		this.parent = parent;
	}

	protected File getUserSelectedFile(ExtensionFileFilter filter) throws UserCancelledException {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(filter);
		fileChooser.setSelectedFile(new File("unnamed." + filter.getExtension()));

		int result = fileChooser.showSaveDialog(parent);

		if (result == JFileChooser.APPROVE_OPTION) {
			if (fileChooser.getSelectedFile().exists()) {
				int response = JOptionPane.showConfirmDialog(fileChooser,
						"File already exists, replace?", "Warning", JOptionPane.YES_NO_OPTION);

				if (response != JOptionPane.YES_OPTION) {
					throw new UserCancelledException();
				}
			}

			return fileChooser.getSelectedFile();
		} else {
			throw new UserCancelledException();
		}

	}

	protected File getUserSelectedFolder() throws UserCancelledException {
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int result = showSaveDialog();

		if (result == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		} else {
			throw new UserCancelledException();
		}

	}

	public MutableTreeNode getRootNode() {
		return rootNode;
	}

	public int showSaveDialog() throws HeadlessException {
		return fileChooser.showSaveDialog(parent);
	}

}

class ExportDelimitedFileAction extends ExportAction {
	private static final ExtensionFileFilter DATA_FILE_FILTER = new ExtensionFileFilter(
			"Delimited Text File", DataListView.DATA_FILE_EXTENSION);

	private static final long serialVersionUID = 1L;

	private static String getActionDescription(MutableTreeNode node) {
		if (isSingleFileExport(node)) {
			return "Export (Text) to file";
		} else {
			return "Export (Text) to folder";
		}
	}

	private static boolean isSingleFileExport(MutableTreeNode node) {
		if (node.getChildCount() == 0 && node instanceof DataTreeNode) {
			return true;
		}
		return false;
	}

	private DelimitedFileExporter fileExporter;

	public ExportDelimitedFileAction(Component parent, MutableTreeNode nodeToExport) {
		super(parent, nodeToExport, getActionDescription(nodeToExport));

		fileExporter = new DelimitedFileExporter();
	}

	private void exportAllDataNodes() throws IOException, ActionException {

		/*
		 * Use recursion to find data items
		 */
		File folder = getUserSelectedFolder();

		folder.mkdir();

		ArrayList<DataPath> dataFilePaths = new ArrayList<DataPath>();
		findDataItemsRecursive(getRootNode(), new ArrayList<String>(), dataFilePaths);

		if (dataFilePaths.size() == 0) {
			throw new ActionException("Nothing to export");
		} else {
			for (DataPath dataPath : dataFilePaths) {

				Collection<String> folderPath = dataPath.getPath();
				DataTreeNode dataNode = dataPath.getDataNode();

				File dataFile = folder;
				for (String folderName : folderPath) {
					dataFile = new File(dataFile, folderName);
					dataFile.mkdir();

					if (!dataFile.exists()) {
						throw new ActionException("Problem creating folder: " + dataFile.toString());
					}
				}

				dataFile = new File(dataFile, dataNode.toString() + "."
						+ DataListView.DATA_FILE_EXTENSION);

				exportNode(dataNode, dataFile);
			}

		}

	}

	@Override
	protected void action() throws ActionException {
		try {
			if (isSingleFileExport(getRootNode())) {
				File file = getUserSelectedFile(DATA_FILE_FILTER);

				exportNode((DataTreeNode) getRootNode(), file);

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

	protected void exportNode(DataTreeNode node, File file) throws IOException, ActionException {
		if (node instanceof SpikePatternNode) {
			fileExporter.export((((SpikePatternNode) node).getUserObject()), file);
		} else if (node instanceof TimeSeriesNode) {
			fileExporter.export((((TimeSeriesNode) node).getUserObject()), file);
		} else {
			throw new ActionException("Could not export node type: "
					+ node.getClass().getSimpleName());
		}
	}

}

class ExportFileChooser extends JFileChooser {

	private static final long serialVersionUID = 1L;

	public ExportFileChooser() {
		super();
		setAcceptAllFileFilterUsed(false);
	}

}

class ExportMatlabAction extends ExportAction {
	private static final ExtensionFileFilter MATLAB_FILE_FILTER = new ExtensionFileFilter(
			"Matlab File", DataListView.MATLAB_FILE_EXTENSION);
	private static final long serialVersionUID = 1L;
	private MatlabExporter matlabExporter;

	public ExportMatlabAction(Component parent, MutableTreeNode nodeToExport) {
		super(parent, nodeToExport, "Export (Matlab) to file");
		matlabExporter = new MatlabExporter();
	}

	@Override
	protected void action() throws ActionException {
		File file = getUserSelectedFile(MATLAB_FILE_FILTER);

		ArrayList<DataPath> dataPaths = new ArrayList<DataPath>();
		findDataItemsRecursive(getRootNode(), new ArrayList<String>(), dataPaths);

		if (dataPaths.size() == 0) {
			throw new ActionException("Nothing to export");
		} else {
			for (DataPath dataPath : dataPaths) {

				Collection<String> path = dataPath.getPath();
				DataTreeNode dataNode = dataPath.getDataNode();

				StringBuilder name = new StringBuilder(200);
				boolean first = true;
				for (String nodeName : path) {
					if (first) {
						first = false;
					} else {
						name.append(".");
					}

					name.append(nodeName);
				}

				addNode(dataNode, name.toString());
			}

			try {
				matlabExporter.write(file);
			} catch (IOException e) {
				throw new ActionException("Error writing file: " + e.getMessage());
			}
		}

	}

	protected void addNode(DataTreeNode node, String name) throws ActionException {

		if (node instanceof SpikePatternNode) {
			SpikePattern spikePattern = ((SpikePatternNode) node).getUserObject();
			matlabExporter.add(name, spikePattern);
		} else if (node instanceof TimeSeriesNode) {
			TimeSeries spikePattern = ((TimeSeriesNode) node).getUserObject();
			matlabExporter.add(name, spikePattern);
		} else {
			throw new ActionException("Could not export node type: "
					+ node.getClass().getSimpleName());
		}
	}
}

class ExtensionFileFilter extends FileExtensionFilter {

	private String description;
	private String extension;

	public ExtensionFileFilter(String description, String extension) {
		super();
		this.description = description;
		this.extension = extension;
	}

	@Override
	public boolean acceptExtension(String extension) {
		if (extension.compareTo(extension) == 0) {
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public String getExtension() {
		return extension;
	}

}
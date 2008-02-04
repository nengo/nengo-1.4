package ca.neo.ui.dataList;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import ca.neo.model.Ensemble;
import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.Probeable;
import ca.neo.util.Probe;
import ca.neo.util.SpikePattern;
import ca.neo.util.TimeSeries;
import ca.shu.ui.lib.util.Util;

public class SimulatorDataModel extends DefaultTreeModel {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new node in the parent only if a node with the same name does
	 * not already exist
	 * 
	 * @param parent
	 * @param newNodeName
	 */
	private static SortableMutableTreeNode createSortableNode(DefaultMutableTreeNode parent,
			Node neoNode) {
		String name = neoNode.getName();

		if (neoNode instanceof Network) {
			name += " (Network)";
		}

		SortableMutableTreeNode newNode = findInDirectChildren(parent, neoNode.getName());

		if (newNode == null) {
			newNode = new NeoTreeNode(name, neoNode);
			parent.add(newNode);
		}

		return newNode;
	}

	private static SortableMutableTreeNode createSortableNode(DefaultMutableTreeNode parent,
			String name) {

		SortableMutableTreeNode newNode = findInDirectChildren(parent, name);

		if (newNode == null) {
			newNode = new SortableMutableTreeNode(name);
			parent.add(newNode);
		}

		return newNode;
	}

	/**
	 * Using O(n) search. Performance can be improved here.
	 * 
	 * @param parent
	 * @param name
	 * @return
	 */
	private static SortableMutableTreeNode findInDirectChildren(DefaultMutableTreeNode parent,
			String name) {

		Enumeration<?> enumeration = parent.children();
		SortableMutableTreeNode targetNode = null;

		while (enumeration.hasMoreElements()) {
			Object obj = enumeration.nextElement();
			if (obj instanceof SortableMutableTreeNode) {
				SortableMutableTreeNode node = (SortableMutableTreeNode) obj;

				if (node.getUserObject().toString().compareTo(name) == 0) {
					targetNode = node;
					break;
				}
			} else {
				throw new UnsupportedOperationException("An unsupported Node type was found");
			}
		}
		return targetNode;
	}

	private static void sortTree(MutableTreeNode node) {
		if (node instanceof SortableMutableTreeNode) {
			((SortableMutableTreeNode) node).sort();
		}

		if (!node.isLeaf()) {
			Enumeration<?> enumeration = node.children();

			while (enumeration.hasMoreElements()) {
				Object obj = enumeration.nextElement();

				if (obj instanceof MutableTreeNode) {
					sortTree(((MutableTreeNode) obj));
				}
			}
		}
	}

	private HashSet<String> nameLUT = new HashSet<String>();

	private Hashtable<Integer, DefaultMutableTreeNode> topLevelNetworks = new Hashtable<Integer, DefaultMutableTreeNode>();

	public SimulatorDataModel() {
		super(new DefaultMutableTreeNode("root"));
	}

	private void addSpikePatterns(DefaultMutableTreeNode top, Network network) {
		Node[] nodes = network.getNodes();

		for (Node node : nodes) {
			if (node instanceof Ensemble) {
				Ensemble ensemble = (Ensemble) node;

				if (ensemble.isCollectingSpikes()) {

					SortableMutableTreeNode ensNode = createSortableNode(top, ensemble);
					/*
					 * Make a clone of the data
					 */
					SpikePattern spikePattern = (SpikePattern) Util.cloneSerializable(ensemble
							.getSpikePattern());
					DefaultMutableTreeNode spNode = new SpikePatternNode(spikePattern);
					ensNode.add(spNode);
				}

			} else if (node instanceof Network) {
				Network subNet = (Network) node;

				DefaultMutableTreeNode netNode = createSortableNode(top, subNet);

				addSpikePatterns(netNode, subNet);
			}
		}
	}

	private void addTimeSeries(DefaultMutableTreeNode top, Probe[] probes) {
		for (Probe probe : probes) {
			DefaultMutableTreeNode top0 = top;

			if (probe.getEnsembleName() != null && !probe.getEnsembleName().equals("")) {
				SortableMutableTreeNode ensembleNode = createSortableNode(top0, probe
						.getEnsembleName());
				top0 = ensembleNode;
			}

			Probeable target = probe.getTarget();
			if (target instanceof Node) {
				SortableMutableTreeNode targetNode = createSortableNode(top0, (Node) target);

				/*
				 * Make a clone of the data
				 */
				TimeSeries probeData = (TimeSeries) Util.cloneSerializable(probe.getData());

				DefaultMutableTreeNode stateNode = new ProbeDataNode(probeData, probe
						.getStateName());

				targetNode.add(stateNode);

			} else {
				Util.Assert(false, "Probe target is not a node");
			}

		}

	}

	/**
	 * Captures the current data from a network and copies it to this simulator
	 * data tree
	 */
	public void captureData(Network network) {
		
		Util.Assert(network.getSimulator() != null, "No simulator available for data view");

		DefaultMutableTreeNode networkNode = topLevelNetworks.get(network.hashCode());

		if (networkNode == null) {
			String name = network.getName();
			/*
			 * Create a unique name
			 */

			if (nameLUT.contains(name)) {
				int i = 2;

				while (nameLUT.contains(name)) {
					name = name + i;
				}

			}
			nameLUT.add(name);

			networkNode = new DefaultMutableTreeNode(name);
			topLevelNetworks.put(network.hashCode(), networkNode);

			this.insertNodeInto(networkNode, ((MutableTreeNode) getRoot()), 0);
			// add(networkNode);
		}

		Calendar cal = new GregorianCalendar();

		SortableMutableTreeNode captureNode = new SortableMutableTreeNode("Simulation "
				+ cal.get(Calendar.HOUR_OF_DAY) + "h" + cal.get(Calendar.MINUTE) + "m"
				+ cal.get(Calendar.SECOND) + "s " + cal.get(Calendar.MONTH) + "M"
				+ cal.get(Calendar.DATE) + "D");

		addSpikePatterns(captureNode, network);
		addTimeSeries(captureNode, network.getSimulator().getProbes());
		sortTree(captureNode);

		if (captureNode.getChildCount() == 0) {
			captureNode.add(new DefaultMutableTreeNode("no data collected"));
		}

		this.insertNodeInto(captureNode, networkNode, 0);
	}

}

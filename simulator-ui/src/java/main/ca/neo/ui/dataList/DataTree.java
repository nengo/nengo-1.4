package ca.neo.ui.dataList;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import ca.neo.model.Ensemble;
import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.Probeable;
import ca.neo.util.Probe;
import ca.neo.util.SpikePattern;
import ca.shu.ui.lib.util.Util;

public class DataTree extends DefaultTreeModel {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new node in the parent only if a node with the same name does
	 * not already exist
	 * 
	 * @param parent
	 * @param newNodeName
	 */
	private static DefaultMutableTreeNode createUniqueNode(
			DefaultMutableTreeNode parent, String newNodeName) {
		DefaultMutableTreeNode newNode = findInDirectChildren(parent,
				newNodeName);

		if (newNode == null) {
			newNode = new DefaultMutableTreeNode(newNodeName);
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
	private static DefaultMutableTreeNode findInDirectChildren(
			DefaultMutableTreeNode parent, String name) {

		Enumeration<?> enumeration = parent.children();
		DefaultMutableTreeNode targetNode = null;

		while (enumeration.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration
					.nextElement();

			if (node.getUserObject().toString().compareTo(name) == 0) {
				targetNode = node;
				break;
			}
		}
		return targetNode;
	}

	private Vector<String> nameLUT = new Vector<String>();

	private Hashtable<Network, DefaultMutableTreeNode> topLevelNetworks = new Hashtable<Network, DefaultMutableTreeNode>();

	public DataTree() {
		super(new DefaultMutableTreeNode("Data List"));
	}

	private void addSpikePatterns(DefaultMutableTreeNode top, Network network) {
		Node[] nodes = network.getNodes();

		for (Node node : nodes) {
			if (node instanceof Ensemble) {
				Ensemble ensemble = (Ensemble) node;

				if (ensemble.isCollectingSpikes()) {

					/*
					 * Clone the serialization pattern before storing it
					 */
					SpikePattern spikePattern = (SpikePattern) Util
							.cloneSerializable(ensemble.getSpikePattern());

					DefaultMutableTreeNode ensNode = createUniqueNode(top,
							ensemble.getName());

					DefaultMutableTreeNode spNode = new DefaultMutableTreeNode(
							new SpikePatternWrapper(spikePattern));
					ensNode.add(spNode);
				}

			} else if (node instanceof Network) {
				Network subNet = (Network) node;

				DefaultMutableTreeNode netNode = createUniqueNode(top, subNet
						.getName()
						+ " (Network)");

				addSpikePatterns(netNode, subNet);
			}
		}
	}

	private void addTimeSeries(DefaultMutableTreeNode top, Probe[] probes) {
		for (Probe probe : probes) {
			DefaultMutableTreeNode top0 = top;

			if (probe.getEnsembleName() != null
					&& probe.getEnsembleName().compareTo("") != 0) {
				DefaultMutableTreeNode ensembleNode = createUniqueNode(top0,
						probe.getEnsembleName());
				top0 = ensembleNode;
			}

			Probeable target = probe.getTarget();
			if (target instanceof Node) {
				String targetNodeName = ((Node) target).getName();

				DefaultMutableTreeNode targetNode = createUniqueNode(top0,
						targetNodeName);

				DefaultMutableTreeNode stateNode = new DefaultMutableTreeNode(
						probe.getStateName() + " (Probe data)");
				targetNode.add(stateNode);

			}

		}

	}

	/**
	 * Captures the current data from a network and copies it to this simulator
	 * data tree
	 */
	public void captureData(Network network) {
		Util.Assert(network.getSimulator() != null,
				"No simulator available for data view");

		DefaultMutableTreeNode networkNode = topLevelNetworks.get(network);

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
			topLevelNetworks.put(network, networkNode);

			this.insertNodeInto(networkNode, ((MutableTreeNode) getRoot()), 0);
			// add(networkNode);
		}

		Date date = new Date();

		DefaultMutableTreeNode captureNode = new DefaultMutableTreeNode(date
				.toString());

		this.insertNodeInto(captureNode, networkNode, 0);

		addSpikePatterns(captureNode, network);
		addTimeSeries(captureNode, network.getSimulator().getProbes());
	}

	static class SpikePatternWrapper {
		SpikePattern spikePattern;

		public SpikePatternWrapper(SpikePattern spikePattern) {
			super();
			this.spikePattern = spikePattern;
		}

		public String toString() {
			return "Spike Pattern";
		}

	}

}

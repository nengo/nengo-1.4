package ca.neo.ui.dataList;

import java.util.Date;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

import ca.neo.model.Ensemble;
import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.Probeable;
import ca.neo.util.Probe;
import ca.neo.util.SpikePattern;
import ca.shu.ui.lib.util.Util;

public class DataTree extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;

	private Network network;

	public DataTree(Network network) {
		super(network.getName() + " Data");

		// DefaultMutableTreeNode top = new DefaultMutableTreeNode(name);

		this.network = network;

	}

	/**
	 * Captures the current data from a network and copies it to this simulator
	 * data tree
	 */
	public void captureData() {
		Util.Assert(network.getSimulator() != null,
				"No simulator available for data view");

		Date date = new Date();

		DefaultMutableTreeNode captureNode = new DefaultMutableTreeNode(date
				.toString());
		add(captureNode);

		addSpikePatterns(captureNode, network);
		addTimeSeries(captureNode, network.getSimulator().getProbes());
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

			}
			// else if (node instanceof Network) {
			// Network subNet = (Network) node;
			//
			// DefaultMutableTreeNode netNode = createUniqueNode(top, subNet
			// .getName()
			// + " (Network)");
			//
			// addSpikePatterns(netNode, subNet);
			// }
		}
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

	public Network getNetwork() {
		return network;
	}
}

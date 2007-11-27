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

	private HashSet<String> nameLUT = new HashSet<String>();

	private Hashtable<Integer, DefaultMutableTreeNode> topLevelNetworks = new Hashtable<Integer, DefaultMutableTreeNode>();

	public SimulatorDataModel() {
		super(new DefaultMutableTreeNode("Data List"));

	}

	private void addSpikePatterns(DefaultMutableTreeNode top, Network network) {
		Node[] nodes = network.getNodes();

		for (Node node : nodes) {
			if (node instanceof Ensemble) {
				Ensemble ensemble = (Ensemble) node;

				if (ensemble.isCollectingSpikes()) {

					DefaultMutableTreeNode ensNode = createUniqueNode(top,
							ensemble.getName());
					/*
					 * Make a clone of the data
					 */
					SpikePattern spikePattern = (SpikePattern) Util
							.cloneSerializable(ensemble.getSpikePattern());
					DefaultMutableTreeNode spNode = new SpikePatternNode(
							spikePattern);
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

				/*
				 * Make a clone of the data
				 */
				TimeSeries probeData = (TimeSeries) Util
						.cloneSerializable(probe.getData());

				DefaultMutableTreeNode stateNode = new TimeSeriesNode(
						probeData, probe.getStateName());

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
		Util.Assert(network.getSimulator() != null,
				"No simulator available for data view");

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

		DefaultMutableTreeNode captureNode = new DefaultMutableTreeNode(
				"Simulation " + cal.get(Calendar.HOUR_OF_DAY) + "h"
						+ cal.get(Calendar.MINUTE) + "m"
						+ cal.get(Calendar.SECOND) + "s "
						+ cal.get(Calendar.MONTH) + "M"
						+ cal.get(Calendar.DATE) + "D");

		addSpikePatterns(captureNode, network);
		addTimeSeries(captureNode, network.getSimulator().getProbes());

		if (captureNode.getChildCount() == 0) {
			captureNode.add(new DefaultMutableTreeNode("no data collected"));
		}

		this.insertNodeInto(captureNode, networkNode, 0);
	}

}

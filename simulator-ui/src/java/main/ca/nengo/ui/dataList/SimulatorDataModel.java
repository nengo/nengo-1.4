/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "SimulatorDataModel.java". Description: 
""

The Initial Developer of the Original Code is Bryan Tripp & Centre for Theoretical Neuroscience, University of Waterloo. Copyright (C) 2006-2008. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of the GNU 
Public License license (the GPL License), in which case the provisions of GPL 
License are applicable  instead of those above. If you wish to allow use of your 
version of this file only under the terms of the GPL License and not to allow 
others to use your version of this file under the MPL, indicate your decision 
by deleting the provisions above and replace  them with the notice and other 
provisions required by the GPL License.  If you do not delete the provisions above,
a recipient may use your version of this file under either the MPL or the GPL License.
 */

package ca.nengo.ui.dataList;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import ca.nengo.model.Ensemble;
import ca.nengo.model.Network;
import ca.nengo.model.Node;
import ca.nengo.model.Probeable;
import ca.nengo.util.Probe;
import ca.nengo.util.SpikePattern;
import ca.nengo.util.TimeSeries;
import ca.shu.ui.lib.util.Util;

public class SimulatorDataModel extends DefaultTreeModel {

	private static final long serialVersionUID = 1L;
	private ProbePlotHelper plotterStrategy;

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
		plotterStrategy = ProbePlotHelper.getInstance();
		this.setRoot(new DefaultMutableTreeNode("Results"));
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
					SpikePattern spikePattern = (SpikePattern) Util.cloneSerializable(ensemble.getSpikePattern());
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
				SortableMutableTreeNode ensembleNode = createSortableNode(top0,
						probe.getEnsembleName());
				top0 = ensembleNode;
			}

			Probeable target = probe.getTarget();
			if (target instanceof Node) {
				SortableMutableTreeNode targetNode = createSortableNode(top0, (Node) target);

				/*
				 * Make a clone of the data
				 */
				TimeSeries probeData = (TimeSeries) Util.cloneSerializable(probe.getData());

				DefaultMutableTreeNode stateNode = new ProbeDataNode(probeData,
						probe.getStateName(), plotterStrategy.isApplyTauFilterByDefault(probe));

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
	public SortableMutableTreeNode captureData(Network network) {

		Util.Assert(network.getSimulator() != null, "No simulator available for data view");

		DefaultMutableTreeNode networkNode = topLevelNetworks.get(network.hashCode());

		if (networkNode != null && networkNode.getParent() == null) {
			// Node has already been removed from the tree by the user
			//
			topLevelNetworks.remove(network.hashCode());
			nameLUT.remove(network.getName());
			networkNode = null;
		}

		if (networkNode == null) {
			String originalName = network.getName();

			String name = originalName;

			/*
			 * Ensure a unique name
			 */
			int i = 1;
			while (nameLUT.contains(name)) {
				name = String.format("%s (%d)", originalName, i++);
			}
			nameLUT.add(name);

			networkNode = new DefaultMutableTreeNode(name);
			topLevelNetworks.put(network.hashCode(), networkNode);

			this.insertNodeInto(networkNode, ((MutableTreeNode) getRoot()), 0);
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
		return captureNode;
	}

}

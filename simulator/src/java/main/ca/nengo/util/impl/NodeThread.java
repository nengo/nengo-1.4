package ca.nengo.util.impl;

import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Node;
import ca.nengo.model.Projection;
import ca.nengo.model.SimulationException;

public class NodeThread extends Thread {

	private NodeThreadPool myNodeThreadPool;

	private Node[] myNodes;
	private int myStartIndexInNodes;
	private int myEndIndexInNodes;
	
	private Projection[] myProjections;
	private int myStartIndexInProjections;
	private int myEndIndexInProjections;
	
	private float startTime;
	private float endTime;

	public NodeThread(NodeThreadPool nodePool, Node[] nodes, 
			int startIndexInNodes, int endIndexInNodes,
			Projection[] projections, int startIndexInProjections,
			int endIndexInProjections) {
		
		myNodeThreadPool = nodePool;
		
		myNodes = nodes;
		myProjections = projections;
		
		myStartIndexInNodes = startIndexInNodes;
		myEndIndexInNodes = endIndexInNodes;
		
		myStartIndexInProjections = startIndexInProjections;
		myEndIndexInProjections = endIndexInProjections;
	}

	public void waitForPool() {
		try {
			myNodeThreadPool.threadWait();
		} catch (Exception e) {
		}
	}

	public void finished() {
		try {
			myNodeThreadPool.threadFinished();
		} catch (Exception e) {
		}
	}

	public void run() {
		try {
			int i;
			float startTime, endTime;

			waitForPool();

			while (true) {
				startTime = myNodeThreadPool.getStartTime();
				endTime = myNodeThreadPool.getEndTime();

				for (i = myStartIndexInProjections; i < myEndIndexInProjections; i++) {
					InstantaneousOutput values = myProjections[i].getOrigin()
							.getValues();
					myProjections[i].getTermination().setValues(values);
				}

				finished();

				for (i = myStartIndexInNodes; i < myEndIndexInNodes; i++) {
					myNodes[i].run(startTime, endTime);
				}

				finished();

				// This is the means of getting out of the loop. The pool will interrupt
				// this thread at the appropriate time.
				if (Thread.currentThread().isInterrupted() || myNodeThreadPool.getRunFinished()) {
					return;
				}
			}
		} catch (SimulationException e) {
		}
	}
}
package ca.nengo.util.impl;

import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Node;
import ca.nengo.model.Projection;
import ca.nengo.model.SimulationException;
import ca.nengo.util.ThreadTask;

public class NodeThread extends Thread {

	private NodeThreadPool myNodeThreadPool;

	private Node[] myNodes;
	private int myStartIndexInNodes;
	private int myEndIndexInNodes;

	private Projection[] myProjections;
	private int myStartIndexInProjections;
	private int myEndIndexInProjections;

	private ThreadTask[] myTasks;
	private int myStartIndexInTasks;
	private int myEndIndexInTasks;

	public NodeThread(NodeThreadPool nodePool, Node[] nodes,
			int startIndexInNodes, int endIndexInNodes,
			Projection[] projections, int startIndexInProjections,
			int endIndexInProjections, ThreadTask[] tasks,
            int startIndexInTasks, int endIndexInTasks) {

		myNodeThreadPool = nodePool;

		myNodes = nodes;
		myProjections = projections;
        myTasks = tasks;

		myStartIndexInNodes = startIndexInNodes;
		myEndIndexInNodes = endIndexInNodes;

		myStartIndexInProjections = startIndexInProjections;
		myEndIndexInProjections = endIndexInProjections;

		myStartIndexInTasks = startIndexInTasks;
		myEndIndexInTasks = endIndexInTasks;
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

	// might have to make these protected?
	protected void runProjections(float startTime, float endTime) throws SimulationException{
		for (int i = myStartIndexInProjections; i < myEndIndexInProjections; i++) {
			InstantaneousOutput values = myProjections[i].getOrigin().getValues();
			myProjections[i].getTermination().setValues(values);
		}
	}
	
	protected void runNodes(float startTime, float endTime) throws SimulationException{
		for (int i = myStartIndexInNodes; i < myEndIndexInNodes; i++) {
			myNodes[i].run(startTime, endTime);
		}
	}
	
	protected void runTasks(float startTime, float endTime) throws SimulationException {
		for (int i = myStartIndexInTasks; i < myEndIndexInTasks; i++) {
            myTasks[i].run(startTime, endTime);
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

				runProjections(startTime, endTime);

				finished();

				runNodes(startTime, endTime);

				finished();

                runTasks(startTime, endTime);

                finished();

				// This is the means of getting out of the loop. The pool will interrupt
				// this thread at the appropriate time.
				if (Thread.currentThread().isInterrupted() || myNodeThreadPool.getRunFinished()) {
					kill();
					return;
				}
			}
		} catch (SimulationException e) {
		}
	}
	
	protected void kill(){
	}
}

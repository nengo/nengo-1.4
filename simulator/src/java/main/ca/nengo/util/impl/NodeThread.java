package ca.nengo.util.impl;

import java.util.Date;

import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Node;
import ca.nengo.model.Projection;
import ca.nengo.model.SimulationException;
import ca.nengo.util.ThreadTask;

/**
 * A thread for running projections, nodes and tasks in. Projections are all runs before nodes, nodes before tasks.
 *
 * @author Eric Crawford
 */
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

	private boolean myCollectTimings;

	private double myAverageTimeOnProjectionsPerStep;
	private double myAverageTimeOnNodesPerStep;
	private double myAverageTimeOnTasksPerStep;

	private int myNumSteps;

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
		
		myNumSteps = 0;
		myAverageTimeOnProjectionsPerStep = 0;
		myAverageTimeOnNodesPerStep = 0;
		myAverageTimeOnTasksPerStep = 0;
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
			float startTime, endTime;

			waitForPool();

			while (true) {
				startTime = myNodeThreadPool.getStartTime();
				endTime = myNodeThreadPool.getEndTime();
				
				long projectionInterval, nodeInterval, taskInterval;
				
				projectionInterval = myCollectTimings ? new Date().getTime() : 0;
				
				runProjections(startTime, endTime);
				
				projectionInterval = myCollectTimings ? new Date().getTime() - projectionInterval : 0;

				finished();
				
				nodeInterval = myCollectTimings ? new Date().getTime() : 0;

				runNodes(startTime, endTime);
				
				nodeInterval = myCollectTimings ? new Date().getTime() - nodeInterval : 0;

				finished();
				
				taskInterval = myCollectTimings ? new Date().getTime() : 0;

                runTasks(startTime, endTime);
                
                taskInterval = myCollectTimings ? new Date().getTime() - taskInterval : 0;

                finished();
                
                if(myCollectTimings){
	                myAverageTimeOnProjectionsPerStep = (myAverageTimeOnProjectionsPerStep * myNumSteps + projectionInterval) / (myNumSteps + 1);
	                myAverageTimeOnNodesPerStep = (myAverageTimeOnNodesPerStep * myNumSteps + nodeInterval) / (myNumSteps + 1);
	                myAverageTimeOnTasksPerStep = (myAverageTimeOnTasksPerStep * myNumSteps + taskInterval) / (myNumSteps + 1);
	                
	                myNumSteps++;
                }
                
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
		if(myCollectTimings){
			StringBuffer timingOutput = new StringBuffer();
			timingOutput.append("Timings for thread: " + this.getName() + "\n");
			timingOutput.append("Average time processing projections per step: " + myAverageTimeOnProjectionsPerStep + " ms\n");
			timingOutput.append("Average time processing nodes per step: " + myAverageTimeOnNodesPerStep + " ms\n");
			timingOutput.append("Average time processing tasks per step: " + myAverageTimeOnTasksPerStep + " ms\n");
			
			System.out.print(timingOutput.toString());
		}
	}
	
	public void setCollectTimings(boolean myCollectTimings) {
		this.myCollectTimings = myCollectTimings;
	}
	
	public double getMyAverageTimeOnProjectionsPerStep() {
		return myAverageTimeOnProjectionsPerStep;
	}

	public double getMyAverageTimeOnNodesPerStep() {
		return myAverageTimeOnNodesPerStep;
	}

	public double getMyAverageTimeOnTasksPerStep() {
		return myAverageTimeOnTasksPerStep;
	}
}

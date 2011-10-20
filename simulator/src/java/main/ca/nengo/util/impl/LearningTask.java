package ca.nengo.util.impl;

import ca.nengo.util.ThreadTask;
import ca.nengo.model.SimulationException;
import ca.nengo.model.SpikeOutput;
import ca.nengo.model.StructuralException;
import ca.nengo.model.impl.PlasticEnsembleImpl;
import ca.nengo.model.impl.PlasticEnsembleTermination;

/**
 * Implementation of a ThreadTask to multithread learning in a plastic ensemble.
 *
 * This task will seperate the learning calculations such as getDerivative into indepdent
 * threadable tasks.
 * 
 * @author Jonathan Lai
 */

public class LearningTask implements ThreadTask {

    private PlasticEnsembleImpl myParent;
    private PlasticEnsembleTermination myTermination;

    private int startIdx;
    private int endIdx;
    private boolean finished;
    
    private float myStartTime;
    private float myEndTime;
    private float[][] myTransform;

    /**
     * @param parent Parent PlasticEnsemble of this task
     * @param termination PlasticEnsembleTermination that this task will learn on
     * @param start Starting index for the set of terminations to learn on
     * @param end Ending index for the set of terminations to learn on
     */
    public LearningTask(PlasticEnsembleImpl parent, PlasticEnsembleTermination termination, int start, int end) {
        myParent = parent;
        myTermination = termination;
        startIdx = start;
        endIdx = end;
        finished = true;
    }

    /**
     * @param copy LearningTask to copy the parent and termination values from
     * @param start Starting index for the set of terminations to learn on
     * @param end Ending index for the set of terminations to learn on
     */
    public LearningTask(LearningTask copy, int start, int end) {
        myParent = copy.myParent;
        myTermination = copy.myTermination;
        startIdx = start;
        endIdx = end;
        finished = copy.finished;
    }
    
	/**
	 * @see ca.nengo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
        finished = false;
	}
    
    /**
     * @see ca.nengo.model.util.ThreadTask#getParent()
     */
    public PlasticEnsembleImpl getParent() {
        return myParent;
    }
    
    /**
     * @see ca.nengo.model.util.ThreadTask#isFinished()
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Overrides run startTime and endTime
     * @param startTime Override for startTime
     * @param endTime Override for endTime
     */
    public void setTime(float startTime, float endTime) {
        myStartTime = startTime;
        myEndTime = endTime;
    }

    /**
     * Sets the reference to the transform array for learning
     * @param transform The array of transforms to use
     */
    public void setTransform(float[][] transform) {
        myTransform = transform;
    }
    
    /**
     * @see ca.nengo.model.util.ThreadTask#run(float, float)
     */
    public void run(float startTime, float endTime) throws SimulationException {
        if (!finished)
        {
            float[][] derivative;
			try {
                derivative = myTermination.getDerivative(myTransform, myTermination.getInput(), endTime, startIdx, endIdx);
			} catch (StructuralException e) {
				throw new SimulationException(e);
			}
            float scale = (myTermination.getInput() instanceof SpikeOutput) ? 1 : (myEndTime - myStartTime); 
            for (int i = startIdx; i < endIdx; i++) {
                for (int j = 0; j < myTransform[i].length; j++) {
                    myTransform[i][j] += derivative[i][j] * scale;
                }
            }
            finished = true;
        }
    }

    public LearningTask clone() throws CloneNotSupportedException {
        return (LearningTask) super.clone();
    }
    
}

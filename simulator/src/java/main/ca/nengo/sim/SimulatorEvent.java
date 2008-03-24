package ca.nengo.sim;

public class SimulatorEvent {

	public enum Type {
		STARTED, FINISHED, STEP_TAKEN
	};

	private float myProgress;
	private Type myType;

	public SimulatorEvent(float progress, Type type) {
		super();
		myProgress = progress;
		myType = type;
	}

	public float getProgress() {
		return myProgress;
	}

	public Type getType() {
		return myType;
	}

}

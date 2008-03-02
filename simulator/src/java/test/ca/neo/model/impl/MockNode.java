/**
 * 
 */
package ca.neo.model.impl;

import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;

/**
 * A Cloneable Node for testing copy&paste / drag&drop.
 * 
 * @author Bryan Tripp
 */
public class MockNode implements Node, Cloneable {

	private static final long serialVersionUID = 1L;

	private String myDocumentation;
	private String myName;
	private SimulationMode myMode;

	public MockNode(String name) {
		myName = name;
		myMode = SimulationMode.DEFAULT;
	}

	/**
	 * @see ca.neo.model.Node#getDocumentation()
	 */
	public String getDocumentation() {
		return myDocumentation;
	}

	/**
	 * @see ca.neo.model.Node#getName()
	 */
	public String getName() {
		return myName;
	}

	/**
	 * @see ca.neo.model.Node#getOrigin(java.lang.String)
	 */
	public Origin getOrigin(String name) throws StructuralException {
		return null;
	}

	/**
	 * @see ca.neo.model.Node#getOrigins()
	 */
	public Origin[] getOrigins() {
		return new Origin[0];
	}

	/**
	 * @see ca.neo.model.Node#getTermination(java.lang.String)
	 */
	public Termination getTermination(String name) throws StructuralException {
		return null;
	}

	/**
	 * @see ca.neo.model.Node#getTerminations()
	 */
	public Termination[] getTerminations() {
		return new Termination[0];
	}

	/**
	 * @see ca.neo.model.Node#run(float, float)
	 */
	public void run(float startTime, float endTime) throws SimulationException {
	}

	/**
	 * @see ca.neo.model.Node#setDocumentation(java.lang.String)
	 */
	public void setDocumentation(String text) {
		myDocumentation = text;
	}

	/**
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
	}

	/**
	 * @see ca.neo.model.SimulationMode.ModeConfigurable#getMode()
	 */
	public SimulationMode getMode() {
		return myMode;
	}

	/**
	 * @see ca.neo.model.SimulationMode.ModeConfigurable#setMode(ca.neo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
		myMode = mode;
	}

	/**
	 * @see ca.neo.util.VisiblyMutable#addChangeListener(ca.neo.util.VisiblyMutable.Listener)
	 */
	public void addChangeListener(Listener listener) {
	}

	/**
	 * @see ca.neo.util.VisiblyMutable#removeChangeListener(ca.neo.util.VisiblyMutable.Listener)
	 */
	public void removeChangeListener(Listener listener) {
	}

	@Override
	public MockNode clone() throws CloneNotSupportedException {
		return (MockNode) super.clone();
	}

	public static void main(String[] args) {
		MockNode node = new MockNode("test");
		node.setDocumentation("documentation");
		node.setMode(SimulationMode.APPROXIMATE);

		try {
			MockNode other = (MockNode) node.clone();
			System.out.println("equals? " + (node == other));
			System.out.println(other.getName());
			System.out.println(other.getDocumentation());
			System.out.println(other.getMode());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	public void setName(String myName) {
		this.myName = myName;
	}

}

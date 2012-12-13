/**
 * 
 */
package ca.nengo.model.impl;

import java.util.HashMap;

import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.SimulationException;
import ca.nengo.model.SimulationMode;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;
import ca.nengo.util.ScriptGenException;

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
	 * @see ca.nengo.model.Node#getDocumentation()
	 */
	public String getDocumentation() {
		return myDocumentation;
	}

	/**
	 * @see ca.nengo.model.Node#getName()
	 */
	public String getName() {
		return myName;
	}

	/**
	 * @see ca.nengo.model.Node#getOrigin(java.lang.String)
	 */
	public Origin getOrigin(String name) throws StructuralException {
		return null;
	}

	/**
	 * @see ca.nengo.model.Node#getOrigins()
	 */
	public Origin[] getOrigins() {
		return new Origin[0];
	}

	/**
	 * @see ca.nengo.model.Node#getTermination(java.lang.String)
	 */
	public Termination getTermination(String name) throws StructuralException {
		return null;
	}

	/**
	 * @see ca.nengo.model.Node#getTerminations()
	 */
	public Termination[] getTerminations() {
		return new Termination[0];
	}

	/**
	 * @see ca.nengo.model.Node#run(float, float)
	 */
	public void run(float startTime, float endTime) throws SimulationException {
	}

	/**
	 * @see ca.nengo.model.Node#setDocumentation(java.lang.String)
	 */
	public void setDocumentation(String text) {
		myDocumentation = text;
	}

	/**
	 * @see ca.nengo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
	}

	/**
	 * @see ca.nengo.model.SimulationMode.ModeConfigurable#getMode()
	 */
	public SimulationMode getMode() {
		return myMode;
	}

	/**
	 * @see ca.nengo.model.SimulationMode.ModeConfigurable#setMode(ca.nengo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
		myMode = mode;
	}

	/**
	 * @see ca.nengo.util.VisiblyMutable#addChangeListener(ca.nengo.util.VisiblyMutable.Listener)
	 */
	public void addChangeListener(Listener listener) {
	}

	/**
	 * @see ca.nengo.util.VisiblyMutable#removeChangeListener(ca.nengo.util.VisiblyMutable.Listener)
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

	public Node[] getChildren() {
		return new Node[0];
	}

	public String toScript(HashMap<String, Object> scriptData) throws ScriptGenException {

		return "";
	}
}

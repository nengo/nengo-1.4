package ca.shu.ui.lib.objects.lines;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import ca.shu.ui.lib.objects.GEdge;
import ca.shu.ui.lib.world.impl.WorldObject;
import edu.umd.cs.piccolo.PNode;

public class LineHolder extends WorldObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Collection<GEdge> edges;

	
	
	
	public LineHolder() {
		super();
		edges = new Vector<GEdge>(2);

		this.addPropertyChangeListener(PNode.PROPERTY_FULL_BOUNDS,
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent arg0) {
						layoutEdges();
					}
				});
		stateChanged();
	}

	public void addEdge(GEdge edge) {
		edges.add(edge);
	}

	public Collection<GEdge> getEdges() {
		return edges;
	}

	@Override
	protected void stateChanged() {
		// TODO Auto-generated method stub
		super.stateChanged();

		/*
		 * Translate node state to its edges
		 */
		GEdge.State edgeState;
		if (state == State.HIGHLIGHT) {
			edgeState = GEdge.State.HIGHLIGHT;
		} else {
			edgeState = GEdge.State.DEFAULT;
		}

		Iterator<GEdge> it = edges.iterator();
		while (it.hasNext()) {
			GEdge edge = it.next();
			edge.setState(edgeState);
		}
	}

	public void layoutEdges() {
		// Populate it... Then later, iterate over its elements
		Iterator<GEdge> it = getEdges().iterator();
		while (it.hasNext()) {
			GEdge edge = it.next();
			edge.updateEdge();

		}
	}

}

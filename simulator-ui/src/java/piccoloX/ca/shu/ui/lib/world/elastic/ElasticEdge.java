package ca.shu.ui.lib.world.elastic;

import ca.shu.ui.lib.world.piccolo.WorldObjectImpl;
import ca.shu.ui.lib.world.piccolo.primitives.PXEdge;

public class ElasticEdge extends PXEdge {

	private static final long serialVersionUID = 1L;

	public ElasticEdge(WorldObjectImpl startNode, WorldObjectImpl endNode, double length,
			boolean isDirected) {
		super(startNode, endNode, isDirected);
		init(length);
	}

	public ElasticEdge(WorldObjectImpl startNode, WorldObjectImpl endNode, double length) {
		super(startNode, endNode);
		init(length);
	}

	private double length;

	private void init(double length) {
		this.length = length;
	}

	public double getLength() {
		return length;
	}

}

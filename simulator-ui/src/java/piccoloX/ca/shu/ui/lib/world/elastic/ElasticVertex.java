package ca.shu.ui.lib.world.elastic;

import java.awt.geom.Point2D;

import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;

public class ElasticVertex extends DirectedSparseVertex {
	private final ElasticObject myObject;

	public ElasticVertex(ElasticObject nodeUI) {
		super();
		this.myObject = nodeUI;
	}

	public Point2D getLocation() {
		return myObject.getOffsetReal();
	}

}

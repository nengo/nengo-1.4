package ca.shu.ui.lib.world.elastic;

import java.awt.geom.Point2D;

import edu.uci.ics.jung.graph.impl.SimpleSparseVertex;

public class ElasticVertex extends SimpleSparseVertex {
	private final ElasticObject myObject;

	public ElasticVertex(ElasticObject nodeUI) {
		super();
		this.myObject = nodeUI;
	}

	public Point2D getLocation() {
		return myObject.getOffsetReal();
	}

	public double getRepulsionRange() {
		return myObject.getRepulsionRange();
	}

}

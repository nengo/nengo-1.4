package ca.nengo.ui.lib.world.elastic;

import java.awt.geom.Point2D;

import edu.uci.ics.jung.graph.impl.LeanSparseVertex;

public class ElasticVertex extends LeanSparseVertex {
	private final ElasticObject myObject;

	public ElasticVertex(ElasticObject nodeUI) {
		this.myObject = nodeUI;
	}

	public Point2D getLocation() {
		return myObject.getOffsetReal();
	}

	public double getRepulsionRange() {
		return myObject.getRepulsionRange();
	}

}

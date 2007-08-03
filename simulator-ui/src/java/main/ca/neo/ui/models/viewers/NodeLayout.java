package ca.neo.ui.models.viewers;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Hashtable;

public class NodeLayout implements Serializable {

	private static final long serialVersionUID = 1L;

	Hashtable<String, Point2D> nodePositions;
	String layoutName;

	public NodeLayout(String layoutName) {
		super();
		this.layoutName = layoutName;
		nodePositions = new Hashtable<String, Point2D>();
	}

	public Point2D getPosition(String nodeName) {
		return nodePositions.get(nodeName);
	}

	public Point2D putPosition(String nodeName, Point2D position) {
		return nodePositions.put(nodeName, position);
	}

	public String getName() {
		return layoutName;
	}

}

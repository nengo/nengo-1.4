package ca.shu.ui.lib.world.elastic;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import ca.shu.ui.lib.objects.DirectedEdge;
import ca.shu.ui.lib.util.ElasticLayout;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.WorldGround;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.Layout;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

public class ElasticGround extends WorldGround {

	private static final long serialVersionUID = 1L;

	private DirectedSparseGraph myGraph;

	private ElasticLayoutRunner springLayoutRunner;

	public ElasticGround(ElasticWorld world, PLayer layer) {
		super(world, layer);
	}

	public ElasticLayout getElasticLayout() {
		if (springLayoutRunner != null) {
			return springLayoutRunner.getLayout();
		}
		return null;
	}

	public Point2D getElasticPosition(ElasticObject node) {
		if (getElasticLayout() != null) {
			ElasticVertex vertex = myVertexMap.get(node);
			if (vertex != null) {
				return getElasticLayout().getLocation(myVertexMap.get(node));
			}
		}
		return node.getOffsetReal();
	}

	/**
	 * @return A Graph used to by Jung Layouts
	 */
	public DirectedSparseGraph getGraph() {
		updateGraph();
		return myGraph;
	}

	private Hashtable<ElasticObject, ElasticVertex> myVertexMap = new Hashtable<ElasticObject, ElasticVertex>();

	@Override
	public ElasticWorld getWorld() {
		return (ElasticWorld) super.getWorld();
	}

	public boolean isAutoLayout() {
		if (getElasticLayout() != null) {
			return true;
		} else {
			return false;
		}
	}

	public void setElasticLayout(boolean enabled) {
		if (springLayoutRunner != null) {
			springLayoutRunner.stopLayout();
			springLayoutRunner = null;
		}
		if (enabled) {

			springLayoutRunner = new ElasticLayoutRunner(this);
			springLayoutRunner.start();
		}

	}

	public void setElasticPosition(ElasticObject node, double x, double y) {
		boolean doRealMove = true;

		if (getElasticLayout() != null) {
			ElasticVertex vertex = myVertexMap.get(node);
			if (vertex != null) {
				getElasticLayout().forceMove(vertex, x, y);

				if (getElasticLayout().isLocked(vertex)) {
					doRealMove = false;
				}
			}
		}
		if (doRealMove) {
			node.setOffsetReal(x, y);
		}
	}

	public void setElasticLock(ElasticObject node, boolean lockEnabled) {
		if (getElasticLayout() != null) {
			ElasticVertex vertex = myVertexMap.get(node);

			if (vertex != null) {
				if (lockEnabled) {
					getElasticLayout().lockVertex(vertex);
				} else {
					getElasticLayout().unlockVertex(vertex);
				}
			}
		}

	}

	public void updateChildrensFromLayout(Layout layout, boolean zoomToLayout) {
		/**
		 * Layout nodes
		 */
		boolean foundNode = false;
		Iterator<?> it = getChildrenIterator();

		double startX = Double.POSITIVE_INFINITY;
		double startY = Double.POSITIVE_INFINITY;
		double endX = Double.NEGATIVE_INFINITY;
		double endY = Double.NEGATIVE_INFINITY;

		while (it.hasNext()) {
			ElasticObject node = (ElasticObject) (it.next());

			ElasticVertex vertex = myVertexMap.get(node);
			if (vertex != null) {

				Point2D coord = layout.getLocation(vertex);

				if (coord != null) {
					foundNode = true;
					double x = coord.getX();
					double y = coord.getY();
					if (zoomToLayout) {
						node.animateToPositionScaleRotation(x, y, 1, 0, 1000);
					} else {
						node.setOffsetReal(x, y);
					}

					if (x < startX) {
						startX = x;
					}
					if (x + node.getWidth() > endX) {
						endX = x + node.getWidth();
					}

					if (y < startY) {
						startY = y;
					}
					if (y + node.getHeight() > endY) {
						endY = y + node.getHeight();
					}
				}
			}
		}

		if (zoomToLayout && foundNode) {
			PBounds fullBounds = new PBounds(startX, startY, endX - startX,
					endY - startY);
			getWorld().zoomToBounds(fullBounds);
		}

	}

	public void updateGraph() {
		if (myGraph == null) {
			myGraph = new DirectedSparseGraph();
		} else {
			myGraph.removeAllEdges();
		}

		/*
		 * TODO: Only update when network model and nodes are updated. This
		 * requires event notification from models.
		 */

		Iterator<?> it = getChildrenIterator();
		/**
		 * Add vertices
		 */
		while (it.hasNext()) {
			ElasticObject obj = (ElasticObject) it.next();

			ElasticVertex vertex = myVertexMap.get(obj);

			if (myVertexMap.get(obj) == null) {

				vertex = new ElasticVertex(obj);
				myGraph.addVertex(vertex);
				myVertexMap.put(obj, vertex);
			}
		}

		/**
		 * Remove vertices
		 */
		List<ElasticObject> elasticObjToRemove = new ArrayList<ElasticObject>();
		for (ElasticObject elasticObj : myVertexMap.keySet()) {
			if (elasticObj.getParent() != this) {
				ElasticVertex vertex = myVertexMap.get(elasticObj);
				myGraph.removeVertex(vertex);
				elasticObjToRemove.add(elasticObj);

			}
		}

		for (ElasticObject elasticObj : elasticObjToRemove) {
			myVertexMap.remove(elasticObj);
		}

		/**
		 * Convert UI Directed edges to Jung directed edges
		 */
		List<DirectedEdge> edges = getEdges();

		for (DirectedEdge uiEdge : edges) {
			PNode startNode = uiEdge.getStartNode();
			PNode endNode = uiEdge.getEndNode();

			// Find the Elastic Objects which are ancestors of the start and end
			// nodes
			while (startNode.getParent() != this && startNode != null) {
				startNode = startNode.getParent();
			}

			while (endNode.getParent() != this && endNode != null) {
				endNode = endNode.getParent();
			}

			if (startNode.getParent() == this && endNode.getParent() == this) {

				ElasticVertex startVertex = myVertexMap.get(startNode);
				ElasticVertex endVertex = myVertexMap.get(endNode);

				if (startVertex == null || endVertex == null) {
					System.out.println("one of these is null");
				}
				// ignore recursive connections
				if (startVertex != endVertex) {
					DirectedSparseEdge edge = new DirectedSparseEdge(
							startVertex, endVertex);
					myGraph.addEdge(edge);
				}
			} else {
				Util.Assert(false, "Could not find Elastic Nodes of edge");
			}

		}
	}

}

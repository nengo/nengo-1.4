package ca.shu.ui.lib.world.elastic;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import ca.shu.ui.lib.objects.DirectedEdge;
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

	public ElasticLayoutRunner getElasticLayout() {
		if (springLayoutRunner != null) {
			return springLayoutRunner;
		}
		return null;
	}

	public Point2D getElasticPosition(ElasticObject node) {
		if (getElasticLayout() != null) {
			ElasticVertex vertex = myVertexMap.get(node);
			if (vertex != null) {
				if (!getElasticLayout().isLocked(vertex)) {
					return getElasticLayout()
							.getLocation(myVertexMap.get(node));
				}
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

	private Hashtable<DirectedEdge, DirectedSparseEdge> myEdgeMap = new Hashtable<DirectedEdge, DirectedSparseEdge>();

	// private class EdgeMap {
	// private Hashtable<ElasticObject, HashSet<ElasticObject>> myEdgesMap = new
	// Hashtable<ElasticObject, HashSet<ElasticObject>>();
	//
	// private class ProjectionsSet extends HashSet<ElasticObject> {
	//
	// private static final long serialVersionUID = 1L;
	//
	// }
	//
	// public void containsEdge(ElasticObject startNode, ElasticObject endNode)
	// {
	// ProjectionsSet projectionsTo = myEdgesMap.get(startNode);
	//
	// if (projectionsTo != null) {
	// if (projectionsTo.contains(endNode)) {
	// return true;
	// }
	// }
	//
	// }
	//
	// public void clear() {
	// myEdgesMap.clear();
	// }
	// }

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
			myVertexMap.clear();
			myEdgeMap.clear();
			myGraph = null;
			springLayoutRunner = new ElasticLayoutRunner(this);
			springLayoutRunner.start();
		}

	}

	public void setElasticPosition(ElasticObject node, double x, double y) {
		boolean doRealMove = true;
		if (x == 0 || y == 0)
			return;

		if (getElasticLayout() != null) {
			ElasticVertex vertex = myVertexMap.get(node);
			if (vertex != null) {

				getElasticLayout().forceMove(vertex, x, y);
				if (!getElasticLayout().isLocked(vertex)) {
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

	public void updateChildrenFromLayout(Layout layout, boolean zoomToLayout) {
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

	/**
	 * @return True, if the graph changed
	 */
	public boolean updateGraph() {
		boolean changed = false;
		if (myGraph == null) {
			changed = true;
			myGraph = new DirectedSparseGraph();
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

			if (!myVertexMap.containsKey(obj)) {
				ElasticVertex vertex = new ElasticVertex(obj);
				myGraph.addVertex(vertex);
				myVertexMap.put(obj, vertex);
				changed = true;
			}
		}

		/**
		 * Remove vertices
		 */
		List<ElasticObject> elasticObjToRemove = new ArrayList<ElasticObject>();
		for (ElasticObject elasticObj : myVertexMap.keySet()) {
			if (elasticObj.getParent() != this) {
				elasticObjToRemove.add(elasticObj);
			}
		}

		for (ElasticObject elasticObj : elasticObjToRemove) {
			myGraph.removeVertex(myVertexMap.get(elasticObj));
			myVertexMap.remove(elasticObj);
			changed = true;
		}

		/**
		 * Add edges
		 */
		List<DirectedEdge> edges = getEdges();

		for (DirectedEdge uiEdge : edges) {

			PNode startNode = uiEdge.getStartNode();
			PNode endNode = uiEdge.getEndNode();

			// Find the Elastic Objects which are ancestors of the start and
			// end
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

				Util.Assert(startVertex != null && endVertex != null,
						"Could not find vertice");

				DirectedSparseEdge jungEdge = myEdgeMap.get(uiEdge);

				boolean createJungEdge = false;
				if (jungEdge != null) {
					// find if an existing edge has changed
					if (jungEdge.getSource() != startVertex
							|| jungEdge.getDest() != endVertex) {

						myEdgeMap.remove(uiEdge);
						myGraph.removeEdge(jungEdge);
						changed = true;

						// try to add the new changed one
						createJungEdge = true;
					}

				} else {
					createJungEdge = true;
				}

				if (createJungEdge) {
					// avoid recursive edges
					if (startVertex != endVertex) {
						jungEdge = new DirectedSparseEdge(startVertex,
								endVertex);
						myEdgeMap.put(uiEdge, jungEdge);

						myGraph.addEdge(jungEdge);
						changed = true;
					}
				}

			} else {
				Util.Assert(false, "Could not find Elastic Nodes of edge");
			}

		}
		/*
		 * Remove edges
		 */
		List<DirectedEdge> edgesToRemove = new ArrayList<DirectedEdge>();
		for (DirectedEdge uiEdge : myEdgeMap.keySet()) {
			if (!containsEdge(uiEdge)) {
				edgesToRemove.add(uiEdge);
				changed = true;
			}
		}
		for (DirectedEdge uiEdge : edgesToRemove) {
			myGraph.removeEdge(myEdgeMap.get(uiEdge));
			myEdgeMap.remove(uiEdge);
		}

		// Return whether the graph changed
		return changed;

	}

	@Override
	protected void prepareForDestroy() {
		setElasticLayout(false);
		super.prepareForDestroy();
	}

}

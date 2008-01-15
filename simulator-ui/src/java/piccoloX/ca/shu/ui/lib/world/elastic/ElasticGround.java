package ca.shu.ui.lib.world.elastic;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.IWorldObject;
import ca.shu.ui.lib.world.piccolo.WorldGroundImpl;
import ca.shu.ui.lib.world.piccolo.primitives.PXEdge;
import edu.uci.ics.jung.graph.impl.AbstractSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.visualization.Layout;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

public class ElasticGround extends WorldGroundImpl {

	private static final long serialVersionUID = 1L;

	private SparseGraph myGraph;

	private ElasticLayoutRunner elasticLayoutThread;
	private boolean childrenUpdatedFlag = false;

	public ElasticGround() {
		super();
		getPiccolo().addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().compareTo(PNode.PROPERTY_CHILDREN) == 0) {
					childrenUpdatedFlag = true;
				}
			}

		});
	}

	public Point2D getElasticPosition(ElasticObject node) {
		if (elasticLayoutThread != null) {
			ElasticVertex vertex = myVertexMap.get(node);
			if (vertex != null) {
				if (!elasticLayoutThread.isLocked(vertex)) {
					return elasticLayoutThread.getLocation(myVertexMap
							.get(node));
				}
			}
		}
		return node.getOffsetReal();
	}

	/**
	 * @return The current graph representation of the Ground.
	 */
	public SparseGraph getGraph() {
		return myGraph;
	}

	private Hashtable<ElasticObject, ElasticVertex> myVertexMap = new Hashtable<ElasticObject, ElasticVertex>();

	private Hashtable<PXEdge, AbstractSparseEdge> myEdgeMap = new Hashtable<PXEdge, AbstractSparseEdge>();

	@Override
	public ElasticWorld getWorld() {
		return (ElasticWorld) super.getWorld();
	}

	public boolean isElasticMode() {
		if (elasticLayoutThread != null) {
			return true;
		} else {
			return false;
		}
	}

	public void setElasticEnabled(boolean enabled) {
		if (elasticLayoutThread != null) {
			elasticLayoutThread.stopLayout();
			elasticLayoutThread = null;
		}
		if (enabled) {
			myVertexMap.clear();
			myEdgeMap.clear();
			myGraph = null;
			elasticLayoutThread = new ElasticLayoutRunner(this);
			elasticLayoutThread.start();
		}

	}

	public void setElasticPosition(ElasticObject node, double x, double y) {
		boolean doRealMove = true;
		if (x == 0 || y == 0)
			return;

		if (elasticLayoutThread != null) {
			ElasticVertex vertex = myVertexMap.get(node);
			if (vertex != null) {

				elasticLayoutThread.forceMove(vertex, x, y);
				if (!elasticLayoutThread.isLocked(vertex)) {
					doRealMove = false;
				}
			}
		}
		if (doRealMove) {
			node.setOffsetReal(x, y);
		}
	}

	/**
	 * Locks the position of an elastic node so it isn't affected by the layout
	 * runner
	 * 
	 * @param node
	 * @param lockEnabled
	 */
	public void setPositionLocked(ElasticObject node, boolean lockEnabled) {
		if (elasticLayoutThread != null) {
			ElasticVertex vertex = myVertexMap.get(node);

			if (vertex == null) {
				// Try to update the layout and get the vertex again
				// Node might have been updated recently and not added to the
				// graph
				elasticLayoutThread.updateLayout();
				vertex = myVertexMap.get(node);
			}

			if (vertex != null) {
				if (lockEnabled) {
					elasticLayoutThread.lockVertex(vertex);
				} else {
					elasticLayoutThread.unlockVertex(vertex);
				}
			} else {
				Util.Assert(false, "Vertex not found");
			}
		}

	}

	public boolean isPositionLocked(ElasticObject node) {
		if (elasticLayoutThread != null) {
			ElasticVertex vertex = myVertexMap.get(node);

			if (vertex != null) {
				return elasticLayoutThread.isLocked(vertex);
			}
		}
		return false;
	}

	public void updateChildrenFromLayout(Layout layout, boolean animateNodes,
			boolean zoomToLayout) {
		/**
		 * Layout nodes
		 */
		boolean foundNode = false;

		double startX = Double.POSITIVE_INFINITY;
		double startY = Double.POSITIVE_INFINITY;
		double endX = Double.NEGATIVE_INFINITY;
		double endY = Double.NEGATIVE_INFINITY;

		for (IWorldObject child : getChildren()) {
			ElasticObject elasticObj = (ElasticObject) (child);

			ElasticVertex vertex = myVertexMap.get(elasticObj);
			if (vertex != null) {

				Point2D coord = layout.getLocation(vertex);

				if (coord != null) {

					foundNode = true;
					double x = coord.getX();
					double y = coord.getY();

					if (elasticObj.isAnimating()) {
						// If the object is being animated in another process,
						// then we force update it's position in the layout
						x = elasticObj.getOffsetReal().getX();
						y = elasticObj.getOffsetReal().getY();
						if (elasticLayoutThread != null) {
							elasticLayoutThread.forceMove(vertex, x, y);
						}
					} else {
						x = coord.getX();
						y = coord.getY();
						if (animateNodes) {
							elasticObj.animateToPositionScaleRotation(x, y, 1,
									0, 1000);
						} else {
							elasticObj.setOffsetReal(x, y);
						}
					}

					if (x < startX) {
						startX = x;
					}
					if (x + elasticObj.getWidth() > endX) {
						endX = x + elasticObj.getWidth();
					}

					if (y < startY) {
						startY = y;
					}
					if (y + elasticObj.getHeight() > endY) {
						endY = y + elasticObj.getHeight();
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

	public static class UpdateGraphResult {
		private boolean graphUpdated;
		private Collection<ElasticVertex> addedVertices;

		public UpdateGraphResult(boolean graphUpdated,
				Collection<ElasticVertex> addedVertices) {
			super();
			this.graphUpdated = graphUpdated;
			this.addedVertices = addedVertices;
		}

		public boolean isGraphUpdated() {
			return graphUpdated;
		}

		public Collection<ElasticVertex> getAddedVertices() {
			return addedVertices;
		}

	}

	/**
	 * This method must be executed from the swing dispatcher thread because it
	 * must be synchronized with the Graphical children elements.
	 */
	public UpdateGraphResult updateGraph() {
		// if (!SwingUtilities.isEventDispatchThread()) {
		// Util.debugMsg("Update not dispatched from Swing thread");
		// }

		boolean changed = false;
		if (myGraph == null) {
			changed = true;
			childrenUpdatedFlag = true;
			myGraph = new SparseGraph();
		}
		Collection<ElasticVertex> addedVertexes = Collections.emptyList();
		if (childrenUpdatedFlag) {
			addedVertexes = new LinkedList<ElasticVertex>();
			childrenUpdatedFlag = false;

			/**
			 * Add vertices
			 */
			for (IWorldObject wo : getChildren()) {
				ElasticObject obj = (ElasticObject) wo;

				if (!myVertexMap.containsKey(obj)) {
					ElasticVertex vertex = new ElasticVertex(obj);
					myGraph.addVertex(vertex);
					myVertexMap.put(obj, vertex);
					addedVertexes.add(vertex);

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
		}

		/*
		 * TODO: Only update edges when they are changed. Have to figure out a
		 * way to know when edge start and end nodes are changed. This is tricky
		 * because the start and end nodes we're interested in are the Elastic
		 * Objects above the ground, which the dosen't know about
		 */

		/**
		 * Add edges
		 */
		Collection<PXEdge> edges = getEdges();

		for (PXEdge uiEdge : edges) {

			IWorldObject startNode = uiEdge.getStartNode();
			IWorldObject endNode = uiEdge.getEndNode();

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

				if (!(startVertex != null && endVertex != null)) {
					Util.Assert(false, "Could not find vertice");
				}

				AbstractSparseEdge jungEdge = myEdgeMap.get(uiEdge);

				boolean createJungEdge = false;
				if (jungEdge != null) {
					// find if an existing edge has changed
					if (jungEdge.getEndpoints().getFirst() != startVertex
							|| jungEdge.getEndpoints().getSecond() != endVertex) {

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
						if (uiEdge.isDirected()) {
							jungEdge = new DirectedSparseEdge(startVertex,
									endVertex);
						} else {
							jungEdge = new UndirectedSparseEdge(startVertex,
									endVertex);
						}

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
		List<PXEdge> edgesToRemove = new ArrayList<PXEdge>();
		for (PXEdge uiEdge : myEdgeMap.keySet()) {
			if (!containsEdge(uiEdge)) {
				edgesToRemove.add(uiEdge);

			}
		}
		for (PXEdge uiEdge : edgesToRemove) {
			AbstractSparseEdge jungEdge = myEdgeMap.get(uiEdge);
			changed = true;
			// have to check if the edge is still there because it might have
			// been removed when the vertice was removed
			if (myGraph.getEdges().contains(jungEdge)) {
				myGraph.removeEdge(jungEdge);
			}
			myEdgeMap.remove(uiEdge);
		}

		// Return whether the graph changed
		return new UpdateGraphResult(changed, addedVertexes);

	}

	@Override
	protected void prepareForDestroy() {
		setElasticEnabled(false);
		super.prepareForDestroy();
	}

}

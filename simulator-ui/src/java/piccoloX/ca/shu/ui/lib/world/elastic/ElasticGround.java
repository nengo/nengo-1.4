package ca.shu.ui.lib.world.elastic;

import java.awt.geom.Point2D;
import java.util.Iterator;

import ca.neo.ui.models.UINeoNode;
import ca.shu.ui.lib.util.ElasticLayout;
import ca.shu.ui.lib.world.WorldGround;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.Layout;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.util.PBounds;

public class ElasticGround extends WorldGround {

	private static final long serialVersionUID = 1L;

	private DirectedSparseGraph myGraph;

	private ElasticLayoutRunner springLayoutRunner;

	public ElasticGround(ElasticWorld world, PLayer layer) {
		super(world, layer);
	}

	public Point2D getElasticPosition(ElasticObject node) {
		if (getElasticLayout() != null) {
			return getElasticLayout().getLocation(node.getVertex());
		} else {
			return node.getOffsetReal();
		}

	}

	/**
	 * @return The child nodes of this network as a Graph object readable by
	 *         Jung Layout algorithms
	 */
	public DirectedSparseGraph getGraph(boolean updateFromModel) {
		if (myGraph == null) {
			myGraph = new DirectedSparseGraph();
			updateFromModel = true;
		}
		if (updateFromModel) {
			getWorld().updateGraph(myGraph);
		}
		return myGraph;
	}

	public void getNeoNodePosition(UINeoNode node, double x, double y) {
		if (getElasticLayout() != null) {
			getElasticLayout().forceMove(node.getVertex(), x, y);
		} else {
			node.setOffsetReal(x, y);
		}

	}

	public boolean isAutoLayout() {
		if (getElasticLayout() != null) {
			return true;
		} else {
			return false;
		}
	}

	public ElasticLayout getElasticLayout() {
		if (springLayoutRunner != null) {
			return springLayoutRunner.getLayout();
		}
		return null;
	}

	@Override
	public ElasticWorld getWorld() {
		return (ElasticWorld) super.getWorld();
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
		if (getElasticLayout() != null) {
			getElasticLayout().forceMove(node.getVertex(), x, y);
		} else {
			node.setOffsetReal(x, y);
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

			if (node.getVertex() != null) {

				Point2D coord = layout.getLocation(node.getVertex());

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

}

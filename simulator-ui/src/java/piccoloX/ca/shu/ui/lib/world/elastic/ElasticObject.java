package ca.shu.ui.lib.world.elastic;

import java.awt.geom.Point2D;

import ca.shu.ui.lib.world.WorldObject;
import edu.uci.ics.jung.graph.Vertex;
import edu.umd.cs.piccolo.PNode;

public class ElasticObject extends WorldObject {

	private static final long serialVersionUID = 1L;

	// Cache the Elastic world for fast access because it is used often
	private ElasticGround elasticGround;

	/**
	 * Representative vertex used by Jung layout algorithms
	 */
	private transient Vertex vertex;

	protected ElasticGround getElasticWorld() {
		return elasticGround;
	}

	@Override
	public Point2D getOffset() {
		if (getElasticWorld() != null) {
			return getElasticWorld().getElasticPosition(this);
		} else {
			return super.getOffset();
		}
	}

	/**
	 * This is the real getOffset function.
	 * 
	 * @param x
	 * @param y
	 */
	public Point2D getOffsetReal() {
		return super.getOffset();
	}

	/**
	 * @return Vertex to be used by Jung Graph visualization library
	 */
	public Vertex getVertex() {
		if (vertex == null) {
			vertex = new ElasticVertex(this);
		}
		return vertex;
	}

	@Override
	public void offset(double dx, double dy) {
		Point2D offset = getOffset();
		offset.setLocation(offset.getX() + dx, offset.getY() + dy);
		setOffset(offset);
	}

	/**
	 * @see edu.umd.cs.piccolo.PNode#setOffset(double, double)
	 *      <p>
	 *      If NetworkViewer exists as a parent, this becomes a re-direct to
	 *      Network Viewer's set location function.
	 *      </p>
	 */
	@Override
	public void setOffset(double x, double y) {
		if (getElasticWorld() != null) {
			getElasticWorld().setElasticPosition(this, x, y);
		} else {
			super.setOffset(x, y);
		}
	}

	/**
	 * This is the real setOffset function.
	 * 
	 * @param x
	 * @param y
	 */
	public void setOffsetReal(double x, double y) {
		super.setOffset(x, y);
	}

	@Override
	public void setParent(PNode newParent) {
		super.setParent(newParent);
		if (getParent() instanceof ElasticGround) {
			elasticGround = (ElasticGround) getParent();
		}
	}

}

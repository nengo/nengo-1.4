package ca.shu.ui.lib.world.elastic;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ca.shu.ui.lib.world.piccolo.WorldObjectImpl;
import edu.umd.cs.piccolo.PNode;

public class ElasticObject extends WorldObjectImpl {

	private static final long serialVersionUID = 1L;

	// Cache the Elastic world for fast access because it is used often
	private ElasticGround elasticGround;

	protected ElasticGround getElasticWorld() {
		return elasticGround;
	}

	public ElasticObject() {
		super();

		getPiccolo().addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(PNode.PROPERTY_PARENT)) {
					if (getParent() instanceof ElasticGround) {
						elasticGround = (ElasticGround) getParent();
					} else {
//						elasticGround = null;
					}
				}
			}

		});
	}

	@Override
	public Point2D getOffset() {
		if (getElasticWorld() != null) {
			return getElasticWorld().getElasticPosition(this);
		} else {
		}
		return super.getOffset();
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
	protected void prepareForDestroy() {
		// vertex = null;
		super.prepareForDestroy();
	}

	@Override
	public void setSelected(boolean isSelected) {
		super.setSelected(isSelected);
		setPositionLocked(isSelected);
	}

	public boolean isPositionLocked() {
		if (elasticGround != null) {
			return elasticGround.isPositionLocked(this);
		}
		return false;
	}

	/**
	 * Counts the number of times the position has been locked
	 */
	private int positionLock = 0;

	public void setPositionLocked(boolean lock) {
		if (lock) {
			positionLock++;
		} else {
			positionLock--;
			if (positionLock < 0)
				positionLock = 0;
		}

		if (elasticGround != null) {
			if (positionLock > 0) {
				elasticGround.setPositionLocked(this, true);
			} else {
				elasticGround.setPositionLocked(this, false);
			}

		}
	}
}

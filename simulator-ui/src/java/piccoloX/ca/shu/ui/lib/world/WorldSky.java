package ca.shu.ui.lib.world;

import java.awt.geom.Rectangle2D;
import java.util.Collection;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;

public class WorldSky extends PCamera implements IWorldLayer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7467076877836999849L;

	World world;

	public WorldSky(World world) {
		super();
		this.world = world;

	}

	public void addChildW(WorldObject child) {
		throw new NotImplementedException();
	}

	public void addedToWorld() {
		throw new NotImplementedException();
	}

	public void addToWorldLayer(WorldObject wo) {

		addChild((PNode) wo);
	}

	public void addWorldObject(WorldObject node) {
		addChild((PNode) node);
	}

	public void destroy() {
		throw new NotImplementedException();
	}

	public void doubleClicked() {
		throw new NotImplementedException();

	}

	public void endDrag() {
		throw new NotImplementedException();
	}

	public Collection<WorldObject> getChildrenAtBounds(Rectangle2D bounds) {
		throw new NotImplementedException();
	}

	public String getName() {
		throw new NotImplementedException();
	}

	public World getWorld() {
		return world;
	}

	public IWorldLayer getWorldLayer() {
		throw new NotImplementedException();
	}

	public boolean isDraggable() {
		throw new NotImplementedException();
	}

	public void justDropped() {
		throw new NotImplementedException();
	}

	public void removedFromWorld() {
		throw new NotImplementedException();
	}

	public void setDraggable(boolean isDraggable) {
		throw new NotImplementedException();
	}

	public void startDrag() {
		throw new NotImplementedException();
	}

	@Override
	public void translateView(double arg0, double arg1) {
		// TODO Auto-generated method stub
		super.translateView(arg0, arg1);

	}

}

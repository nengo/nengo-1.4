package ca.shu.ui.lib.world.impl;

import java.awt.geom.Rectangle2D;
import java.util.Collection;

import ca.shu.ui.lib.world.IWorld;
import ca.shu.ui.lib.world.IWorldLayer;
import ca.shu.ui.lib.world.IWorldObject;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;

public class WorldSky extends PCamera implements IWorldLayer{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7467076877836999849L;

	IWorld world;

	public void addWorldObject(IWorldObject node) {
		addChild((PNode) node);
	}

	public IWorld getWorld() {
		return world;
	}

	public WorldSky(IWorld world) {
		super();
		this.world = world;
		

	}

	public Collection<PNode> getChildrenAtBounds(Rectangle2D bounds) {
		System.out.println("Unimplemented!!");
		// TODO Auto-generated method stub
		return null;
	}

	public void addToWorldLayer(IWorldObject wo) {
		// TODO Auto-generated method stub
		addChild((PNode) wo);
	}


}

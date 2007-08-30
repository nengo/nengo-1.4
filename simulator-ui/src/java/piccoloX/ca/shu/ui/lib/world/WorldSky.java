package ca.shu.ui.lib.world;

import edu.umd.cs.piccolo.PCamera;

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

	public void addToWorldLayer(WorldObject wo) {

		addChild(wo);
	}

	public void addWorldObject(WorldObject node) {
		addChild(node);
	}

	public World getWorld() {
		return world;
	}

	@Override
	public void translateView(double arg0, double arg1) {
		super.translateView(arg0, arg1);

	}

}

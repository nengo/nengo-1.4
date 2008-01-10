package ca.shu.ui.lib.world;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ca.shu.ui.lib.objects.PEdge;
import edu.umd.cs.piccolo.PCamera;

/**
 * A layer within a world which looks at the ground layer. This layer can also
 * contain world objects, but their positions are static during panning and
 * zooming.
 * 
 * @author Shu Wu
 */
public class WorldSky extends PCamera implements IWorldLayer {

	private static final long serialVersionUID = -7467076877836999849L;

	/**
	 * World this layer belongs to
	 */
	private World world;

	/**
	 * Create a new sky layer
	 * 
	 * @param world
	 *            World this layer belongs to
	 */
	public WorldSky() {
		super();
	}

	public void setWorld(World world) {
		this.world = world;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.shu.ui.lib.world.IWorldLayer#addEdge(ca.shu.ui.lib.objects.DirectedEdge)
	 */
	public void addEdge(PEdge edge) {
		throw new NotImplementedException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.shu.ui.lib.world.IWorldLayer#getWorld()
	 */
	public World getWorld() {
		return world;
	}

}

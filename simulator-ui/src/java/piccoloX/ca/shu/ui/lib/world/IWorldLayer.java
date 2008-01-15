package ca.shu.ui.lib.world;

import java.util.Collection;

import ca.shu.ui.lib.world.piccolo.WorldImpl;
import ca.shu.ui.lib.world.piccolo.objects.Window;

/**
 * A Layer of the world
 * 
 * @author Shu Wu
 */
public interface IWorldLayer extends IWorldObject {
	/**
	 * @param child
	 *            Child node to add
	 */
	public void addChild(IWorldObject child);

	/**
	 * @return World which this layer belongs to
	 */
	public WorldImpl getWorld();

	/**
	 * @return A Collection of windows
	 */
	public Collection<Window> getAllWindows();

	/**
	 * @param world
	 *            The world
	 */
	public void setWorld(WorldImpl world);

	/**
	 * Clears the layer of all children
	 */
	public void clearLayer();

}

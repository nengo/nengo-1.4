package ca.shu.ui.lib.world;


/**
 * @author Shu Wu
 */
public interface Droppable {

	public void justDropped();

	public boolean acceptTarget(WorldObject target);

}

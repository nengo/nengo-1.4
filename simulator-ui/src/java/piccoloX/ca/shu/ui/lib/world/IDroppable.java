package ca.shu.ui.lib.world;


/**
 * @author Shu Wu
 */
public interface IDroppable {

	public void justDropped();

	public boolean acceptTarget(IWorldObject target);

}

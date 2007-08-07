package ca.shu.ui.lib.objects.lines;

import ca.shu.ui.lib.world.WorldObject;

public interface ILineAcceptor extends WorldObject {

	public boolean setLineEnd(LineEnd lineEnd);

	public void removeLineEnd();

}

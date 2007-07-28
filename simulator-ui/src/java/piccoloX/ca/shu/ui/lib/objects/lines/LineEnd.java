package ca.shu.ui.lib.objects.lines;

import java.util.Collection;
import java.util.Iterator;

import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

public class LineEnd extends WorldObjectImpl {
	private static final long serialVersionUID = 1L;

	ILineAcceptor connectedTo;

	public LineEnd() {
		super();

		addChild(new LineEndIcon());
		setBounds(getFullBounds());
		setChildrenPickable(false);
		setTangible(false);

		setDraggable(true);
	}

	public void justDropped() {

		Collection<PNode> nodes = this.getWorldLayer().getChildrenAtBounds(
				localToGlobal(getBounds()));

		boolean foundSomethingToConnectTo = false;

		Iterator<PNode> it = nodes.iterator();
		while (it.hasNext()) {
			PNode node = it.next();

			if (node instanceof ILineAcceptor) {
				System.out.println("**Dropped into line in!");
				if (((ILineAcceptor) node).connect(this)) {
					connectedTo = (ILineAcceptor) node;
					foundSomethingToConnectTo = true;
					break;
				}
			}
		}

		/*
		 * Not connected anymore, disconnect from what it was connected to
		 */
		if (!foundSomethingToConnectTo && connectedTo != null) {
			connectedTo.disconnect();
			connectedTo = null;
		}
	}
}

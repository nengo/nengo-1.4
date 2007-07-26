package ca.shu.ui.lib.objects.lines;

import java.util.Collection;
import java.util.Iterator;

import edu.umd.cs.piccolo.PNode;

public class LineEnd extends LineHolder  {
	private static final long serialVersionUID = 1L;
	
	ILineAcceptor connectedTo;

	public LineEnd() {
		super();

		this.addChild(new LineEndIcon());
		this.setBounds(getFullBounds());
		this.setChildrenPickable(false);
		this.setTangible(false);

		this.setDraggable(true);

		// this.addInputEventListener(new LineEndHandler(this));

	}

	public void justDropped() {
		// IWorldLayer worldLayer = getWorldLayer();

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
